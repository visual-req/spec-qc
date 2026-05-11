package com.py_spec_qc.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class ScanRequest {
    @JsonProperty("req_dir")
    public String reqDir;

    @JsonProperty("out_dir")
    public String outDir;

    @JsonProperty("rules_dir")
    public String rulesDir;
}

