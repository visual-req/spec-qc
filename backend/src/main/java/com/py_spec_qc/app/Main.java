package com.py_spec_qc.app;

import picocli.CommandLine;

public final class Main {
    public static void main(String[] args) {
        int code = new CommandLine(new RootCommand()).execute(args);
        System.exit(code);
    }
}

