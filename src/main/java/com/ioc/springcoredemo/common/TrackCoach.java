package com.ioc.springcoredemo.common;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Exercice 5.2 : quatrième implémentation de Coach → contribue à l'ambiguïté.
 * Exercice 5.4 : @Primary → choisi par défaut quand aucun @Qualifier n'est présent.
 *                Règle d'or : @Qualifier est PRIORITAIRE sur @Primary.
 * Exercice 5.5 : trace constructeur pour observer l'initialisation eager.
 */
@Component
@Primary
public class TrackCoach implements Coach {

    // -- Exercice 5.5 : trace de création --
    public TrackCoach() {
        System.out.println(">> Constructeur : " + getClass().getSimpleName());
    }

    @Override
    public String getDailyWorkout() {
        return "Run a hard 5k!";
    }
}
