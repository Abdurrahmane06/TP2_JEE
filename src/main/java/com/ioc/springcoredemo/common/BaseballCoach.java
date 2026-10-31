package com.ioc.springcoredemo.common;

import org.springframework.stereotype.Component;

/**
 * Exercice 5.2 : deuxième implémentation de Coach → provoque NoUniqueBeanDefinitionException.
 * Exercice 5.5 : trace constructeur pour observer l'initialisation eager.
 */
@Component
public class BaseballCoach implements Coach {

    // -- Exercice 5.5 : trace de création --
    public BaseballCoach() {
        System.out.println(">> Constructeur : " + getClass().getSimpleName());
    }

    @Override
    public String getDailyWorkout() {
        return "Spend 30 minutes on batting practice";
    }
}
