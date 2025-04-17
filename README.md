# VMStresser

**VMStresser** is a modular, extensible, and cloud-compatible Java-based stress testing suite that targets a variety of system resources including CPU, memory, disk, network, file descriptors, and threads. Ideal for benchmarking, diagnostics, and stress testing in virtualized environments like VMs or containers.

---

## Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Automation Script](#automation-script)
- [Configuration](#configuration)
- [Stressors](#stressors)
- [Examples](#examples)
- [Dependencies](#dependencies)
- [Troubleshooting](#troubleshooting)
- [License](#license)

---

## Features

- Targeted stress tests for CPU, memory, disk, network, threads, and file descriptors.
- YAML-based centralized configuration.
- Command-line override for quick experimentation.
- Auto-generated reports and log collection.
- Google Cloud integration for remote execution and result storage.

---

## Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/FuratH/VMStresser.git
   cd VMStresser
   ```

2. **Build the project**
   Ensure you have Maven and JDK installed:
   ```bash
   mvn clean package
   ```

---

## Usage

Run the tool using:

```bash
java -jar target/VMStresser-1.0-SNAPSHOT.jar [options]
```

### Command-line Options (Partial)

```bash
--help                     Show help message
--list-stressors           List all stressors
--cpu                      Enable CPU stress
--memory                   Enable memory stress
--disk                     Enable disk stress
--network                  Enable network stress
--file-descriptors         Enable file descriptor stress
--threads                  Enable thread stress
--duration                 Duration in minutes
--limit-cpu                Max CPU usage (%)
--limit-memory             Max memory in MB
...
```

---

## Automation Script

A helper script `run.sh` is included for executing the stress test remotely and uploading logs to Google Cloud:

### Script Usage

```bash
./run.sh TIMESTAMP ZONE JAVA_PARAMS
```

- **TIMESTAMP**: Folder prefix for GCS storage (e.g. `20250417T1200`)
- **ZONE**: GCP zone of the remote VM (e.g. `us-central1-a`)
- **JAVA_PARAMS**: All arguments passed to the Java application

### Example

```bash
./run.sh 20250417T1200 us-central1-a --cpu --duration 2 --limit-cpu 80
```

This will:
- Run the stress test remotely on the instance `hamdanfurat@sut-firecracker`
- Upload `stresser.log` to: `gs://duet-benchmarking-results/20250417T1200/stresser.log`

---

## Configuration

Put a `config.yaml` file in the classpath:

```yaml
duration: 2
wait_time: 1
report_path: "stresser_report.txt"
cpu:
  threads: 4
memory:
  arrays: 2
  array_size_mb: 512
disk:
  file_count: 10
  file_size_mb: 100
network:
  download_url: "http://example.com"
limits:
  cpu_max_usage_pct: 80
  memory_max_usage_mb: 1024
  disk_max_io_speed_mbps: 20
  network_max_bandwidth_mbps: 10
```

Command-line arguments override config values.

---

## Stressors

| Stressor         | Class                      | Description                                       |
|------------------|----------------------------|---------------------------------------------------|
| CPU              | `CpuStresser`              | High CPU load with throttling support             |
| Memory           | `MemoryStresser`           | Allocate memory in large blocks                   |
| Disk             | `DiskStresser`             | Write large files to disk                         |
| Network          | `NetworkStresser`          | Download data to simulate bandwidth stress        |
| File Descriptors | `FileDescriptorStresser`   | Open many files simultaneously                    |
| Threads          | `ThreadStresser`           | Spawn threads to simulate thread count pressure   |

---

## Examples

**Run with custom CPU and memory stress:**
```bash
java -jar target/VMStresser-1.0-SNAPSHOT.jar --cpu --memory --cpu-threads 8 --limit-memory 2048
```

**Run based only on YAML configuration:**
```bash
java -jar target/VMStresser-1.0-SNAPSHOT.jar --cpu --memory --disk --network
```

---

## Dependencies

- Java 8+
- Maven
- [args4j](http://args4j.kohsuke.org/)
- [SnakeYAML](https://bitbucket.org/asomov/snakeyaml)
- SLF4J for logging

---

## Troubleshooting

| Issue                                      | Solution                                            |
|-------------------------------------------|-----------------------------------------------------|
| Configuration file not found              | Ensure `config.yaml` is in the classpath            |
| Excessive resource consumption            | Use `--limit-*` flags                               |
| Network test fails                        | Confirm the `network.download_url` is accessible    |
| Java jar not found                        | Check if Maven build succeeded (`target/`)          |

---
