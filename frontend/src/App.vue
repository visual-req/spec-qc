<template>
  <div class="container">
    <div class="card">
      <div class="row">
        <div class="title">AI需求质量扫描</div>
        <div style="display:flex; gap: 8px;">
          <div class="notif-wrap">
            <button class="btn notif-btn" :disabled="!notifications.length" @click="toggleNotif">
              提醒
              <span v-if="notifications.length" class="badge">{{ notifications.length }}</span>
            </button>
            <div v-if="notifOpen" class="notif-panel">
              <div v-if="!notifications.length" class="empty" style="padding: 10px;">暂无提醒</div>
              <div v-else>
                <div v-for="n in notifications" :key="n.id" class="notif-item" @click="openNotif(n)">
                  <div class="notif-title">{{ n.title }}</div>
                  <div class="notif-sub mono">{{ n.file }} #{{ n.seq }}</div>
                </div>
              </div>
            </div>
          </div>
          <button class="btn" @click="showConfig = !showConfig">{{ showConfig ? '收起配置' : '展开配置' }}</button>
          <button class="btn btn-primary" :disabled="isScanning" @click="startScan">开始扫描</button>
        </div>
      </div>

      <div class="spacer"></div>

      <div v-show="showConfig">
        <div>
          <label>待评审需求目录（推荐：选择文件夹上传）</label>
          <div class="input-row">
            <input :value="reqFolderDisplay" placeholder="请填写绝对路径（也可选择文件夹上传）" @input="onReqPathInput" />
            <button class="btn" :disabled="isScanning" @click="pickReqFolder">选择文件夹</button>
          </div>
          <input ref="reqPicker" type="file" webkitdirectory directory multiple style="display:none" @change="onReqFolderChange" />
        </div>
        <div class="spacer"></div>
        <div>
          <label>需求评审结果目录（可选，默认：work/output）</label>
          <div class="input-row">
            <input v-model="outDir" placeholder="请填写绝对路径（留空则使用默认输出目录）" />
            <button class="btn" :disabled="isScanning" @click="toggleOutPicker">{{ outPickerOpen ? '收起' : '选择文件夹' }}</button>
          </div>
          <div v-if="outPickerOpen">
            <div class="spacer"></div>
            <div class="root-row">
              <button v-for="r in roots" :key="r.name" class="btn" @click="openOutPath(r.path)">{{ r.name }}</button>
              <button class="btn" @click="outGoUp">上一级</button>
              <button class="btn btn-primary" @click="chooseOutDir">选择此目录</button>
            </div>
            <div class="path-box mono">{{ outBrowsePath }}</div>
            <div class="spacer"></div>
            <div class="tree">
              <div v-for="e in outEntries" :key="e.path" class="tree-row" @click="openOutPath(e.path)">
                <div class="tree-name">{{ e.name }}</div>
              </div>
              <div v-if="!outEntries.length" class="empty" style="padding: 12px;">无子目录</div>
            </div>
          </div>
        </div>
        <div class="spacer"></div>
        <div>
          <label>规则目录（可选，支持选择文件夹上传或手工输入服务器路径）</label>
          <div class="input-row">
            <input :value="rulesFolderDisplay" placeholder="请填写绝对路径（也可选择文件夹上传）" @input="onRulesPathInput" />
            <button class="btn" :disabled="isScanning" @click="pickRulesFolder">选择文件夹</button>
          </div>
          <input ref="rulesPicker" type="file" webkitdirectory directory multiple style="display:none" @change="onRulesFolderChange" />
        </div>
      </div>
    </div>

    <div class="spacer"></div>

    <div class="card">
      <div class="row">
        <div style="font-size: 14px; font-weight: 600;">扫描汇总</div>
        <div style="display:flex; gap: 8px;">
          <button class="btn" @click="showProgress = !showProgress">{{ showProgress ? '收起列表' : '展开列表' }}</button>
          <button class="btn" @click="showLogs = !showLogs">{{ showLogs ? '收起日志' : '展开日志' }}</button>
        </div>
      </div>
      <div class="spacer"></div>
      <div class="stats">
        <div class="stat"><div class="k">总文件数</div><div class="v">{{ summary.totalFiles }}</div></div>
        <div class="stat"><div class="k">已扫描文件数</div><div class="v">{{ summary.scannedFiles }}</div></div>
        <div class="stat"><div class="k">发现问题总数</div><div class="v">{{ summary.totalIssues }}</div></div>
        <div class="stat"><div class="k">扫描状态</div><div class="v">{{ scanStatusText }}</div></div>
      </div>
      <div v-show="showProgress">
        <div class="spacer"></div>
        <div class="table-wrap">
          <div class="table-scroll progress-scroll">
            <table>
              <thead>
                <tr>
                  <th>文件名</th>
                  <th style="width: 90px;">状态</th>
                  <th style="width: 170px;">开始时间</th>
                  <th style="width: 170px;">结束时间</th>
                  <th style="width: 110px;">总时长</th>
                  <th style="width: 90px;">规则数</th>
                  <th style="width: 90px;">问题数</th>
                  <th style="width: 90px;">输出</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="it in progressFiles" :key="it.file_name" @click="selectFile(it.file_name)" style="cursor:pointer;">
                  <td>{{ it.file_name }}</td>
                  <td>
                    <span class="tag" :class="statusTagClass(it.status)">{{ statusTagText(it.status) }}</span>
                  </td>
                  <td>{{ formatLocalTime(it.started_at) }}</td>
                  <td>{{ formatLocalTime(it.ended_at) }}</td>
                  <td>{{ formatDuration(it.duration_ms) }}</td>
                  <td>{{ it.rule_count ?? 0 }}</td>
                  <td>{{ it.issue_count ?? 0 }}</td>
                  <td>
                    <button class="btn" :disabled="!it.output_path || !jobId" @click.stop="download(it.file_name)">
                      下载
                    </button>
                  </td>
                </tr>
                <tr v-if="!progressFiles.length">
                  <td colspan="8" class="empty">暂无数据</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div class="spacer"></div>
      </div>
      <div v-show="showLogs" class="status mono status-scroll">{{ statusText }}</div>
    </div>

    <div class="spacer"></div>

    <div class="card">
      <div style="font-size: 14px; font-weight: 600; margin-bottom: 10px;">问题查看</div>
      <div style="display:flex; gap: 8px; align-items:center; flex-wrap: wrap;">
        <label style="margin:0;">文件筛选</label>
        <select class="btn" v-model="selectedFile" @change="onSelectedFileChange">
          <option value="__ALL__">全部</option>
          <option v-for="name in fileOptions" :key="name" :value="name">{{ name }}</option>
        </select>
      </div>
      <div class="spacer"></div>
      <div class="issue-list-wrap">
        <div v-if="!displayedIssues.length" class="empty">暂无数据</div>
        <div v-else class="issue-list">
          <div v-for="row in displayedIssues" :key="row.__key" class="issue-item" :data-key="row.__key" :class="row.__key === highlightKey ? 'issue-item-highlight' : ''">
            <div class="issue-head">
              <div class="issue-title">
                <button class="btn btn-sm" @click.stop="toggleIssue(row)">{{ isIssueOpen(row) ? '折叠' : '展开' }}</button>
                <span class="tag" :class="row.severity === '高' ? 'tag-red' : (row.severity === '中' ? 'tag-gold' : 'tag-blue')">
                  {{ row.severity || '低' }}
                </span>
                <span style="margin-left:8px;">#{{ row.seq }} {{ row.category }}</span>
                <span v-if="row.evidence_section" class="issue-section mono">{{ row.evidence_section }}</span>
              </div>
              <div style="display:flex; gap: 8px; align-items:center;">
                <span class="tag" :class="reviewTagClass(row.review_status)">{{ reviewTagText(row.review_status) }}</span>
                <button class="btn" :disabled="!jobId || !isFileReviewable(row.__file) || isReviewBusy(row)" @click="decide(row, 'reject')">{{ isReviewBusy(row) ? '处理中' : '拒绝' }}</button>
                <button class="btn btn-primary" :disabled="!jobId || !isFileReviewable(row.__file) || row.review_status === 'accepted' || isReviewBusy(row)" @click="decide(row, 'accept')">{{ isReviewBusy(row) ? '处理中' : '接受' }}</button>
                <div v-if="selectedFile === '__ALL__'" class="issue-file">{{ row.__file }}</div>
              </div>
            </div>
            <div class="issue-body" v-show="isIssueOpen(row)">
              <div class="issue-row">
                <div class="kv inline">
                  <div class="k">页号</div>
                  <div class="v">{{ row.evidence_page }}</div>
                </div>
                <div class="kv inline">
                  <div class="k">章节编号</div>
                  <div class="v">{{ row.evidence_section }}</div>
                </div>
              </div>
              <div class="issue-row">
                <div class="kv inline">
                  <div class="k">问题描述</div>
                  <div class="v">{{ row.description }}</div>
                </div>
                <div class="kv inline">
                  <div class="k">关联标准</div>
                  <div class="v mono">{{ row.related_standard }}</div>
                </div>
              </div>
              <div class="issue-row one-col">
                <div class="kv">
                  <div class="k">内容摘录</div>
                  <div class="v" v-html="renderEvidenceHtml(row)"></div>
                </div>
              </div>
              <div class="issue-row one-col">
                <div class="kv">
                  <div class="k">建议</div>
                  <div class="v" v-html="renderSuggestionHtml(row)"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const reqDir = ref('')
