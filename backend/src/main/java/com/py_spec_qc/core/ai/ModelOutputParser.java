package com.py_spec_qc.core.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ModelOutputParser {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Pattern JSON_OBJECT = Pattern.compile("\\{[\\s\\S]*\\}");

    public JsonNode parseJsonObject(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Model output is null");
        }
        String t = text.trim();
        try {
            JsonNode node = MAPPER.readTree(t);
            if (!node.isObject()) {
                throw new IllegalArgumentException("Model output must be a JSON object");
            }
            return node;
        } catch (IOException ignored) {
        }

        Matcher m = JSON_OBJECT.matcher(t);
        String objText = null;
        if (m.find()) {
            objText = m.group(0);
        } else {
            int idx = t.indexOf('{');
            if (idx >= 0) {
                objText = t.substring(idx);
            }
        }
        if (objText == null || objText.isBlank()) {
            throw new IllegalArgumentException("Model output is not valid JSON");
        }

        for (int attempt = 0; attempt < 6; attempt++) {
            String candidate = attempt == 0 ? objText : truncateTailToPreviousObjectEnd(objText, attempt);
            if (candidate == null || candidate.isBlank()) {
                continue;
            }
            String repaired = repairTruncatedJson(candidate);
            try {
                JsonNode node = MAPPER.readTree(repaired);
                if (!node.isObject()) {
                    throw new IllegalArgumentException("Model output must be a JSON object");
                }
                return node;
            } catch (IOException ignored2) {
            }
        }
        throw new IllegalArgumentException("Model output is not valid JSON");
    }

    private static String truncateTailToPreviousObjectEnd(String s, int stepsBack) {
        if (s == null) {
            return "";
        }
        String t = s;
        int back = Math.max(1, stepsBack);
        for (int i = 0; i < back; i++) {
            int lastBrace = t.lastIndexOf('}');
            if (lastBrace < 0) {
                return "";
            }
            t = t.substring(0, lastBrace + 1);
        }
        return t;
    }

    private static String repairTruncatedJson(String s) {
        if (s == null) {
            return "";
        }
        String t = s.trim();
        if (t.isBlank()) {
            return "";
        }

        StringBuilder out = new StringBuilder(t.length() + 128);
        Deque<Character> stack = new ArrayDeque<>();
        boolean inString = false;
        boolean escape = false;
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            out.append(c);
            if (escape) {
                escape = false;
                continue;
            }
            if (inString) {
                if (c == '\\') {
                    escape = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }
            if (c == '"') {
                inString = true;
                continue;
            }
            if (c == '{') {
                stack.push('}');
            } else if (c == '[') {
                stack.push(']');
            } else if (c == '}' || c == ']') {
                if (!stack.isEmpty() && stack.peek() == c) {
                    stack.pop();
                }
            }
        }

        if (inString) {
            out.append('"');
        }
        trimTrailingCommaAndSpaces(out);
        while (!stack.isEmpty()) {
            out.append(stack.pop());
            trimTrailingCommaAndSpaces(out);
        }
        return out.toString();
    }

    private static void trimTrailingCommaAndSpaces(StringBuilder sb) {
        if (sb == null) {
            return;
        }
        int i = sb.length() - 1;
        while (i >= 0) {
            char c = sb.charAt(i);
            if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
                i--;
                continue;
            }
            break;
        }
        if (i >= 0 && sb.charAt(i) == ',') {
            sb.deleteCharAt(i);
        }
    }
}
