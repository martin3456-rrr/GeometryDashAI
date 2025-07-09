package com.Generator;

import java.util.*;

public class MarkovChain {
    // Klucz: Lista dwóch nazw wzorców reprezentująca stan. Wartość: Mapa możliwych następnych wzorców i ich liczebności.
    private final Map<List<String>, Map<String, Integer>> transitionCounts = new HashMap<>();
    private final Map<List<String>, Integer> stateTotals = new HashMap<>();
    private final Random random = new Random();
    public static final String START_TOKEN = "##START##"; // Unikalny token startowy

    public void train(List<Pattern> sequence) {
        if (sequence.size() < 2) {
            return;
        }

        // Trenuj początek sekwencji
        addTransition(List.of(START_TOKEN, START_TOKEN), sequence.get(0).getName());
        addTransition(List.of(START_TOKEN, sequence.get(0).getName()), sequence.get(1).getName());

        // Trenuj resztę sekwencji
        for (int i = 0; i < sequence.size() - 2; i++) {
            List<String> state = List.of(sequence.get(i).getName(), sequence.get(i + 1).getName());
            String nextPatternName = sequence.get(i + 2).getName();
            addTransition(state, nextPatternName);
        }
    }

    private void addTransition(List<String> state, String nextPatternName) {
        transitionCounts.putIfAbsent(state, new HashMap<>());
        Map<String, Integer> transitions = transitionCounts.get(state);
        transitions.put(nextPatternName, transitions.getOrDefault(nextPatternName, 0) + 1);
        stateTotals.put(state, stateTotals.getOrDefault(state, 0) + 1);
    }

    public Pattern getNextPattern(List<String> currentState) {
        if (!transitionCounts.containsKey(currentState) || stateTotals.getOrDefault(currentState, 0) == 0) {
            // Fallback: spróbuj modelu 1. rzędu (używając tylko ostatniego wzorca)
            List<String> fallbackState = List.of(START_TOKEN, currentState.get(1));
            if (transitionCounts.containsKey(fallbackState) && stateTotals.getOrDefault(fallbackState, 0) > 0) {
                return getPatternFromState(fallbackState);
            }
            // Ostateczny fallback: zwróć losowy wzorzec
            return PatternLibrary.getRandomPattern();
        }
        return getPatternFromState(currentState);
    }

    private Pattern getPatternFromState(List<String> state) {
        Map<String, Integer> transitions = transitionCounts.get(state);
        int total = stateTotals.get(state);
        int randValue = random.nextInt(total);

        int cumulative = 0;
        for (Map.Entry<String, Integer> entry : transitions.entrySet()) {
            cumulative += entry.getValue();
            if (randValue < cumulative) {
                return PatternLibrary.getPatternByName(entry.getKey());
            }
        }
        return PatternLibrary.getRandomPattern(); // Nie powinno być osiągnięte
    }

    public boolean isKnownTransition(List<String> state, String next) {
        return transitionCounts.containsKey(state) && transitionCounts.get(state).containsKey(next);
    }
}