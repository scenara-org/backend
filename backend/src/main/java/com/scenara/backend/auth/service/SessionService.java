package com.scenara.backend.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

/**
 * Sessões de login guardadas no Redis. Cada login gera uma sessão com TTL igual à validade do JWT;
 * o token só é aceito enquanto a sessão existir, o que permite logout e revogação antes da expiração.
 *
 * Chaves:
 *  - session:{sessionId}        -> userId (expira junto com o token)
 *  - user-sessions:{userId}     -> conjunto de sessionIds do usuário (para revogar todas de uma vez)
 */
@Service
public class SessionService {

    private static final String SESSION_PREFIX = "session:";
    private static final String USER_SESSIONS_PREFIX = "user-sessions:";

    private final StringRedisTemplate redis;
    private final Duration ttl;

    public SessionService(StringRedisTemplate redis, @Value("${jwt.expiration}") long expirationMillis) {
        this.redis = redis;
        this.ttl = Duration.ofMillis(expirationMillis);
    }

    public String criar(String userId) {
        String sessionId = UUID.randomUUID().toString();
        String userSessionsKey = USER_SESSIONS_PREFIX + userId;

        redis.opsForValue().set(SESSION_PREFIX + sessionId, userId, ttl);
        redis.opsForSet().add(userSessionsKey, sessionId);
        redis.expire(userSessionsKey, ttl);

        return sessionId;
    }

    public boolean estaAtiva(String sessionId, String userId) {
        if (sessionId == null || userId == null) {
            return false;
        }
        return userId.equals(redis.opsForValue().get(SESSION_PREFIX + sessionId));
    }

    public void revogar(String sessionId) {
        String userId = redis.opsForValue().getAndDelete(SESSION_PREFIX + sessionId);
        if (userId != null) {
            redis.opsForSet().remove(USER_SESSIONS_PREFIX + userId, sessionId);
        }
    }

    public void revogarTodas(String userId) {
        String userSessionsKey = USER_SESSIONS_PREFIX + userId;
        Set<String> sessionIds = redis.opsForSet().members(userSessionsKey);
        if (sessionIds != null && !sessionIds.isEmpty()) {
            redis.delete(sessionIds.stream().map(id -> SESSION_PREFIX + id).toList());
        }
        redis.delete(userSessionsKey);
    }
}
