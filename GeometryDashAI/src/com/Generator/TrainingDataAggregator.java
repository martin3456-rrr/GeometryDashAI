package com.Generator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class TrainingDataAggregator {

    public static List<Pattern> loadAllLevelsFromDirectory(String path) {
        System.out.println("Wczytywanie danych treningowych z: " + path);
        List<Pattern> allPatterns = new ArrayList<>();
        File dir = new File(path);
        File[] levelFiles = dir.listFiles((d, name) -> name.endsWith(".json"));

        if (levelFiles == null) {
            System.err.println("Nie znaleziono plików z poziomami w katalogu.");
            return allPatterns;
        }

        for (File file : levelFiles) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(file.toURI())));
                JSONObject levelJson = new JSONObject(content);
                JSONArray patternNames = levelJson.getJSONArray("patterns");

                for (int i = 0; i < patternNames.length(); i++) {
                    String name = patternNames.getString(i);
                    Pattern pattern = PatternLibrary.getPatternByName(name);
                    if (pattern != null) {
                        allPatterns.add(pattern);
                    }
                }
            } catch (Exception e) {
                System.err.println("Błąd wczytywania pliku poziomu: " + file.getName());
            }
        }
        System.out.println("Wczytano łącznie " + allPatterns.size() + " wzorców z " + levelFiles.length + " poziomów.");
        return allPatterns;
    }
}