package com.py_spec_qc.app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import java.awt.Color;
import java.awt.image.BufferedImage;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;

public final class DocxFromMarkdown {
    private static final Pattern HEADING = Pattern.compile("^(#{1,6})\\s*(.+?)\\s*$");
    private static final Pattern HR = Pattern.compile("^\\s*([-*_])\\1\\1+\\s*$");
    private static final Pattern IMAGE = Pattern.compile("!\\[[^\\]]*\\]\\(([^)]+)\\)");
    private static final Pattern BOLD = Pattern.compile("\\*\\*(.+?)\\*\\*");
    private static final Pattern INLINE_CODE = Pattern.compile("`([^`]+)`");
    private static final HttpClient HTTP = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
    private static final Pattern SECTION_7 = Pattern.compile("^\\s*7\\.(\\d)\\b");
    private static final Pattern CHAPTER_ZH = Pattern.compile("^\\s*([一二三四五六七八九十]+)[、\\.．\\s]+(.+)$");
    private static final Pattern CHAPTER_AR = Pattern.compile("^\\s*(\\d+)[、\\.．\\s]+(.+)$");
    private static final Pattern CHAPTER_CN = Pattern.compile("^\\s*第\\s*(\\d+)\\s*章\\s*(.+)$");

    public static void main(String[] args) throws Exception {
        if (args == null || args.length < 2) {
            System.err.println("Usage: DocxFromMarkdown <input_md> <output_docx>");
            System.exit(2);
        }
        Path input = Path.of(args[0]).toAbsolutePath().normalize();
        Path output = Path.of(args[1]).toAbsolutePath().normalize();
        Path baseDir = input.getParent() == null ? Path.of("").toAbsolutePath().normalize() : input.getParent().toAbsolutePath().normalize();

        String md = Files.readString(input, StandardCharsets.UTF_8);
        List<String> lines = splitLines(md);

        try (XWPFDocument doc = new XWPFDocument()) {
            addTitle(doc, "SpecQC需求质量检查工具");

            boolean insertedWork = false;
            boolean insertedScan = false;
            boolean insertedUiConfig = false;
            boolean insertedUiProgress = false;
            boolean insertedUiReport = false;
            boolean inSection71 = false;
            int currentChapter = 0;
            int chapter3Sub = 0;
            boolean insertedChapter3Tail = false;
            boolean capturingRuleExt = false;
            List<String> ruleExtSnippets = new ArrayList<>();
            List<String> tuningSnippets = new ArrayList<>();

            int i = 0;
            boolean inCode = false;
            String codeLang = "";
            List<String> codeBuf = new ArrayList<>();
            while (i < lines.size()) {
                String raw = lines.get(i);
                String line = raw == null ? "" : raw;

                if (line.trim().startsWith("```")) {
                    if (!inCode) {
                        inCode = true;
                        codeLang = line.trim().substring(3).trim();
                        codeBuf.clear();
                    } else {
                        if ("mermaid".equalsIgnoreCase(codeLang == null ? "" : codeLang.trim())) {
                            if (!addMermaidBlock(doc, codeBuf)) {
                                addCodeBlock(doc, codeLang, codeBuf);
                            }
                        } else {
                            addCodeBlock(doc, codeLang, codeBuf);
                        }
                        inCode = false;
                        codeLang = "";
                        codeBuf.clear();
                    }
                    i++;
                    continue;
                }

                if (inCode) {
                    codeBuf.add(line);
                    i++;
                    continue;
                }

                if (line.trim().isEmpty()) {
                    i++;
                    continue;
                }

                Matcher hm = HEADING.matcher(line.trim());
                if (hm.matches()) {
                    int level = hm.group(1).length();
                    String text = hm.group(2);
                    int nextChapter = chapterNumberFromHeading(text);
                    if (nextChapter > 0 && level <= 2) {
                        if (currentChapter == 3 && !insertedChapter3Tail && nextChapter > 3) {
                            chapter3Sub += 1;
                            addHeading(doc, 3, "3." + chapter3Sub + " 避免扫描幻觉与升级扫描规则");
                            addParagraph(doc, "为降低误报/漏报并避免“扫描幻觉”，建议在规则与流程上做以下约束与迭代：");
                            addBullet(doc, "结构化输出：强制 JSON schema（字段必填、枚举值），并在解析端做严格校验；不合规则重试或降级处理。");
                            addBullet(doc, "证据驱动：每条问题必须给出原文片段与定位（章节/段落/页码）；无证据的问题视为不可靠提示。");
                            addBullet(doc, "阈值与去重：对相似问题做聚合去重，必要时引入置信度/规则匹配强度阈值，降低噪声。");
                            addBullet(doc, "抽样复核闭环：对高频误报点建立“拒绝原因 → 规则调整”闭环，持续收敛提示词与规则表述。");
                            addBullet(doc, "规则版本化：规则文件按版本/行业拆分，新增或调整规则后对历史样本文档回归验证。");
                            insertedChapter3Tail = true;
                        }
                        currentChapter = nextChapter;
                        chapter3Sub = (currentChapter == 3) ? 0 : chapter3Sub;
                        addHeading(doc, 1, formatChapterTitle(nextChapter, text));
                        String sec = section7Key(text);
                        if ("7.1".equals(sec)) {
                            inSection71 = true;
                        } else if ("7.2".equals(sec) || "7.3".equals(sec)) {
                            inSection71 = false;
                        }
                        capturingRuleExt = currentChapter == 3 && (text.contains("规则的可扩展能力") || text.contains("规则扩充"));
                        i++;
                        continue;
                    }

                    if (currentChapter == 3 && level == 3) {
                        chapter3Sub += 1;
                        addHeading(doc, 3, "3." + chapter3Sub + " " + text);
                        capturingRuleExt = text.contains("规则的可扩展能力") || text.contains("规则扩充能力");
                        i++;
                        continue;
                    }

                    addHeading(doc, level, text);

                    String sec = section7Key(text);
                    if ("7.1".equals(sec)) {
                        inSection71 = true;
                    } else if ("7.2".equals(sec) || "7.3".equals(sec)) {
                        inSection71 = false;
                    }

                    if (!insertedWork && text.contains("工作原理")) {
                        Path img = baseDir.resolve("assets").resolve("work-principle.svg");
                        if (Files.isRegularFile(img)) {
                            addImage(doc, img);
                            insertedWork = true;
                        }
                    }
                    if (!insertedScan && (text.contains("扫描过程") || text.contains("进度"))) {
                        Path img = baseDir.resolve("assets").resolve("scan-process.svg");
                        if (Files.isRegularFile(img)) {
                            addImage(doc, img);
                            insertedScan = true;
                        }
                    }
                    if (!insertedUiConfig && (text.contains("7.1") || text.contains("任务配置页"))) {
                        Path img = baseDir.resolve("assets").resolve("before_scan.png");
                        if (Files.isRegularFile(img)) {
                            addImage(doc, img);
                            insertedUiConfig = true;
                        }
                    }
                    if (!insertedUiProgress && (text.contains("7.2") || text.contains("扫描进度页"))) {
                        Path img = baseDir.resolve("assets").resolve("scan_top.png");
                        if (Files.isRegularFile(img)) {
                            addImage(doc, img);
                            insertedUiProgress = true;
                        }
                    }
                    if (!insertedUiReport && (text.contains("7.3") || text.contains("质量报告页"))) {
                        Path img = baseDir.resolve("assets").resolve("scan_bottom.png");
                        if (Files.isRegularFile(img)) {
                            addImage(doc, img);
                            insertedUiReport = true;
                        }
                    }
                    i++;
                    continue;
                }

                if (HR.matcher(line.trim()).matches()) {
                    i++;
                    continue;
                }

                if (looksLikeTableRow(line) && i + 1 < lines.size() && looksLikeTableSeparator(lines.get(i + 1))) {
                    List<String> tableLines = new ArrayList<>();
                    tableLines.add(line);
                    tableLines.add(lines.get(i + 1));
                    int j = i + 2;
                    while (j < lines.size()) {
                        String tl = lines.get(j);
                        if (tl == null || tl.trim().isEmpty()) {
                            break;
                        }
                        if (!looksLikeTableRow(tl)) {
                            break;
                        }
                        tableLines.add(tl);
                        j++;
                    }
                    addTable(doc, tableLines);
                    if (currentChapter == 1) {
                        addHeading(doc, 3, "说明");
                        addParagraph(doc, "以上痛点通常会在以下环节放大：评审时间紧、文档篇幅大、评审口径不统一、历史问题难回溯。通过自动化扫描与规则统一可显著降低遗漏。");
                        for (String b : tableToBullets(tableLines)) {
                            addBullet(doc, b);
                        }
                    } else if (currentChapter == 2) {
                        addHeading(doc, 3, "说明");
                        for (String b : tableToBullets(tableLines)) {
                            addBullet(doc, b);
                            if (b.contains("闭环") || b.contains("调优")) {
                                tuningSnippets.add(b);
                            }
                        }
                    } else if (currentChapter == 4) {
                        addHeading(doc, 3, "说明");
                        for (String b : tableToBullets(tableLines)) {
                            addBullet(doc, b);
                        }
                    }
                    i = j;
                    continue;
                }

                Matcher im = IMAGE.matcher(line);
                if (im.find()) {
                    String p = im.group(1);
                    Path img = resolvePath(baseDir, p);
                    if (Files.isRegularFile(img)) {
                        addImage(doc, img);
                    } else {
                        addParagraph(doc, stripInlineMd(line));
                    }
                    i++;
                    continue;
                }

                String trimmed = line.trim();
                if (trimmed.startsWith("- ") || trimmed.startsWith("* ")) {
                    String bulletText = trimmed.substring(2).trim();
                    if (inSection71 && shouldSkip71Line(bulletText)) {
                        i++;
                        continue;
                    }
                    if (capturingRuleExt) {
                        ruleExtSnippets.add(stripInlineMd(bulletText));
                        if (bulletText.contains("闭环") || bulletText.contains("调优")) {
                            tuningSnippets.add(stripInlineMd(bulletText));
                        }
                    }
                    addBullet(doc, bulletText);
                    i++;
                    continue;
                }
                if (trimmed.matches("^\\d+\\.\\s+.*$")) {
                    int dot = trimmed.indexOf('.');
                    String rest = dot >= 0 ? trimmed.substring(dot + 1).trim() : trimmed;
                    addOrdered(doc, rest);
                    i++;
                    continue;
                }

                addParagraphWithInline(doc, line);
                if (capturingRuleExt) {
                    String t = stripInlineMd(line).trim();
                    if (!t.isEmpty()) {
                        ruleExtSnippets.add(t);
                        if (t.contains("闭环") || t.contains("调优")) {
                            tuningSnippets.add(t);
                        }
                    }
                }
                i++;
            }

            if (!insertedWork) {
                Path img = baseDir.resolve("assets").resolve("work-principle.svg");
                if (Files.isRegularFile(img)) {
                    addHeading(doc, 2, "工作原理（图）");
                    addImage(doc, img);
                }
            }
            if (!insertedScan) {
                Path img = baseDir.resolve("assets").resolve("scan-process.svg");
                if (Files.isRegularFile(img)) {
                    addHeading(doc, 2, "扫描过程（图）");
                    addImage(doc, img);
                }
            }
            if (!insertedUiConfig) {
                Path img = baseDir.resolve("assets").resolve("before_scan.png");
                if (Files.isRegularFile(img)) {
                    addHeading(doc, 2, "操作界面示意（任务配置页）");
                    addImage(doc, img);
                }
            }
            if (!insertedUiProgress) {
                Path img = baseDir.resolve("assets").resolve("scan_top.png");
                if (Files.isRegularFile(img)) {
                    addHeading(doc, 2, "操作界面示意（扫描进度页）");
                    addImage(doc, img);
                }
            }
            if (!insertedUiReport) {
                Path img = baseDir.resolve("assets").resolve("scan_bottom.png");
                if (Files.isRegularFile(img)) {
                    addHeading(doc, 2, "操作界面示意（质量报告页）");
                    addImage(doc, img);
                }
            }

            addHeading(doc, 1, "第8章 质量调优");
            addParagraph(doc, "质量调优的目标是降低误报/漏报、提升一致性，并让每条问题更可追溯、更可执行。调优建议优先从规则体系与复核闭环入手，再逐步优化提示词与参数。");
            Path tuneImg = baseDir.resolve("assets").resolve("quality-tuning.svg");
            if (Files.isRegularFile(tuneImg)) {
                addImage(doc, tuneImg);
            }
            addHeading(doc, 3, "8.1 规则与闭环");
            if (!tuningSnippets.isEmpty()) {
                for (String s : uniqueNonEmpty(tuningSnippets, 10)) {
                    addBullet(doc, s);
                }
            } else {
                addBullet(doc, "扫描 → 人工复核 → 规则调优 → 再次扫描的闭环迭代，可持续降低误报率。");
            }
            addHeading(doc, 3, "8.2 规则扩充与版本化");
            if (!ruleExtSnippets.isEmpty()) {
                for (String s : uniqueNonEmpty(ruleExtSnippets, 10)) {
                    addBullet(doc, s);
                }
            } else {
                addBullet(doc, "在 work_dir/quality 下新增、拆分或补充规则条目，按维度组织，并对历史样本文档回归验证。");
            }

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                doc.write(out);
                Files.createDirectories(output.getParent() == null ? Path.of("").toAbsolutePath().normalize() : output.getParent());
                Files.write(output, out.toByteArray());
            }
        }
    }

    private static void addTitle(XWPFDocument doc, String title) {
        XWPFParagraph p = doc.createParagraph();
        p.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun r = p.createRun();
        r.setBold(true);
        r.setFontSize(24);
        r.setText(title == null ? "" : title);
        p.setSpacingAfter(260);
    }

    private static void addHeading(XWPFDocument doc, int level, String text) {
        int lv = Math.max(1, Math.min(level, 6));
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(200);
        p.setSpacingAfter(120);
        XWPFRun r = p.createRun();
        r.setBold(true);
        r.setText(text == null ? "" : text.trim());
        if (lv == 1) r.setFontSize(20);
        else if (lv == 2) r.setFontSize(18);
        else if (lv == 3) r.setFontSize(16);
        else r.setFontSize(14);
    }

    private static void addParagraph(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        setTightLineSpacing(p);
        XWPFRun r = p.createRun();
        r.setText(text == null ? "" : text);
    }

    private static void addParagraphWithInline(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        setTightLineSpacing(p);
        appendInlineRuns(p, text == null ? "" : text);
    }

    private static void addBullet(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        setTightLineSpacing(p);
        p.setIndentationLeft(360);
        XWPFRun r = p.createRun();
        r.setText("• ");
        appendInlineRuns(p, text == null ? "" : text);
    }

    private static void addOrdered(XWPFDocument doc, String text) {
        XWPFParagraph p = doc.createParagraph();
        setTightLineSpacing(p);
        p.setIndentationLeft(360);
        XWPFRun r = p.createRun();
        r.setText("1) ");
        appendInlineRuns(p, text == null ? "" : text);
    }

    private static void addSeparator(XWPFDocument doc) {
        XWPFParagraph p = doc.createParagraph();
        p.setSpacingBefore(160);
        p.setSpacingAfter(160);
        XWPFRun r = p.createRun();
        r.setText("————————————————————————————————————————————————");
    }

    private static void addCodeBlock(XWPFDocument doc, String lang, List<String> lines) {
        String l = lang == null ? "" : lang.trim();
        if (!l.isEmpty()) {
            XWPFParagraph label = doc.createParagraph();
            setTightLineSpacing(label);
            XWPFRun lr = label.createRun();
            lr.setBold(true);
            lr.setText(l);
        }
        XWPFParagraph p = doc.createParagraph();
        setTightLineSpacing(p);
        p.setIndentationLeft(360);
        XWPFRun r = p.createRun();
        r.setFontFamily("Menlo");
        r.setFontSize(10);
        StringBuilder sb = new StringBuilder();
        if (lines != null) {
            for (int i = 0; i < lines.size(); i++) {
                if (i > 0) sb.append('\n');
                sb.append(lines.get(i) == null ? "" : lines.get(i));
            }
        }
        String t = sb.toString();
        String[] parts = t.split("\n", -1);
        for (int i = 0; i < parts.length; i++) {
            r.setText(parts[i] == null ? "" : parts[i]);
            if (i < parts.length - 1) {
                r.addBreak();
            }
        }
    }

    private static void addTable(XWPFDocument doc, List<String> tableLines) {
        if (tableLines == null || tableLines.size() < 2) {
            return;
        }
        List<List<String>> rows = new ArrayList<>();
        for (String line : tableLines) {
            if (line == null) continue;
            String t = line.trim();
            if (t.isEmpty()) continue;
            if (looksLikeTableSeparator(t)) continue;
            rows.add(splitTableRow(t));
        }
        if (rows.isEmpty()) return;
        int cols = rows.stream().mapToInt(r -> r == null ? 0 : r.size()).max().orElse(0);
        if (cols <= 0) return;
        XWPFTable table = doc.createTable(rows.size(), cols);
        table.setInsideHBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "D9D9D9");
        table.setInsideVBorder(XWPFTable.XWPFBorderType.SINGLE, 1, 0, "D9D9D9");
        for (int r = 0; r < rows.size(); r++) {
            XWPFTableRow tr = table.getRow(r);
            List<String> cells = rows.get(r);
            for (int c = 0; c < cols; c++) {
                String cellText = c < (cells == null ? 0 : cells.size()) ? cells.get(c) : "";
                String cleaned = stripInlineMd(cellText);
                var cell = tr.getCell(c);
                cell.removeParagraph(0);
                XWPFParagraph p = cell.addParagraph();
                setTightLineSpacing(p);
                XWPFRun run = p.createRun();
                if (r == 0) {
                    run.setBold(true);
                    shadeCell(cell, "D9E8FF");
                }
                run.setText(cleaned);
            }
        }
        doc.createParagraph().setSpacingAfter(160);
    }

    private static void addImage(XWPFDocument doc, Path img) {
        try {
            long maxWidthEmu = maxContentWidthEmu(doc);
            int maxWidthPx = (int) Math.max(400, Math.min(1400, maxWidthEmu / 9525L));
            ImageData data = loadImage(img, Math.max(1200, maxWidthPx * 2));
            if (data == null || data.type == -1 || data.bytes == null || data.bytes.length == 0) {
                addParagraph(doc, "[image] " + img.getFileName());
                return;
            }
            BufferedImage bi = null;
            try (ByteArrayInputStream bin = new ByteArrayInputStream(data.bytes)) {
                bi = ImageIO.read(bin);
            } catch (Exception ignored) {
            }
            int imgW = bi == null ? maxWidthPx : Math.max(1, bi.getWidth());
            int imgH = bi == null ? (int) Math.round(imgW * 0.6) : Math.max(1, bi.getHeight());
            long naturalWEmu = Units.toEMU(imgW);
            long wEmu = Math.min(maxWidthEmu, naturalWEmu);
            long hEmu = (long) Math.round((double) wEmu * (double) imgH / (double) imgW);
            XWPFParagraph p = doc.createParagraph();
            p.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun r = p.createRun();
            try (InputStream in = new ByteArrayInputStream(data.bytes)) {
                r.addPicture(in, data.type, data.fileName, safeEmuInt(wEmu), safeEmuInt(hEmu));
            }
            p.setSpacingAfter(180);
        } catch (Exception e) {
            addParagraph(doc, "[image error] " + img);
        }
    }

    private static int pictureTypeByName(String lowerName) {
        if (lowerName == null) return -1;
        if (lowerName.endsWith(".png")) return XWPFDocument.PICTURE_TYPE_PNG;
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) return XWPFDocument.PICTURE_TYPE_JPEG;
        if (lowerName.endsWith(".gif")) return XWPFDocument.PICTURE_TYPE_GIF;
        if (lowerName.endsWith(".bmp")) return XWPFDocument.PICTURE_TYPE_BMP;
        return -1;
    }

    private static final class ImageData {
        private final byte[] bytes;
        private final int type;
        private final String fileName;

        private ImageData(byte[] bytes, int type, String fileName) {
            this.bytes = bytes;
            this.type = type;
            this.fileName = fileName;
        }
    }

    private static ImageData loadImage(Path img, int widthPx) throws IOException {
        String name = img.getFileName().toString();
        String lower = name.toLowerCase();
        if (lower.endsWith(".svg")) {
            byte[] svg = Files.readAllBytes(img);
            byte[] png = svgToPng(svg, Math.max(400, widthPx));
            return new ImageData(png, XWPFDocument.PICTURE_TYPE_PNG, name + ".png");
        }
        int type = pictureTypeByName(lower);
        if (type == -1) {
            return null;
        }
        byte[] bytes = Files.readAllBytes(img);
        if (lower.endsWith("scan_top.png")) {
            bytes = blurScanTopLogArea(bytes);
        }
        return new ImageData(bytes, type, name);
    }

    private static byte[] blurScanTopLogArea(byte[] pngBytes) {
        if (pngBytes == null || pngBytes.length == 0) {
            return pngBytes;
        }
        try {
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(pngBytes));
            if (bi == null) {
                return pngBytes;
            }
            int w = bi.getWidth();
            int h = bi.getHeight();
            int x = (int) Math.round(w * 0.03);
            int y = (int) Math.round(h * 0.59);
            int rw = (int) Math.round(w * 0.82);
            int rh = (int) Math.round(h * 0.35);
            pixelate(bi, x, y, rw, rh, 14);
            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ImageIO.write(bi, "png", out);
                return out.toByteArray();
            }
        } catch (Exception e) {
            return pngBytes;
        }
    }

    private static void pixelate(BufferedImage img, int x, int y, int w, int h, int block) {
        if (img == null) {
            return;
        }
        int iw = img.getWidth();
        int ih = img.getHeight();
        int bx = Math.max(0, x);
        int by = Math.max(0, y);
        int bw = Math.min(iw - bx, Math.max(0, w));
        int bh = Math.min(ih - by, Math.max(0, h));
        if (bw <= 0 || bh <= 0) {
            return;
        }
        int bsz = Math.max(6, Math.min(40, block));
        for (int yy = by; yy < by + bh; yy += bsz) {
            for (int xx = bx; xx < bx + bw; xx += bsz) {
                int x2 = Math.min(xx + bsz, bx + bw);
                int y2 = Math.min(yy + bsz, by + bh);
                long rs = 0, gs = 0, bs = 0, as = 0;
                int cnt = 0;
                for (int py = yy; py < y2; py++) {
                    for (int px = xx; px < x2; px++) {
                        int argb = img.getRGB(px, py);
                        as += (argb >>> 24) & 0xFF;
                        rs += (argb >>> 16) & 0xFF;
                        gs += (argb >>> 8) & 0xFF;
                        bs += argb & 0xFF;
                        cnt++;
                    }
                }
                if (cnt <= 0) {
                    continue;
                }
                int a = (int) (as / cnt);
                int r = (int) (rs / cnt);
                int g = (int) (gs / cnt);
                int b = (int) (bs / cnt);
                int avg = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
                for (int py = yy; py < y2; py++) {
                    for (int px = xx; px < x2; px++) {
                        img.setRGB(px, py, avg);
                    }
                }
            }
        }
    }

    private static byte[] svgToPng(byte[] svgBytes, int widthPx) {
        if (svgBytes == null || svgBytes.length == 0) {
            return new byte[0];
        }
        try (ByteArrayInputStream in = new ByteArrayInputStream(svgBytes);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) widthPx);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR, Color.WHITE);
            TranscoderInput input = new TranscoderInput(in);
            TranscoderOutput output = new TranscoderOutput(out);
            transcoder.transcode(input, output);
            return out.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private static void shadeCell(XWPFTableCell cell, String fillHex) {
        if (cell == null) {
            return;
        }
        try {
            CTTcPr pr = cell.getCTTc().isSetTcPr() ? cell.getCTTc().getTcPr() : cell.getCTTc().addNewTcPr();
            CTShd shd = pr.isSetShd() ? pr.getShd() : pr.addNewShd();
            shd.setFill(fillHex == null ? "D9E8FF" : fillHex);
        } catch (Exception ignored) {
        }
    }

    private static long maxContentWidthEmu(XWPFDocument doc) {
        long defaultPageW = 12240L;
        long defaultMarL = 1440L;
        long defaultMarR = 1440L;
        try {
            CTSectPr sect = doc.getDocument().getBody().getSectPr();
            if (sect != null) {
                CTPageSz sz = sect.getPgSz();
                CTPageMar mar = sect.getPgMar();
                long pageW = sz == null ? defaultPageW : coerceTwips(sz.getW(), defaultPageW);
                long marL = mar == null ? defaultMarL : coerceTwips(mar.getLeft(), defaultMarL);
                long marR = mar == null ? defaultMarR : coerceTwips(mar.getRight(), defaultMarR);
                long contentTwips = Math.max(2400L, pageW - marL - marR);
                return contentTwips * 635L;
            }
        } catch (Exception ignored) {
        }
        long contentTwips = Math.max(2400L, defaultPageW - defaultMarL - defaultMarR);
        return contentTwips * 635L;
    }

    private static boolean addMermaidBlock(XWPFDocument doc, List<String> lines) {
        String code = joinLines(lines);
        if (code.isBlank()) {
            return false;
        }
        String sanitized = sanitizeMermaid(code);
        byte[] png = renderMermaidPng(sanitized);
        if (png == null || png.length == 0) {
            png = renderMermaidPng(code);
        }
        if (png == null || png.length == 0) {
            return false;
        }
        try {
            long maxWidthEmu = maxContentWidthEmu(doc);
            BufferedImage bi = ImageIO.read(new ByteArrayInputStream(png));
            if (bi == null) {
                return false;
            }
            int imgW = Math.max(1, bi.getWidth());
            int imgH = Math.max(1, bi.getHeight());
            long wEmu = Math.min(maxWidthEmu, Units.toEMU(imgW));
            long hEmu = (long) Math.round((double) wEmu * (double) imgH / (double) imgW);
            XWPFParagraph p = doc.createParagraph();
            p.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun r = p.createRun();
            try (InputStream in = new ByteArrayInputStream(png)) {
                r.addPicture(in, XWPFDocument.PICTURE_TYPE_PNG, "mermaid.png", safeEmuInt(wEmu), safeEmuInt(hEmu));
            }
            p.setSpacingAfter(180);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static int safeEmuInt(long emu) {
        if (emu < 0) {
            return 0;
        }
        if (emu > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) emu;
    }

    private static long coerceTwips(Object v, long fallback) {
        if (v == null) {
            return fallback;
        }
        if (v instanceof BigInteger bi) {
            return bi.longValue();
        }
        if (v instanceof Number n) {
            return n.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(v));
        } catch (Exception e) {
            return fallback;
        }
    }

    private static byte[] renderMermaidPng(String mermaid) {
        String m = mermaid == null ? "" : mermaid.trim();
        if (m.isEmpty()) {
            return new byte[0];
        }
        try {
            String b64 = Base64.getUrlEncoder().withoutPadding().encodeToString(m.getBytes(StandardCharsets.UTF_8));
            URI uri = URI.create("https://mermaid.ink/img/" + b64);
            HttpRequest req = HttpRequest.newBuilder().uri(uri).timeout(Duration.ofSeconds(20)).GET().build();
            HttpResponse<byte[]> resp = HTTP.send(req, HttpResponse.BodyHandlers.ofByteArray());
            int st = resp.statusCode();
            if (st < 200 || st >= 300) {
                return new byte[0];
            }
            byte[] body = resp.body();
            return body == null ? new byte[0] : body;
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private static String sanitizeMermaid(String s) {
        String t = s == null ? "" : s;
        t = t.replace('（', '(').replace('）', ')');
        t = t.replace('“', '"').replace('”', '"');
        t = t.replace("—", "-");
        return t;
    }

    private static String joinLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.size(); i++) {
            if (i > 0) sb.append('\n');
            sb.append(lines.get(i) == null ? "" : lines.get(i));
        }
        return sb.toString();
    }

    private static String section7Key(String headingText) {
        String t = headingText == null ? "" : headingText.trim();
        if (t.isEmpty()) {
            return "";
        }
        Matcher m = SECTION_7.matcher(t);
        if (!m.find()) {
            return "";
        }
        String d = m.group(1);
        if ("1".equals(d)) return "7.1";
        if ("2".equals(d)) return "7.2";
        if ("3".equals(d)) return "7.3";
        return "";
    }

    private static boolean shouldSkip71Line(String bulletText) {
        String t = bulletText == null ? "" : bulletText.trim();
        if (t.isEmpty()) {
            return false;
        }
        return (t.contains("文本分段长度") && t.contains("tokens"))
                || (t.contains("模型温度") && t.contains("0.1"));
    }

    private static int chapterNumberFromHeading(String headingText) {
        String t = headingText == null ? "" : headingText.trim();
        if (t.isEmpty()) {
            return 0;
        }
        Matcher m1 = CHAPTER_CN.matcher(t);
        if (m1.matches()) {
            try {
                return Integer.parseInt(m1.group(1));
            } catch (Exception ignored) {
            }
        }
        Matcher m2 = CHAPTER_ZH.matcher(t);
        if (m2.matches()) {
            return zhNumberToInt(m2.group(1));
        }
        Matcher m3 = CHAPTER_AR.matcher(t);
        if (m3.matches()) {
            try {
                return Integer.parseInt(m3.group(1));
            } catch (Exception ignored) {
            }
        }
        return 0;
    }

    private static String formatChapterTitle(int chapterNo, String raw) {
        String t = raw == null ? "" : raw.trim();
        Matcher m1 = CHAPTER_CN.matcher(t);
        if (m1.matches()) {
            t = m1.group(2);
        } else {
            Matcher m2 = CHAPTER_ZH.matcher(t);
            if (m2.matches()) {
                t = m2.group(2);
            } else {
                Matcher m3 = CHAPTER_AR.matcher(t);
                if (m3.matches()) {
                    t = m3.group(2);
                }
            }
        }
        t = t.trim();
        return "第" + chapterNo + "章 " + t;
    }

    private static int zhNumberToInt(String zh) {
        if (zh == null || zh.isBlank()) {
            return 0;
        }
        String t = zh.trim();
        int v = 0;
        for (int i = 0; i < t.length(); i++) {
            char ch = t.charAt(i);
            int d = switch (ch) {
                case '一' -> 1;
                case '二' -> 2;
                case '三' -> 3;
                case '四' -> 4;
                case '五' -> 5;
                case '六' -> 6;
                case '七' -> 7;
                case '八' -> 8;
                case '九' -> 9;
                case '十' -> 10;
                default -> 0;
            };
            if (d == 10) {
                if (v == 0) v = 10;
                else v = v * 10;
            } else {
                if (v == 10) {
                    v = 10 + d;
                } else if (v == 0) {
                    v = d;
                } else {
                    v = v * 10 + d;
                }
            }
        }
        return v;
    }

    private static List<String> tableToBullets(List<String> tableLines) {
        if (tableLines == null || tableLines.size() < 3) {
            return List.of();
        }
        List<List<String>> rows = new ArrayList<>();
        for (String line : tableLines) {
            if (line == null) continue;
            String t = line.trim();
            if (t.isEmpty()) continue;
            if (looksLikeTableSeparator(t)) continue;
            rows.add(splitTableRow(t));
        }
        if (rows.size() <= 1) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        for (int i = 1; i < rows.size(); i++) {
            List<String> r = rows.get(i);
            if (r == null || r.isEmpty()) continue;
            String a = r.size() > 0 ? stripInlineMd(r.get(0)) : "";
            String b = r.size() > 1 ? stripInlineMd(r.get(1)) : "";
            String s = (a + "：" + b).trim();
            if (!s.equals("：") && !s.isBlank()) out.add(s);
        }
        return out;
    }

    private static List<String> uniqueNonEmpty(List<String> items, int limit) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        List<String> out = new ArrayList<>();
        for (String s : items) {
            String t = s == null ? "" : s.trim();
            if (t.isEmpty()) continue;
            boolean exists = false;
            for (String e : out) {
                if (e.equals(t)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) out.add(t);
            if (limit > 0 && out.size() >= limit) break;
        }
        return out;
    }

    private static void appendInlineRuns(XWPFParagraph p, String text) {
        String s = text == null ? "" : text;
        int idx = 0;
        while (idx < s.length()) {
            int nextBold = indexOf(s, "**", idx);
            int nextCode = indexOf(s, "`", idx);
            int next = minPos(nextBold, nextCode);
            if (next < 0) {
                XWPFRun r = p.createRun();
                r.setText(stripInlineMd(s.substring(idx)));
                return;
            }
            if (next > idx) {
                XWPFRun r = p.createRun();
                r.setText(stripInlineMd(s.substring(idx, next)));
            }
            if (next == nextBold) {
                int end = s.indexOf("**", next + 2);
                if (end > next + 2) {
                    String content = s.substring(next + 2, end);
                    XWPFRun r = p.createRun();
                    r.setBold(true);
                    r.setText(stripInlineMd(content));
                    idx = end + 2;
                    continue;
                }
            }
            if (next == nextCode) {
                int end = s.indexOf('`', next + 1);
                if (end > next + 1) {
                    String content = s.substring(next + 1, end);
                    XWPFRun r = p.createRun();
                    r.setFontFamily("Menlo");
                    r.setFontSize(10);
                    r.setText(content);
                    idx = end + 1;
                    continue;
                }
            }
            XWPFRun r = p.createRun();
            r.setText(String.valueOf(s.charAt(next)));
            idx = next + 1;
        }
    }

    private static int indexOf(String s, String needle, int from) {
        if (s == null || needle == null || needle.isEmpty()) return -1;
        return s.indexOf(needle, from);
    }

    private static int minPos(int a, int b) {
        if (a < 0) return b;
        if (b < 0) return a;
        return Math.min(a, b);
    }

    private static boolean looksLikeTableRow(String line) {
        if (line == null) return false;
        String t = line.trim();
        return t.contains("|") && !t.startsWith("```");
    }

    private static boolean looksLikeTableSeparator(String line) {
        if (line == null) return false;
        String t = line.trim();
        if (!t.contains("|")) return false;
        String s = t.replace(" ", "");
        return s.matches("^\\|?[:\\-]+(\\|[:\\-]+)+\\|?$");
    }

    private static List<String> splitTableRow(String line) {
        String t = line == null ? "" : line.trim();
        if (t.startsWith("|")) t = t.substring(1);
        if (t.endsWith("|")) t = t.substring(0, t.length() - 1);
        String[] parts = t.split("\\|", -1);
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            out.add(p == null ? "" : p.trim());
        }
        return out;
    }

    private static List<String> splitLines(String s) {
        String t = s == null ? "" : s;
        String[] parts = t.replace("\r\n", "\n").replace('\r', '\n').split("\n", -1);
        List<String> out = new ArrayList<>(parts.length);
        for (String p : parts) out.add(p);
        return out;
    }

    private static Path resolvePath(Path baseDir, String raw) {
        String t = raw == null ? "" : raw.trim();
        if (t.isEmpty()) return baseDir;
        int sp = t.indexOf(' ');
        if (sp > 0) t = t.substring(0, sp);
        Path p = Path.of(t);
        if (p.isAbsolute()) return p.toAbsolutePath().normalize();
        return baseDir.resolve(p).toAbsolutePath().normalize();
    }

    private static String stripInlineMd(String s) {
        String t = s == null ? "" : s;
        t = t.replaceAll("\\*\\*(.+?)\\*\\*", "$1");
        t = t.replaceAll("`([^`]+)`", "$1");
        return t;
    }

    private static void setTightLineSpacing(XWPFParagraph p) {
        if (p == null) return;
        CTPPr ppr = p.getCTP().isSetPPr() ? p.getCTP().getPPr() : p.getCTP().addNewPPr();
        CTSpacing sp = ppr.isSetSpacing() ? ppr.getSpacing() : ppr.addNewSpacing();
        sp.setLineRule(org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule.AUTO);
        sp.setLine(BigInteger.valueOf(276));
        sp.setAfter(BigInteger.valueOf(60));
        sp.setBefore(BigInteger.valueOf(0));
    }
}
