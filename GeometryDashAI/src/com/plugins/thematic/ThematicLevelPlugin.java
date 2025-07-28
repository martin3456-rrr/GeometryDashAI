package com.plugins.thematic;

import com.Generator.ILevelGenerationModel;
import com.Generator.Pattern;
import com.Generator.PatternLibrary;

import java.util.*;
import java.util.stream.Collectors;

public class ThematicLevelPlugin implements ILevelGenerationModel {
    private final Map<String,List<Pattern>> themes = new LinkedHashMap<>();
    private final Iterator<String> themeCycle;
    private final Random rnd = new Random();

    public ThematicLevelPlugin() {
        themes.put("jump",     Collections.emptyList());
        themes.put("ship",     Collections.emptyList());
        themes.put("ball",     Collections.emptyList());
        themeCycle = themes.keySet().iterator();
    }

    @Override
    public void train(List<Pattern> sequence) {
        themes.put("jump", PatternLibrary.PATTERNS.stream()
                .filter(p -> p.getName().toLowerCase().contains("jump"))
                .collect(Collectors.toList()));
        themes.put("ship", PatternLibrary.PATTERNS.stream()
                .filter(p -> p.getName().toLowerCase().contains("ship"))
                .collect(Collectors.toList()));
        themes.put("ball", PatternLibrary.PATTERNS.stream()
                .filter(p -> p.getName().toLowerCase().contains("ball"))
                .collect(Collectors.toList()));
    }

    @Override
    public Pattern getNextPattern(List<String> currentState) {
        if (!themeCycle.hasNext()) {
            themeCycle.forEachRemaining(t -> {});
            throw new NoSuchElementException();
        }
        String theme = themeCycle.next();
        List<Pattern> list = themes.get(theme);
        if (list == null || list.isEmpty()) {
            return PatternLibrary.getRandomPattern();
        }
        return list.get(rnd.nextInt(list.size()));
    }
}
