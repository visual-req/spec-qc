package com.py_spec_qc.app;

import com.py_spec_qc.core.QualityScanner;
import java.nio.file.Path;
import java.util.List;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "scan", description = "Scan requirement Word files for quality issues")
public final class ScanCommand implements Runnable {
    @Option(names = "-req", required = true, description = "Requirement files directory")
    private Path reqDir;

    @Option(names = "--out", required = false, description = "Output directory (optional)")
    private Path outDir;

    @Option(names = "--rules", required = false, description = "Rules directory (optional)")
    private Path rulesDir;

    @Override
    public void run() {
        try {
            List<Path> outputs = new QualityScanner().scanReqDirPaths(reqDir, outDir, rulesDir, null);
            for (Path p : outputs) {
                System.out.println(p.toAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() == null ? String.valueOf(e) : e.getMessage());
            System.exit(2);
        }
    }
}

