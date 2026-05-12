[中文](../zh/manual.md) | [English](manual.md) | [日本語](../ja/manual.md)

## Web UI User Manual

### 1) Start and Enter Page

Access after starting:

- http://localhost:8765/

The page is divided into three sections:

- Top: Configuration and Start Scanning
- Middle: Scan Summary (Progress List / Status Logs)
- Bottom: Issue Viewer (Accept / Reject)

### 2) Select Operation Mode and Configure (Expand Configuration)

The configuration area supports two modes of operation:

- Local Directory: Directly scans `.doc/.docx` files within a specified local directory.
- Upload Files: Select and upload multiple `.doc/.docx` files in the browser to scan them (output goes to the default `work` directory).

#### Local Directory

Requirement Directory to Review (`req_dir`):

- Enter the absolute path directly (most stable).

Requirement Review Output Directory (`out_dir`, optional):

- Leave blank: Defaults to the `work/output` in the working directory.
- Enter absolute path: Writes results to the specified directory.
- Alternatively, click "Choose Folder" to browse and select the target directory within the page (only affects Local Directory mode).

Rules Directory (`rules_dir`, optional):

- Enter the absolute path directly (the directory can contain multiple `.md` rule files).
- Leave blank: Defaults to the rule directory `work/quality`.

#### Upload Files

Upload files to scan:

- Click "Choose Files", multiple `.doc/.docx` files can be selected at once.
- Click "Clear" to remove selected files.
- Uploaded files will be saved to the default `work/input` directory, and filenames will be appended with a timestamp to prevent overwriting; the scan job will create an isolated directory under `work/uploads` for this upload.

In this mode:

- The rules directory is fixed to the default `work/quality`.
- The output directory is fixed to the default `work/output`.

### 3) Start Scanning

Click "Start Scan" in the upper right corner:

- Scanning will proceed file by file, and model requests will be made in chunks according to the rules.
- The page will update the "Scan Summary" and "Issue Viewer" in real-time.

### 4) Scan Summary and Download

Scan Summary Area:

- "Expand List": Displays the status, rule count, issue count, and output path for each file.
- Click a row: Switches the issue area to that file for focused processing.
- "Download": Downloads the Excel results for that file (one output per file).
- "Expand Logs": Displays the status logs of the current task (process info appended on the UI side).

### 5) Notifications (Quick Jump to New Issues)

During scanning, if new issues are found, a number badge will appear on the "Notifications" button in the upper right corner:

- Click "Notifications": Expands the list of issues.
- Click a notification: Automatically switches to the corresponding file and navigates to the specific issue (highlighted).

### 6) Issue Confirmation: Accept / Reject

Issue Viewer Area:

- "File Filter": Select a specific file, or select "All".
- Each issue contains: Severity, Category, Page/Section, Evidence Excerpt, Suggestion.
- Click "Expand/Collapse" to view details.

Actions:

- "Accept": Marks the issue as accepted and attempts to write it into a revised Word document (if successful, returns the revised path and prompts in the status area).
- "Reject": Marks the issue as rejected.

### 7) File Storage Locations (Default `work`)

If using the default working directory:

- Excel: `work/output/*.xlsx`
- Review records: `work/output/*.review.json`
- Revised Word: `work/revise/*.docx`
- Logs: `work/logs/spec-qc.log` and `work/logs/large-model.log`