const outDir = ref('')
const rulesDir = ref('')
const showConfig = ref(true)
const showProgress = ref(true)
const showLogs = ref(true)

const reqPicker = ref(null)
const rulesPicker = ref(null)
const reqFiles = ref([])
const rulesFiles = ref([])
const reqFolderLabel = ref('')
const rulesFolderLabel = ref('')

const jobId = ref('')
const statusText = ref('')
const progressFiles = ref([])
const progressData = ref(null)
const pollTimer = ref(null)

const issuesByFile = ref({})
const selectedFile = ref('__ALL__')
const notifications = ref([])
const notifOpen = ref(false)
const highlightKey = ref('')
const issueOpen = ref({})
const reviewBusy = ref({})

const outPickerOpen = ref(false)
const roots = ref([])
const outBrowsePath = ref('')
const outEntries = ref([])

const isScanning = computed(() => !!pollTimer.value)

const fileOptions = computed(() => {
  const names = (progressFiles.value || []).map((it) => String(it.file_name || '')).filter(Boolean)
  const unique = Array.from(new Set(names))
  unique.sort((a, b) => a.localeCompare(b))
  return unique
})

const summary = computed(() => {
  const p = progressData.value || {}
  const files = (p.files || progressFiles.value || [])
  const totalIssues = files.reduce((acc, it) => acc + (Number(it.issue_count) || 0), 0)
  return {
    totalFiles: Number(p.total_files || files.length || 0),
    scannedFiles: Number(p.scanned_files || files.filter((it) => String(it.status || '').includes('完成') || String(it.status || '').includes('失败')).length),
    totalIssues,
  }
})

