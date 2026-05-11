package com.py_spec_qc.app;

import picocli.CommandLine.Command;

@Command(
        name = "py-spec-qc",
        mixinStandardHelpOptions = true,
        subcommands = {ScanCommand.class, WebCommand.class}
)
public final class RootCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("请使用: py-spec-qc scan -req <需求文件目录>");
    }
}

