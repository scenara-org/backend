package com.scenara.backend.shared.cache;

/** Nomes dos caches guardados no Redis (prefixo das chaves: "{nome}::{chave}"). */
public final class CacheNames {

    /** Simulação completa, chave "{projectId}:{simulationId}". Simulações são imutáveis. */
    public static final String SIMULATION = "simulation";

    /** Resumos das simulações de um projeto, chave "{projectId}". */
    public static final String SIMULATIONS_BY_PROJECT = "simulations-by-project";

    /** Resumos das simulações de um cenário, chave "{projectId}:{scenarioId}". */
    public static final String SIMULATIONS_BY_SCENARIO = "simulations-by-scenario";

    /** Dados do usuário (/users/me), chave "{userId}". */
    public static final String USER = "user";

    private CacheNames() {
    }
}