const fileStatusMap = computed(() => {
  const map = {}
  for (const it of (progressFiles.value || [])) {
    const name = String(it?.file_name || '')
    if (!name) continue
    map[name] = String(it?.status || '')
  }
  return map
})

const scanStatusText = computed(() => {
  const p = progressData.value || {}
  const st = String(p.status || '')
  if (st === 'running') {
    const files = (p.files || progressFiles.value || [])
    const active = files.find((it) => {
      const s = String(it.status || '')
      return s && !s.includes('完成') && !s.includes('失败')
    })
    const detail = active ? String(active.status || '') : ''
    return detail || '扫描中'
  }
  if (st === 'complete' || st === 'done') return '已完成'
  if (st === 'failed' || st === 'error') return '失败'
  if (isScanning.value) return '扫描中'
  return '未开始'
})

const displayedIssues = computed(() => {
  const all = selectedFile.value === '__ALL__'
  const byFile = issuesByFile.value || {}
  const filterOutRejected = (arr) => (arr || []).filter((x) => String(x?.review_status || '').toLowerCase() !== 'rejected')
  if (!all) {
    const arr = filterOutRejected(byFile[selectedFile.value] || [])
    return arr.map((it, idx) => ({ ...it, __file: selectedFile.value, __key: selectedFile.value + ':' + (it.seq || idx) }))
  }
  const rows = []
  for (const [file, arr] of Object.entries(byFile)) {
    const filtered = filterOutRejected(arr || [])
    for (let i = 0; i < filtered.length; i++) {
      const it = filtered[i]
      rows.push({ ...it, __file: file, __key: file + ':' + (it.seq || i) })
    }
  }
  return rows
})

