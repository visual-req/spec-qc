package com.py_spec_qc.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public final class FileProgress {
    @JsonProperty("file_name")
    public String fileName;

    @JsonProperty("status")
    public String status;

    @JsonProperty("started_at")
    public String startedAt;

    @JsonProperty("ended_at")
    public String endedAt;

    @JsonProperty("duration_ms")
    public Long durationMs;

    @JsonProperty("rule_count")
    public Integer ruleCount;

    @JsonProperty("issue_count")
    public Integer issueCount;

    @JsonProperty("output_path")
    public String outputPath;

    @JsonProperty("issues")
    public List<Issue> issues = new ArrayList<>();
}

