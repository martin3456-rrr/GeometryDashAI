package com.jade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Log {
    private static final List<String> messages = Collections.synchronizedList(new ArrayList<>());
    private static final int MAX_MESSAGES = 1000;

    public static void add(String message) {
        synchronized (messages) {
            if (messages.size() > MAX_MESSAGES) {
                messages.removeFirst();
            }
            messages.add(message);
        }
    }

    public static List<String> getMessages() {
        synchronized (messages) {
            return new ArrayList<>(messages);
        }
    }

    public static void clear() {
        messages.clear();
    }
}