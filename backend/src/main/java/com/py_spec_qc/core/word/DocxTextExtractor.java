package com.py_spec_qc.core.word;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBrType;

public final class DocxTextExtractor {
    private static final Pattern HEADING_PREFIX = Pattern.compile("^\\s*([0-9]+(?:\\.[0-9]+)*)\\s*([、.\\-:：\\s]+)\\s*(.*)\\s*$");

    public String extractDocxText(Path docxPath) {
        if (docxPath == null) {
            return "";
        }
        Path p = docxPath.toAbsolutePath().normalize();
        if (!Files.isRegularFile(p)) {
            return "";
        }

        try (InputStream in = Files.newInputStream(p); XWPFDocument doc = new XWPFDocument(in)) {
            List<String> lines = new ArrayList<>();
            List<XWPFParagraph> paras = doc.getParagraphs();
            for (XWPFParagraph para : paras) {
                String text = para.getText();
                if (text == null) {
                    continue;
                }
                text = text.trim();
                if (text.isEmpty()) {
                    continue;
                }
                lines.add(text);
            }
            return String.join("\n", lines).trim();
        } catch (IOException e) {
            return "";
        }
    }

    public String extractDocxTextWithLocations(Path docxPath) {
        if (docxPath == null) {
            return "";
        }
        Path p = docxPath.toAbsolutePath().normalize();
        if (!Files.isRegularFile(p)) {
            return "";
        }

        try (InputStream in = Files.newInputStream(p); XWPFDocument doc = new XWPFDocument(in)) {
            List<String> lines = new ArrayList<>();
            List<XWPFParagraph> paras = doc.getParagraphs();
            int page = 1;
            String[] sectionByLevel = new String[10];
            for (XWPFParagraph para : paras) {
                String raw = para.getText();
                if (raw == null) {
                    if (hasPageBreak(para)) {
                        page += 1;
                    }
                    continue;
                }
                String text = raw.trim();
                if (text.isEmpty()) {
                    if (hasPageBreak(para)) {
                        page += 1;
                    }
                    continue;
                }

                String headingLevel = guessHeadingLevel(para);
                boolean isHeading = headingLevel != null;
                if (isHeading) {
                    String sec = normalizeSectionFromHeadingText(text);
                    int lv = parseLevel(headingLevel);
                    if (lv <= 0 || lv >= sectionByLevel.length) {
                        lv = 1;
                    }
                    sectionByLevel[lv] = sec;
                    for (int i = lv + 1; i < sectionByLevel.length; i++) {
                        sectionByLevel[i] = null;
                    }
                    String path = buildSectionPath(sectionByLevel);
                    lines.add("[页=" + page + "][章节=" + path + "][标题] " + text);
                } else {
                    String path = buildSectionPath(sectionByLevel);
                    lines.add("[页=" + page + "][章节=" + path + "] " + text);
                }

                if (hasPageBreak(para)) {
                    page += 1;
                }
            }
            return String.join("\n", lines).trim();
        } catch (IOException e) {
            return "";
        }
    }

    private static String guessHeadingLevel(XWPFParagraph para) {
        String style = para.getStyle();
        if (style == null) {
            return null;
        }
        String s = style.trim();
        if (s.isEmpty()) {
            return null;
        }
        String compact = s.replace(" ", "").replace("_", "");
        String lower = compact.toLowerCase();
        if (lower.startsWith("heading")) {
            String tail = compact.substring(7).trim();
            String digits = onlyDigits(tail);
            return digits.isEmpty() ? null : digits;
        }
        if (compact.startsWith("标题")) {
            String tail = compact.substring(2).trim();
            String digits = onlyDigits(tail);
            return digits.isEmpty() ? null : digits;
        }
        return null;
    }

    private static String normalizeSectionFromHeadingText(String headingText) {
        String t = headingText == null ? "" : headingText.trim();
        if (t.isEmpty()) {
            return "";
        }
        Matcher m = HEADING_PREFIX.matcher(t);
        if (!m.matches()) {
            return t;
        }
        String num = m.group(1) == null ? "" : m.group(1).trim();
        String title = m.group(3) == null ? "" : m.group(3).trim();
        if (title.isEmpty()) {
            return num;
        }
        return num + " " + title;
    }

    private static int parseLevel(String s) {
        if (s == null || s.isBlank()) {
            return -1;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static String buildSectionPath(String[] sectionByLevel) {
        if (sectionByLevel == null) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        for (int i = 1; i < sectionByLevel.length; i++) {
            String s = sectionByLevel[i];
            if (s != null && !s.trim().isEmpty()) {
                parts.add(s.trim());
            }
        }
        return String.join(" > ", parts);
    }

    private static boolean hasPageBreak(XWPFParagraph para) {
        if (para == null) {
            return false;
        }
        List<XWPFRun> runs = para.getRuns();
        if (runs != null) {
            for (XWPFRun r : runs) {
                if (r == null) {
                    continue;
                }
                CTR ctr = r.getCTR();
                if (ctr == null) {
                    continue;
                }
                List<CTBr> brs = ctr.getBrList();
                if (brs != null) {
                    for (CTBr br : brs) {
                        if (br != null && br.isSetType() && br.getType() == STBrType.PAGE) {
                            return true;
                        }
                    }
                }
                if (ctr.sizeOfLastRenderedPageBreakArray() > 0) {
                    return true;
                }
            }
        }
        try {
            CTPPr ppr = para.getCTP() == null ? null : para.getCTP().getPPr();
            if (ppr != null && ppr.isSetSectPr()) {
                return true;
            }
        } catch (Exception ignored) {
        }
        return false;
    }

    private static String onlyDigits(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