async function getJson(url) {
  const resp = await fetch(url)
  return await resp.json()
}

async function postJson(url, obj) {
  const resp = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(obj),
  })
  return await resp.json()
}

async function postForm(url, formData) {
  const resp = await fetch(url, { method: 'POST', body: formData })
  return await resp.json()
}

function formatLocalTime(iso) {
  if (!iso) return ''
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return String(iso)
  return d.toLocaleString()
}

function formatDuration(ms) {
  const n = Number(ms)
  if (!Number.isFinite(n) || n < 0) return ''
  const totalSec = Math.floor(n / 1000)
  const hh = Math.floor(totalSec / 3600)
  const mm = Math.floor((totalSec % 3600) / 60)
  const ss = totalSec % 60
  const parts = []
  if (hh) parts.push(hh + '小时')
  if (mm) parts.push(mm + '分钟')
  parts.push(ss + '秒')
  return parts.join('')
}

function statusTagClass(s) {
  const v = String(s || '')
  if (v.includes('失败')) return 'tag-red'
  if (v.includes('完成')) return 'tag-green'
  if (v.includes('开始')) return 'tag-blue'
  return 'tag-gold'
}

function statusTagText(s) {
  const v = String(s || '')
  if (v.includes('失败')) return '失败'
  if (v.includes('完成')) return '完成'
  if (v.includes('开始')) return '开始'
  return '扫描中'
}

function reviewTagClass(s) {
  const v = String(s || '').toLowerCase()
  if (v === 'accepted') return 'tag-green'
  if (v === 'rejected') return 'tag-red'
  return 'tag-blue'
}

function reviewTagText(s) {
  const v = String(s || '').toLowerCase()
  if (v === 'accepted') return '已接受'
  if (v === 'rejected') return '已拒绝'
  return '待处理'
}

function isFileReviewable(fileName) {
  const name = String(fileName || '').trim()
  if (!name) return false
  const st = String(fileStatusMap.value?.[name] || '')
  return st.includes('完成') || st.includes('失败')
}

async function decide(row, action) {
  const file = String(row?.__file || selectedFile.value || '')
  const seq = String(row?.seq || '').trim()
  if (!file || !seq || !jobId.value) return
  if (!isFileReviewable(file)) return
  const key = String(row?.__key || (file + ':' + seq)).trim()
  const reqId = Date.now() + '-' + Math.random().toString(16).slice(2)
  reviewBusy.value = { ...(reviewBusy.value || {}), [key]: true }
  appendStatusLog(`[ui] click ${action} req_id=${reqId} file=${file} seq=${seq}`)
  sendClientLog({ req_id: reqId, stage: 'click', action, file, seq })
  try {
    appendStatusLog(`[ui] request ${action} req_id=${reqId}`)
    sendClientLog({ req_id: reqId, stage: 'request_start', action, file, seq })
    const data = await postJson('/api/review/decision', { job_id: jobId.value, file_name: file, seq, action, req_id: reqId })
    appendStatusLog(`[ui] response ${action} req_id=${reqId} ok=${!data?.error}`)
    sendClientLog({ req_id: reqId, stage: 'response', action, file, seq, ok: data?.error ? 'false' : 'true', error: String(data?.error || '') })
    if (data?.error) {
      statusText.value = String(data.error)
      return
    }
    const issues = Array.isArray(data?.issues) ? data.issues : []
    issuesByFile.value = { ...(issuesByFile.value || {}), [file]: issues }
    const revised = String(data?.revised_path || '').trim()
    if (revised) {
      statusText.value = '已写入修订版 Word：' + revised
    }
  } catch (e) {
    statusText.value = String(e)
    appendStatusLog(`[ui] error ${action} req_id=${reqId} err=${String(e)}`)
    sendClientLog({ req_id: reqId, stage: 'error', action, file, seq, error: String(e) })
  } finally {
    const next = { ...(reviewBusy.value || {}) }
    delete next[key]
    reviewBusy.value = next
  }
}

