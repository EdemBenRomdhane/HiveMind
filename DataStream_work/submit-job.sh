#!/bin/bash

# Configuration
JOB_MANAGER_HOST=${JOB_MANAGER_RPC_ADDRESS:-jobmanager}
JOB_MANAGER_PORT=${JOB_MANAGER_RPC_PORT:-8081}
JAR_PATH=$(find /opt/flink/usrlib -name "flink-job.jar" | head -n 1)

echo "Waiting for Flink JobManager at $JOB_MANAGER_HOST:$JOB_MANAGER_PORT..."

# Wait for JobManager to be ready
while ! curl -s "http://$JOB_MANAGER_HOST:$JOB_MANAGER_PORT/config" > /dev/null; do
  echo "JobManager not ready yet... sleeping"
  sleep 5
done

echo "JobManager is ready."

if [ -z "$JAR_PATH" ]; then
    echo "Error: No JAR file found in /opt/flink/usrlib"
    exit 1
fi

echo "Found JAR: $JAR_PATH"

# List running jobs to avoid duplicates (optional, basic check)
RUNNING_JOBS=$(curl -s "http://$JOB_MANAGER_HOST:$JOB_MANAGER_PORT/jobs" | grep -o "RUNNING")

if [ ! -z "$RUNNING_JOBS" ]; then
   echo "Jobs are already running. Skipping submission or handle accordingly."
   # You could implement logic here to cancel existing jobs if needed
else
   echo "Submitting job..."
   flink run -d -m $JOB_MANAGER_HOST:$JOB_MANAGER_PORT -c com.hivemind.datastream.DataStreamJob "$JAR_PATH"
   echo "Job submitted."
fi

# Keep the container alive if you want logs, or exit
# tail -f /dev/null
