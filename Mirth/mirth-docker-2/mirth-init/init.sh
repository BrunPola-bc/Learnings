#!/bin/sh
set -e

# ----- CONFIGURATION -----
MIRTH_URL="https://mc:8443"
INITIALIZATION_MARKER="/opt/connect/appdata/.initialized"
USER=$(cat /run/secrets/mirth_admin_user)
PASS=$(cat /run/secrets/mirth_admin_pass)

# -------------------------

# Check marker file
if [ -f "$INITIALIZATION_MARKER" ]; then
  echo "Server previously initialized. Exiting."
  exit 0
fi

echo "Waiting for Mirth to be ready..."

while true; do
    HTTP_CODE=$(
      curl -s -k \
        -u "$USER:$PASS" \
        -H "Accept: application/xml" \
        -H "X-Requested-With: OpenAPI" \
        -o /dev/null \
        -w "%{http_code}" \
        "$MIRTH_URL/api/server/status" \
      || echo "000"
    )

    [ "$HTTP_CODE" -eq 200 ] && break
    sleep 3
done

echo "Mirth is ready!"

# Restore server configuration
HTTP_RESPONSE=$(
  curl -s -k \
    -u "$USER:$PASS" \
    -H "Content-Type: application/xml" \
    -H "X-Requested-With: OpenAPI" \
    -X PUT \
    --data-binary @/mirth-init/server-config-backup.xml \
    -o /tmp/mirth_response.xml \
    -w "%{http_code}" \
    "$MIRTH_URL/api/server/configuration"
)


if [ "$HTTP_RESPONSE" -ge 200 ] && [ "$HTTP_RESPONSE" -lt 300 ]; then
  echo "Configuration restored successfully (HTTP $HTTP_RESPONSE)"
else
  echo "ERROR restoring configuration (HTTP $HTTP_RESPONSE)"
  cat /tmp/mirth_response.xml
  exit 1
fi

# Create marker file
touch "$INITIALIZATION_MARKER"

echo "Initialization complete."
