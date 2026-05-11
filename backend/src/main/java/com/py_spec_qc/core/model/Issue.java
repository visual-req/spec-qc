package com.py_spec_qc.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Issue {
    @JsonProperty("seq")
    public String seq;

    @JsonProperty("severity")
    public String severity;

    @JsonProperty("category")
    public String category;

    @JsonProperty("description")
    public String description;

    @JsonProperty("evidence_page")
    public String evidencePage;

    @JsonProperty("evidence_section")
    public String evidenceSection;

    @JsonProperty("evidence_excerpt")
    public String evidenceExcerpt;

    @JsonProperty("evidence_paragraph")
    public String evidenceParagraph;

    @JsonProperty("suggestion")
    public String suggestion;

    @JsonProperty("suggestion_html")
    public String suggestionHtml;

    @JsonProperty("review_status")
    public String reviewStatus;

    @JsonProperty("review_updated_at")
    public String reviewUpdatedAt;

    @JsonProperty("related_standard")
    public String relatedStandard;
}
