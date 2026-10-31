package com.ioc.springcoredemo.common;

/**
 * Exercice 5.8 : classe tierce (non annotée @Component) — simule une dépendance
 * externe non modifiable. Elle est enregistrée comme bean via @Configuration + @Bean
 * dans SportConfig, avec l'id explicite "aquatic".
 */
public class SwimCoach implements Coach {

    public SwimCoach() {
        System.out.println(">> Constructeur : " + getClass().getSimpleName());
    }

    @Override
    public String getDailyWorkout() {
        return "Swim 1000 meters as a warm up";
    }
}
