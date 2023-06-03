package com.example.wavezcellular.utils;

import java.util.Random;

public class NameGenerator {
    private static final String[] FIRST_NAMES = {
            "John", "Emma", "Michael", "Sophia", "William", "Olivia",
            "James", "Ava", "Robert", "Isabella", "David", "Mia"
            // Add more first names as needed
    };

    private static final String[] LAST_NAMES = {
            "Smith", "Johnson", "Brown", "Taylor", "Miller", "Wilson",
            "Anderson", "Thomas", "Clark", "White", "Hall", "Walker"
            // Add more last names as needed
    };

    private static final Random random = new Random();

    public static String generateRandomName() {
        String firstName = getRandomElement(FIRST_NAMES);
        String lastName = getRandomElement(LAST_NAMES);

        return firstName + " " + lastName;
    }

    private static <T> T getRandomElement(T[] array) {
        int index = random.nextInt(array.length);
        return array[index];
    }
}