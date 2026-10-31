package com.ioc.springcoredemo.config;

import com.ioc.springcoredemo.common.Coach;
import com.ioc.springcoredemo.common.SwimCoach;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Exercice 5.8 : déclarer un bean à partir d'une classe non annotée @Component.
 *
 * Cas d'usage typiques où @Bean est préféré à @Component :
 *  1. Classe tierce non modifiable (pas accès au source).
 *  2. Création complexe / via factory method.
 *  3. Plusieurs instances du même type configurées différemment.
 */
@Configuration
public class SportConfig {

    /**
     * Produit un bean de type Coach (SwimCoach) avec l'id explicite "aquatic".
     * L'id "aquatic" sera utilisé côté contrôleur via @Qualifier("aquatic").
     */
    @Bean("aquatic")
    public Coach swimCoach() {
        return new SwimCoach();
    }
}
