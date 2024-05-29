package com.example.instashare.Model;

import java.util.Random;

public class RandomUtils {
    /**
     * Generates the random between two given integers.
     */
    public static int generateRandomBetween(int start, int end) {

        Random random = new Random();
        int rand = random.nextInt(Integer.MAX_VALUE - 1) % end;

        if (rand < start) {
            rand = start;
        }
        return rand;
    }
}