const knownIssueKeys = new Set()

function pushNotifications(nextIssues) {
  const list = []
  for (const [file, arr] of Object.entries(nextIssues || {})) {
    for (const it of (arr || [])) {
      const seq = String(it?.seq || '').trim()
      if (!seq) continue
      const key = file + ':' + seq
      if (knownIssueKeys.has(key)) continue
      knownIssueKeys.add(key)
      const title = '发现新问题：' + String(it?.category || '') + '（' + String(it?.severity || '') + '）'
      list.push({ id: key, file, seq, title })
    }
  }
  if (list.length) {
    notifications.value = [...list, ...(notifications.value || [])].slice(0, 50)
  }
}

function toggleNotif() {
  notifOpen.value = !notifOpen.value
}

async function openNotif(n) {
  const file = String(n?.file || '')
  const seq = String(n?.seq || '')
  if (!file || !seq) return
  notifOpen.value = false
  notifications.value = (notifications.value || []).filter((x) => x && x.id !== n.id)
  selectedFile.value = file
  await selectFile(file)
  highlightKey.value = file + ':' + seq
  setTimeout(() => {
    const el = document.querySelector('[data-key="' + highlightKey.value.replaceAll('"', '') + '"]')
    if (el && el.scrollIntoView) el.scrollIntoView({ behavior: 'smooth', block: 'center' })
  }, 50)
}

function escapeHtml(s) {
  return String(s || '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#39;')
}

function renderEvidenceHtml(row) {
  const paragraphRaw = String(row?.evidence_paragraph || row?.evidence_excerpt || '')
  const excerptRaw = String(row?.evidence_excerpt || '')
  const paragraph = escapeHtml(paragraphRaw)
  const excerpt = escapeHtml(excerptRaw)
  const base = (!excerpt || !paragraph || !paragraph.includes(excerpt))
    ? paragraph
    : paragraph.split(excerpt).join('<span class="hl">' + excerpt + '</span>')
  return base.replaceAll('\n', '<br>')
}

function sanitizeHtmlAllowlist(html) {
  const raw = String(html || '')
  if (!raw.trim()) return ''
  const removedScripts = raw.replace(/<\s*script[^>]*>[\s\S]*?<\s*\/\s*script\s*>/gi, '')
  const allow = new Set(['table', 'thead', 'tbody', 'tr', 'th', 'td', 'p', 'ul', 'ol', 'li', 'br', 'strong', 'b', 'code', 'pre'])
  return removedScripts.replace(/<\/?\s*([a-zA-Z0-9]+)(\s+[^>]*)?\s*>/g, (m, tagName) => {
    const t = String(tagName || '').toLowerCase()
    if (!allow.has(t)) return ''
    const isClose = m.trim().startsWith('</')
    if (t === 'br') return '<br>'
    return isClose ? `</${t}>` : `<${t}>`
  })
}

function renderSuggestionHtml(row) {
  const html = String(row?.suggestion_html || '')
  if (html.trim()) return sanitizeHtmlAllowlist(html)
  const text = escapeHtml(String(row?.suggestion || ''))
  return text.replaceAll('\n', '<br>')
}

function nowTs() {
  return new Date().toLocaleString()
}

function appendStatusLog(line) {
  const t = `${nowTs()} ${String(line || '')}`.trim()
  const prev = String(statusText.value || '')
  statusText.value = (prev ? (prev + '\n') : '') + t
}

async function sendClientLog(obj) {
  try {
    await postJson('/api/client/log', obj || {})
  } catch (e) {
  }
}

function isReviewBusy(row) {
  const key = String(row?.__key || '').trim()
  if (!key) return false
  return !!(reviewBusy.value || {})[key]
}

function isIssueOpen(row) {
  const key = String(row?.__key || '').trim()
  if (!key) return true
  const v = (issueOpen.value || {})[key]
  return v === undefined ? true : !!v
}

function toggleIssue(row) {
  const key = String(row?.__key || '').trim()
  if (!key) return
  const next = { ...(issueOpen.value || {}) }
  next[key] = !isIssueOpen(row)
  issueOpen.value = next
}

