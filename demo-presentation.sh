#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

"${SCRIPT_DIR}/demo-admin.sh"
echo ""
"${SCRIPT_DIR}/demo-user.sh"
