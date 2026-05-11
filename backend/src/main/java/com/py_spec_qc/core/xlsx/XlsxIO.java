package com.py_spec_qc.core.xlsx;

import com.py_spec_qc.core.model.Issue;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

public final class XlsxIO {
    public void writeError(Path path, String fileName, String errorMsg) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("文件名", "状态", "错误信息"));
        rows.add(List.of(nullToEmpty(fileName), "失败", nullToEmpty(errorMsg)));
        writeSheet(path, "quality", rows);
    }

    public void writeIssues(Path path, String fileName, List<Issue> issues) {
        List<List<String>> rows = new ArrayList<>();
        rows.add(List.of("文件名", "序号", "严重性", "问题分类", "问题描述", "页号", "章节编号", "内容摘抄", "解决建议", "关联质量标准"));
        if (issues == null || issues.isEmpty()) {
            rows.add(List.of(nullToEmpty(fileName), "0", "", "", "未发现问题", "", "", "", "", ""));
        } else {
            int idx = 0;
            for (Issue it : issues) {
                if (it == null) {
                    continue;
                }
                idx += 1;
                rows.add(List.of(
                        nullToEmpty(fileName),
                        String.valueOf(idx),
                        nullToEmpty(it.severity),
                        nullToEmpty(it.category),
                        nullToEmpty(it.description),
                        nullToEmpty(it.evidencePage),
                        nullToEmpty(it.evidenceSection),
                        nullToEmpty((it.evidenceParagraph == null || it.evidenceParagraph.isBlank()) ? it.evidenceExcerpt : it.evidenceParagraph),
                        nullToEmpty(it.suggestion),
                        nullToEmpty(it.relatedStandard)
                ));
            }
        }
        writeSheet(path, "quality", rows);
    }

    public List<Issue> readIssues(Path xlsxPath) {
        Path p = xlsxPath.toAbsolutePath().normalize();
        if (!Files.isRegularFile(p) || !p.getFileName().toString().toLowerCase().endsWith(".xlsx")) {
            throw new IllegalArgumentException("Invalid xlsx path: " + p);
        }
        try (InputStream in = Files.newInputStream(p); XSSFWorkbook wb = new XSSFWorkbook(in)) {
            Sheet sheet = wb.getNumberOfSheets() > 0 ? wb.getSheetAt(0) : null;
            if (sheet == null) {
                return List.of();
            }
            List<Issue> issues = new ArrayList<>();
            boolean isFirst = true;
            for (Row row : sheet) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                String seq = cellToString(row.getCell(1));
                String desc = cellToString(row.getCell(4));
                if ("0".equals(seq.trim()) && "未发现问题".equals(desc.trim())) {
                    continue;
                }
                Issue it = new Issue();
                it.seq = seq;
                it.severity = cellToString(row.getCell(2));
                it.category = cellToString(row.getCell(3));
                it.description = desc;
                it.evidencePage = cellToString(row.getCell(5));
                it.evidenceSection = cellToString(row.getCell(6));
                it.evidenceParagraph = cellToString(row.getCell(7));
                it.evidenceExcerpt = "";
                it.suggestion = cellToString(row.getCell(8));
                it.relatedStandard = cellToString(row.getCell(9));
                issues.add(it);
            }
            return issues;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read xlsx: " + p, e);
        }
    }

    private static void writeSheet(Path path, String sheetName, List<List<String>> rows) {
        Path p = path.toAbsolutePath().normalize();
        try {
            Files.createDirectories(p.getParent());
        } catch (IOException e) {
            throw new RuntimeException("Operation not permitted: '" + p + "'", e);
        }
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet(sheetName);
            XSSFCellStyle headerStyle = createHeaderStyle(wb);
            XSSFCellStyle bodyStyle = createBodyStyle(wb);
            int r = 0;
            for (List<String> row : rows) {
                Row rr = sheet.createRow(r++);
                if (r == 1) {
                    rr.setHeightInPoints(22f);
                } else {
                    rr.setHeightInPoints(20f);
                }
                int c = 0;
                for (String v : row) {
                    Cell cell = rr.createCell(c++, CellType.STRING);
                    cell.setCellValue(nullToEmpty(v));
                    cell.setCellStyle(r == 1 ? headerStyle : bodyStyle);
                }
            }
            int cols = rows.isEmpty() ? 1 : rows.get(0).size();
            for (int i = 0; i < cols; i++) {
                sheet.autoSizeColumn(i);
                int w = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.min(w + 512, 256 * 60));
            }
            try (OutputStream out = Files.newOutputStream(p)) {
                wb.write(out);
            }
        } catch (IOException e) {
            throw new RuntimeException("Operation not permitted: '" + p + "'", e);
        }
    }

    private static XSSFCellStyle createHeaderStyle(XSSFWorkbook wb) {
        XSSFCellStyle st = wb.createCellStyle();
        st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        st.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        st.setVerticalAlignment(VerticalAlignment.CENTER);
        st.setBorderBottom(BorderStyle.THIN);
        st.setBorderTop(BorderStyle.THIN);
        st.setBorderLeft(BorderStyle.THIN);
        st.setBorderRight(BorderStyle.THIN);
        Font f = wb.createFont();
        f.setBold(true);
        f.setColor(IndexedColors.WHITE.getIndex());
        st.setFont(f);
        return st;
    }

    private static XSSFCellStyle createBodyStyle(XSSFWorkbook wb) {
        XSSFCellStyle st = wb.createCellStyle();
        st.setWrapText(true);
        st.setVerticalAlignment(VerticalAlignment.TOP);
        st.setBorderBottom(BorderStyle.THIN);
        st.setBorderTop(BorderStyle.THIN);
        st.setBorderLeft(BorderStyle.THIN);
        st.setBorderRight(BorderStyle.THIN);
        return st;
    }

    private static String cellToString(Cell cell) {
        if (cell == null) {
            return "";
        }
        return switch (cell.getCellType()) {
            case STRING -> nullToEmpty(cell.getStringCellValue());
            case NUMERIC -> {
                double v = cell.getNumericCellValue();
                if (Math.floor(v) == v) {
                    yield String.valueOf((long) v);
                }
                yield String.valueOf(v);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> nullToEmpty(cell.getCellFormula());
            case BLANK, _NONE, ERROR -> "";
        };
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
