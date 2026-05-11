#!/usr/bin/env bash
set -euo pipefail

default_server="root@8.152.199.125"
server="${1:-${default_server}}"
if [[ "${server}" == "--"* ]]; then
  server="${default_server}"
else
  shift || true
fi

port="9000"
container_name="spec-qc-9000"
server_dir="/opt/spec-qc-9000"
data_dir="/opt/spec-qc-data"
build_image="docker.m.daocloud.io/library/maven:3.9.10-eclipse-temurin-17"
runtime_image="docker.m.daocloud.io/library/eclipse-temurin:17-jre"
image_tag="spec-qc:9000"

while [[ $# -gt 0 ]]; do
  case "$1" in
    --port)
      port="${2:-}"
      shift 2
      ;;
    --name)
      container_name="${2:-}"
      shift 2
      ;;
    --server-dir)
      server_dir="${2:-}"
      shift 2
      ;;
    --data-dir)
      data_dir="${2:-}"
      shift 2
      ;;
    --build-image)
      build_image="${2:-}"
      shift 2
      ;;
    --runtime-image)
      runtime_image="${2:-}"
      shift 2
      ;;
    --tag)
      image_tag="${2:-}"
      shift 2
      ;;
    *)
      echo "Unknown arg: $1"
      exit 2
      ;;
  esac
done

root_dir="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

tmp_name="spec-qc-src_$(date +%Y%m%d_%H%M%S).tgz"
tmp_local="/tmp/${tmp_name}"
tar -C "${root_dir}" -czf "${tmp_local}" Dockerfile backend frontend work

if command -v shasum >/dev/null 2>&1; then
  local_sha="$(shasum -a 256 "${tmp_local}" | awk '{print $1}')"
else
  local_sha="$(openssl dgst -sha256 "${tmp_local}" | awk '{print $2}')"
fi

echo "LOCAL_TGZ=${tmp_local}"
echo "LOCAL_SHA256=${local_sha}"

tmp_remote="/tmp/${tmp_name}"
scp "${tmp_local}" "${server}:${tmp_remote}"

ssh "${server}" bash -lc "set -euo pipefail
echo REMOTE_TGZ='${tmp_remote}'
if command -v sha256sum >/dev/null 2>&1; then
  remote_sha=\$(sha256sum '${tmp_remote}' | awk '{print \$1}')
else
  remote_sha=\$(python3 - <<'PY'
import hashlib
p='${tmp_remote}'
h=hashlib.sha256()
with open(p,'rb') as f:
  for b in iter(lambda: f.read(1024*1024), b''):
    h.update(b)
print(h.hexdigest())
PY
  )
fi
echo REMOTE_SHA256=\${remote_sha}
test \"\${remote_sha}\" = '${local_sha}'

mkdir -p '${server_dir}'
tar -xzf '${tmp_remote}' -C '${server_dir}'

docker build \
  --build-arg BUILD_IMAGE='${build_image}' \
  --build-arg RUNTIME_IMAGE='${runtime_image}' \
  -t '${image_tag}' '${server_dir}'

in_use=\$(docker ps --format '{{.Names}}\t{{.Ports}}' | awk -v p=':${port}->' 'index(\$0, p) {print \$1; exit 0}')
if [[ -n \"\${in_use}\" && \"\${in_use}\" != '${container_name}' ]]; then
  echo \"ERROR: port ${port} already in use by container: \${in_use}\"
  docker ps --format 'CONTAINER={{.Names}} {{.Status}} {{.Ports}}' | awk -v name=\"\${in_use}\" '\$0 ~ name {print}'
  exit 4
fi

mkdir -p '${data_dir}/work/quality'
if [[ ! -f '${data_dir}/work/quality/quality_standard.md' && -f '${server_dir}/work/quality/quality_standard.md' ]]; then
  cp '${server_dir}/work/quality/quality_standard.md' '${data_dir}/work/quality/quality_standard.md'
fi
if [[ ! -f '${data_dir}/work/quality/banking_quality_standard.md' && -f '${server_dir}/work/quality/banking_quality_standard.md' ]]; then
  cp '${server_dir}/work/quality/banking_quality_standard.md' '${data_dir}/work/quality/banking_quality_standard.md'
fi

docker rm -f '${container_name}' >/dev/null 2>&1 || true
docker run -d --restart unless-stopped \
  --name '${container_name}' \
  -p ${port}:8765 \
  -v '${data_dir}:/data' \
  -e SPEC_QC_CONFIG='/data/config.yaml' \
  '${image_tag}' web

docker ps --filter name='${container_name}' --format 'CONTAINER={{.Names}} {{.Status}} {{.Ports}}' | tee /tmp/spec-qc-${port}.ps.txt
grep -q \"\\b${port}->\" /tmp/spec-qc-${port}.ps.txt
"

echo "OK: http://8.152.199.125:${port}/"
