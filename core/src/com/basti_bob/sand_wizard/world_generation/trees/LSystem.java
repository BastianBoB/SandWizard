package com.basti_bob.sand_wizard.world_generation.trees;

public class LSystem {

    public static String generateLSystem(String rule, int iterations) {
        String lSystem = rule;

        for (int i = 0; i < iterations; i++) {

            StringBuilder next = new StringBuilder();

            for (char c : lSystem.toCharArray()) {
                if (c == 'F')
                    next.append(rule);
                else
                    next.append(c);
            }

            lSystem = next.toString();
        }

        return lSystem;
    }
}
