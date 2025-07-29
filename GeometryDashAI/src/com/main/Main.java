package com.main;

import com.jade.Window;

public class Main {
    public static void main(String[] args) {
        // Wyłączenie logów ND4J/DeepLearning4J
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");
        System.setProperty("org.slf4j.simpleLogger.log.org.nd4j", "off");
        System.setProperty("org.slf4j.simpleLogger.log.org.deeplearning4j", "off");

        com.util.TeePrintStream.hookSystemStreams();

        Window window = Window.getWindow();
        window.init();

        Thread mainThread = new Thread(window);
        mainThread.start();
    }
}