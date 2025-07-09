package com.manager;

import com.util.Sound;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static Map<String, Sound> sounds = new HashMap<>();

    public static void addSound(String name, String filepath) {
        if (!sounds.containsKey(name)) {
            sounds.put(name, new Sound(filepath));
        }
    }

    public static Sound getSound(String name) {
        return sounds.get(name);
    }

    public static void play(String name) {
        Sound sound = getSound(name);
        if (sound != null) {
            sound.play();
        }
    }

    public static void loop(String name) {
        Sound sound = getSound(name);
        if (sound != null) {
            sound.loop();
        }
    }
}