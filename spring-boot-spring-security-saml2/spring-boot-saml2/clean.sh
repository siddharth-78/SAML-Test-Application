#!/bin/bash

# Check for processes using port 8080
PID=$(sudo lsof -t -i :8080)

# If a PID is found, kill it
if [ ! -z "$PID" ]; then
    echo "Killing process on port 8080 with PID: $PID"
    sudo kill -9 $PID
else
    echo "No process found on port 8080."
fi
