package com.main;

import com.jade.Window;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
       Window window = Window.getWindow();
       window.init();

       Thread mainThread = new Thread(window);
       mainThread.start();
    }
}