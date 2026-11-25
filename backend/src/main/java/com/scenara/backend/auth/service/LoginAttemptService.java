package com.scenara.backend.auth.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Limita tentativas de login com contadores no Redis (INCR + EXPIRE), por e-mail e por IP.
 * Cada contador vive numa janela de 15 minutos que começa na primeira falha.
 *
 * Chaves:
 *  - login-attempts:email:{email} -> número de falhas para o e-mail
 *  - login-attempts:ip:{ip}       -> número de falhas vindas do IP (cobre várias contas atacadas do mesmo lugar)
 */
@Service
public class LoginAttemptService {

    static final int MAX_FALHAS_POR_EMAIL = 5;
    static final int MAX_FALHAS_POR_IP = 20;
    static final Duration JANELA = Duration.ofMinutes(15);

    private static final String EMAIL_PREFIX = "login-attempts:email:";
    private static final String IP_PREFIX = "login-attempts:ip:";

    private final StringRedisTemplate redis;

    public LoginAttemptService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /** Lança 429 se o e-mail ou o IP já atingiram o limite de falhas na janela atual. */
    public void verificarBloqueio(String email, String ip) {
        verificar(emailKey(email), MAX_FALHAS_POR_EMAIL);
        verificar(ipKey(ip), MAX_FALHAS_POR_IP);
    }

    public void registrarFalha(String email, String ip) {
        incrementar(emailKey(email));
        incrementar(ipKey(ip));
    }

    /** Login certo zera as falhas do e-mail. As do IP continuam, para não "lavar" um ataque com uma conta própria. */
    public void registrarSucesso(String email) {
        redis.delete(emailKey(email));
    }

    private void verificar(String key, int limite) {
        String valor = redis.opsForValue().get(key);
        if (valor == null || Long.parseLong(valor) < limite) {
            return;
        }
        Long segundos = redis.getExpire(key, TimeUnit.SECONDS);
        long minutos = Math.max(1, (segundos == null || segundos < 0 ? JANELA.toSeconds() : segundos + 59) / 60);
        throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                "Muitas tentativas de login. Tente novamente em " + minutos + (minutos == 1 ? " minuto." : " minutos."));
    }

    private void incrementar(String key) {
        Long total = redis.opsForValue().increment(key);
        // A janela começa na primeira falha. O segundo teste garante a expiração caso um EXPIRE anterior tenha se perdido.
        if (total != null && (total == 1 || redis.getExpire(key) == -1)) {
            redis.expire(key, JANELA);
        }
    }

    private static String emailKey(String email) {
        return EMAIL_PREFIX + email.trim().toLowerCase(Locale.ROOT);
    }

    private static String ipKey(String ip) {
        return IP_PREFIX + ip;
    }
}
