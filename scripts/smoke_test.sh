#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:?usage: smoke_test.sh <base-url> <version> [attempts]}"
VERSION="${2:?usage: smoke_test.sh <base-url> <version> [attempts]}"
ATTEMPTS="${3:-40}"

for attempt in $(seq 1 "$ATTEMPTS"); do
  HEALTH=$(curl -fsS "$BASE_URL/health" 2>/dev/null || true)

  if echo "$HEALTH" | jq -e --arg v "$VERSION" '.version == $v' >/dev/null 2>&1; then
    ADD=$(curl -fsS "$BASE_URL/add?a=2&b=3" 2>/dev/null || true)

    if echo "$ADD" | jq -e '.result == 5' >/dev/null 2>&1; then
      echo "OK: $BASE_URL serves version $VERSION and passes the functional check (attempt $attempt)"
      exit 0
    fi
  fi

  echo "Attempt $attempt/$ATTEMPTS: waiting for version $VERSION and functional check"
  sleep 15
done

echo "FAIL: $BASE_URL did not pass health and functional checks for version $VERSION"
exit 1
