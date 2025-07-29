package com.util;

import com.jade.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public final class TeePrintStream extends PrintStream {

    private final PrintStream original;
    private final StringBuilder buffer = new StringBuilder();

    public static void hookSystemStreams() {
        System.setOut(new TeePrintStream(System.out));
        System.setErr(new TeePrintStream(System.err));
    }
    private TeePrintStream(PrintStream original) {
        super(new OutputStream() {
            @Override public void write(int b) throws IOException
            {
                original.write(b);
            }
        }, true);
        this.original = original;
    }
    @Override
    public void println(String x) {
        logAndForward(x);
    }
    @Override
    public void println(Object x){
        logAndForward(String.valueOf(x));
    }
    @Override
    public void print(String s){
        forward(s);
    }
    @Override public void print(Object o){
        forward(String.valueOf(o));
    }
    private void logAndForward(String line) {
        buffer.append(line);
        forward(line + System.lineSeparator());
        Log.add(line);
    }
    private void forward(String txt) {
        original.print(txt);
    }
}
