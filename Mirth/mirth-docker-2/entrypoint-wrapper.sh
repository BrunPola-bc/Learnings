#!/bin/bash
set -e

# ---------------------------
# Wrapper entrypoint for Mirth Connect
# ---------------------------

MIRTH_INPUT_DIR="/opt/connect/input"
CHANNELS_DIR="/opt/connect/appdata/channels"

# Detect first startup
if [ ! -d "$CHANNELS_DIR" ]; then
    echo "First startup detected: creating channels directory."
    mkdir -p "$CHANNELS_DIR"
    chown -R mirth:mirth "$CHANNELS_DIR"
fi

# Import channels if the channels folder is empty
if [ ! "$(ls -A $CHANNELS_DIR 2>/dev/null)" ]; then
    echo "Channels directory empty: importing exported channel group(s)."
    if [ -d "$MIRTH_INPUT_DIR" ]; then
        for file in "$MIRTH_INPUT_DIR"/*.xml; do
            if [ -f "$file" ]; then
                echo "Copying $file to $CHANNELS_DIR"
                cp "$file" "$CHANNELS_DIR/"
                chown mirth:mirth "$CHANNELS_DIR/$(basename "$file")"
            fi
        done
    else
        echo "Warning: Mirth input directory $MIRTH_INPUT_DIR does not exist."
    fi
else
    echo "Channels directory already populated, skipping import."
fi

# ---------------------------
# Call the original Mirth entrypoint with CMD
# ---------------------------
exec /entrypoint.sh "$@"