const reqFolderDisplay = computed(() => {
  if (reqFiles.value && reqFiles.value.length) {
    const label = reqFolderLabel.value ? (reqFolderLabel.value + ' ') : ''
    return label + '(' + reqFiles.value.length + '个文件)'
  }
  return reqDir.value
})

const rulesFolderDisplay = computed(() => {
  if (rulesFiles.value && rulesFiles.value.length) {
    const label = rulesFolderLabel.value ? (rulesFolderLabel.value + ' ') : ''
    return label + '(' + rulesFiles.value.length + '个文件)'
  }
  return rulesDir.value
})

async function loadCache() {
  try {
    const data = await getJson('/api/cache')
    reqDir.value = data?.req_dir || 'work/input'
    outDir.value = data?.out_dir || 'work/output'
    rulesDir.value = data?.rules_dir || 'work/quality'
  } catch (e) {
    statusText.value = String(e)
    if (!reqDir.value) reqDir.value = 'work/input'
    if (!outDir.value) outDir.value = 'work/output'
    if (!rulesDir.value) rulesDir.value = 'work/quality'
  }
}

async function ensureRootsLoaded() {
  if (roots.value && roots.value.length) return
  try {
    const data = await getJson('/api/roots')
    roots.value = Array.isArray(data?.roots) ? data.roots : []
  } catch (e) {
    roots.value = []
  }
}

async function openOutPath(p) {
  const path = String(p || '').trim()
  if (!path) return
  try {
    const data = await getJson('/api/fs?path=' + encodeURIComponent(path))
    if (data?.error) {
      statusText.value = '读取目录失败：' + String(data.error)
      return
    }
    outBrowsePath.value = String(data?.path || path)
    outEntries.value = Array.isArray(data?.entries) ? data.entries : []
  } catch (e) {
    statusText.value = String(e)
  }
}

function parentPath(p) {
  const s = String(p || '')
  if (!s) return ''
  const sep = s.includes('\\') ? '\\' : '/'
  let t = s
  if (t.endsWith(sep) && t.length > (sep === '\\' ? 3 : 1)) {
    t = t.slice(0, -1)
  }
  const idx = t.lastIndexOf(sep)
  if (idx < 0) return t
  if (sep === '\\' && idx <= 2) return t.slice(0, idx + 1)
  if (sep === '/' && idx === 0) return '/'
  return t.slice(0, idx)
}

async function toggleOutPicker() {
  outPickerOpen.value = !outPickerOpen.value
  if (!outPickerOpen.value) return
  await ensureRootsLoaded()
  const initial = outDir.value && outDir.value.trim()
    ? outDir.value.trim()
    : (roots.value && roots.value.length ? roots.value[0].path : '')
  if (initial) {
    await openOutPath(initial)
  }
}

function outGoUp() {
  const up = parentPath(outBrowsePath.value)
  if (up && up !== outBrowsePath.value) {
    openOutPath(up)
  }
}

function chooseOutDir() {
  if (!outBrowsePath.value) return
  outDir.value = outBrowsePath.value
  outPickerOpen.value = false
}

function pickReqFolder() {
  if (reqPicker.value) reqPicker.value.click()
}

function pickRulesFolder() {
  if (rulesPicker.value) rulesPicker.value.click()
}

function onReqFolderChange(e) {
  const files = Array.from(e?.target?.files || [])
  const filtered = files.filter((f) => {
    const n = String(f.name || '').toLowerCase()
    return n.endsWith('.docx') || n.endsWith('.doc')
  })
  reqFiles.value = filtered
  reqFolderLabel.value = guessTopFolderName(files)
  if (filtered.length) {
    reqDir.value = ''
  }
}

function onRulesFolderChange(e) {
  const files = Array.from(e?.target?.files || [])
  const filtered = files.filter((f) => {
    const n = String(f.name || '').toLowerCase()
    return n.endsWith('.md') || n.endsWith('.docx')
  })
  rulesFiles.value = filtered
  rulesFolderLabel.value = guessTopFolderName(files)
  if (filtered.length) {
    rulesDir.value = ''
  }
}

function onReqPathInput(ev) {
  reqDir.value = String(ev?.target?.value || '')
  if (reqDir.value.trim()) {
    reqFiles.value = []
    reqFolderLabel.value = ''
  }
}

