package com.ioc.springcoredemo.rest;

import com.ioc.springcoredemo.common.Coach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur REST — état Capture 4 (ex 5.6) : singleton vs prototype via /check.
 */
@RestController
public class DemoController {

    private final Coach myCoach;
    private final Coach checkCoach1;
    private final Coach checkCoach2;

    @Autowired
    public DemoController(
            @Qualifier("aquatic")      Coach theCoach,
            @Qualifier("cricketCoach") Coach theCheckCoach1,
            @Qualifier("cricketCoach") Coach theCheckCoach2) {
        myCoach     = theCoach;
        checkCoach1 = theCheckCoach1;
        checkCoach2 = theCheckCoach2;
    }

    @GetMapping("/dailyworkout")
    public String getDailyWorkout() {
        return myCoach.getDailyWorkout();
    }

    @GetMapping("/check")
    public String check() {
        return "Comparing beans: checkCoach1 == checkCoach2, " + (checkCoach1 == checkCoach2);
    }
}