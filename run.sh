#!/bin/bash

# Check if at least 3 arguments are provided
if [ "$#" -lt 3 ]; then
    echo "Usage: $0 TIMESTAMP ZONE PARAMS"
    exit 1
fi

# Assign the first two parameters to TIMESTAMP and ZONE
TIMESTAMP="$1"
ZONE="$2"
shift 2  # Shift away the first two arguments so that $@ now contains only the Java params

# Run the Java jar with the provided parameters
sudo java -jar target/VMStresser-1.0-SNAPSHOT.jar "$@"

# Copy the stresser.log file from the remote instance to the specified Google Cloud Storage bucket
sudo gcloud compute ssh hamdanfurat@sut --zone "$ZONE" -- \
    "gsutil cp '/stresser.log' 'gs://duet-benchmarking-results/${TIMESTAMP}/stresser.log'"
