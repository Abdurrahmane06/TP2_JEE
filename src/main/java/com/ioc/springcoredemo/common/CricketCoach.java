package com.ioc.springcoredemo.common;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

/**
 * Exercice 5.1  : bean @Component, injection par constructeur.
 * Exercice 5.5  : trace constructeur pour observer l'initialisation eager/lazy.
 * Exercice 5.6  : scope singleton par défaut → /check retourne true.
 * Exercice 5.7  : cycle de vie avec @PostConstruct / @PreDestroy (Jakarta, pas javax).
 */
@Component
public class CricketCoach implements Coach {

    // -- Exercice 5.5 : trace de création --
    public CricketCoach() {
        System.out.println(">> Constructeur : " + getClass().getSimpleName());
    }

    // -- Exercice 5.7 : cycle de vie --
    @PostConstruct
    public void doMyStartupStuff() {
        System.out.println("In doMyStartupStuff()");
    }

    @PreDestroy
    public void doMyCleanupStuff() {
        System.out.println("In doMyCleanupStuff()");
    }

    @Override
    public String getDailyWorkout() {
        return "Practice fast bowling for 15 minutes";
    }
}
