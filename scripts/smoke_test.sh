#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${1:?usage: smoke_test.sh <base-url> <version> [attempts]}"
VERSION="${2:?usage: smoke_test.sh <base-url> <version> [attempts]}"
ATTEMPTS="${3:-20}"

for attempt in $(seq 1 "$ATTEMPTS"); do
  if curl -fsS "$BASE_URL/health" | jq -e --arg v "$VERSION" '.version == $v' > /dev/null; then
    if curl -fsS "$BASE_URL/add?a=2&b=3" | jq -e '.result == 5' > /dev/null; then
      echo "OK: $BASE_URL serves version $VERSION and passes the functional check (attempt $attempt)"
      exit 0
    fi
    echo "FAIL: version $VERSION is live but /add returned the wrong result"
    exit 1
  fi
  echo "attempt $attempt: version $VERSION not live yet"
  sleep 15
done

echo "FAIL: $BASE_URL did not report version $VERSION after $ATTEMPTS attempts"
exit 1