function onRulesPathInput(ev) {
  rulesDir.value = String(ev?.target?.value || '')
  if (rulesDir.value.trim()) {
    rulesFiles.value = []
    rulesFolderLabel.value = ''
  }
}

function guessTopFolderName(files) {
  const f = files && files.length ? files[0] : null
  const rel = f && f.webkitRelativePath ? String(f.webkitRelativePath) : ''
  if (!rel) return ''
  const parts = rel.split('/')
  return parts && parts.length ? parts[0] : ''
}

async function startScan() {
  if (!reqFiles.value.length && !reqDir.value.trim()) {
    statusText.value = '请选择需求文件夹，或手工输入服务器目录路径'
    return
  }
  showConfig.value = false
  statusText.value = '启动扫描...'
  progressFiles.value = []
  progressData.value = null
  selectedFile.value = '__ALL__'
  issuesByFile.value = {}
  notifications.value = []
  notifOpen.value = false
  highlightKey.value = ''
  knownIssueKeys.clear()
  try {
    let data
    if (reqFiles.value.length) {
      const fd = new FormData()
      for (const f of reqFiles.value) fd.append('req_files', f, f.name)
      for (const rf of rulesFiles.value) fd.append('rules_files', rf, rf.name)
      data = await postForm('/api/scan_upload', fd)
    } else {
      data = await postJson('/api/scan', { req_dir: reqDir.value, out_dir: outDir.value, rules_dir: rulesDir.value })
    }
    jobId.value = data?.job_id || ''
    if (!jobId.value) {
      statusText.value = data?.error || '启动失败'
      return
    }
    pollTimer.value = setInterval(pollStatus, 1000)
    await pollStatus()
  } catch (e) {
    statusText.value = String(e)
  }
}

async function pollStatus() {
  if (!jobId.value) return
  try {
    const data = await getJson('/api/status/' + encodeURIComponent(jobId.value))
    const p = data?.progress
    progressData.value = p || null
    progressFiles.value = (p?.files || []).slice().sort((a, b) => String(a.file_name || '').localeCompare(String(b.file_name || '')))
    const nextIssues = {}
    for (const it of (p?.files || [])) {
      const name = String(it?.file_name || '')
      if (!name) continue
      const issues = Array.isArray(it?.issues) ? it.issues : null
      if (issues) nextIssues[name] = issues
    }
    if (Object.keys(nextIssues).length) {
      issuesByFile.value = { ...(issuesByFile.value || {}), ...nextIssues }
      pushNotifications(nextIssues)
    }
    const logs = Array.isArray(data?.logs) ? data.logs : []
    const base = (data?.error ? ('失败: ' + data.error) : (data?.message || ''))
    statusText.value = logs.length ? (logs.join('\n') + (base ? ('\n' + base) : '')) : base
    if (data?.status === 'done' || data?.status === 'error') {
      if (pollTimer.value) {
        clearInterval(pollTimer.value)
        pollTimer.value = null
      }
    }
  } catch (e) {
    statusText.value = String(e)
    if (pollTimer.value) {
      clearInterval(pollTimer.value)
      pollTimer.value = null
    }
  }
}

function download(fileName) {
  if (!jobId.value) return
  const url = '/api/download?job_id=' + encodeURIComponent(jobId.value) + '&file_name=' + encodeURIComponent(fileName)
  window.location.href = url
}

async function selectFile(fileName) {
  const name = String(fileName || '')
  if (!name || !jobId.value) return
  selectedFile.value = name
  if ((issuesByFile.value || {})[name]) {
    return
  }
  try {
    const data = await getJson('/api/issues?job_id=' + encodeURIComponent(jobId.value) + '&file_name=' + encodeURIComponent(name))
    issuesByFile.value = { ...(issuesByFile.value || {}), [name]: (data?.issues || []) }
  } catch (e) {
    issuesByFile.value = { ...(issuesByFile.value || {}), [name]: [] }
  }
}

function onSelectedFileChange() {
  const v = selectedFile.value
  if (v && v !== '__ALL__') {
    selectFile(v)
  }
}

onMounted(loadCache)

onBeforeUnmount(() => {
  if (pollTimer.value) clearInterval(pollTimer.value)
})
</script>
