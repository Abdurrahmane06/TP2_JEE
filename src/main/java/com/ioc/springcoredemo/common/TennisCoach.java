package com.ioc.springcoredemo.common;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * Exercice 5.2 : troisième implémentation de Coach → contribue à l'ambiguïté.
 * Exercice 5.5 : @Lazy → le constructeur NE s'affiche PAS au démarrage si TennisCoach
 *                n'est pas injecté ailleurs. Il n'est instancié qu'au premier usage.
 */
@Component
@Lazy
public class TennisCoach implements Coach {

    // -- Exercice 5.5 : trace de création --
    public TennisCoach() {
        System.out.println(">> Constructeur : " + getClass().getSimpleName());
    }

    @Override
    public String getDailyWorkout() {
        return "Practice your backhand volley";
    }
}
