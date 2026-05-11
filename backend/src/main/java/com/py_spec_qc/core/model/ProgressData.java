package com.py_spec_qc.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public final class ProgressData {
    @JsonProperty("total_files")
    public int totalFiles;

    @JsonProperty("scanned_files")
    public int scannedFiles;

    @JsonProperty("files")
    public List<FileProgress> files = new ArrayList<>();

    @JsonProperty("status")
    public String status;
}

