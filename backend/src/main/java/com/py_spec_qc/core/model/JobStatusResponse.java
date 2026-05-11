package com.py_spec_qc.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public final class JobStatusResponse {
    @JsonProperty("status")
    public String status;

    @JsonProperty("message")
    public String message;

    @JsonProperty("error")
    public String error;

    @JsonProperty("outputs")
    public List<String> outputs = new ArrayList<>();

    @JsonProperty("logs")
    public List<String> logs = new ArrayList<>();

    @JsonProperty("progress")
    public ProgressData progress;
}
