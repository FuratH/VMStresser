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

### Command-line Options 

### 🧰 **General Options**

| Option              | Description                                |
|---------------------|--------------------------------------------|
| `--help`            | Displays help message                      |
| `--list-stressors`  | Lists all available stressors              |


### 🧪 **Enable Stressors**

| Option                  | Description                         |
|--------------------------|-------------------------------------|
| `--cpu`                 | Enable CPU stressor                 |
| `--memory`              | Enable memory stressor              |
| `--disk`                | Enable disk stressor                |
| `--network`             | Enable network stressor             |
| `--file-descriptors`    | Enable file descriptor stressor     |
| `--threads`             | Enable thread stressor              |


### ⏱️ **Timing Configuration**

| Option              | Description                                                       |
|---------------------|-------------------------------------------------------------------|
| `--duration`        | Duration of the stress test in **minutes** (default from config)  |
| `--wait-time`       | Time to wait **before** starting the test in **minutes** (default: 0) |


### ⚙️ **Stressor-Specific Configuration**

#### CPU

| Option            | Description                                      |
|-------------------|--------------------------------------------------|
| `--cpu-threads`   | Number of threads for CPU stress                 |

#### Memory

| Option               | Description                                       |
|----------------------|---------------------------------------------------|
| `--memory-arrays`    | Number of byte arrays to allocate                 |
| `--array-size`       | Size of each array in **MB**                      |

#### Disk

| Option                 | Description                                     |
|------------------------|-------------------------------------------------|
| `--disk-file-count`    | Number of files to write during disk stress     |
| `--disk-file-size`     | Size of each file in **MB**                     |

#### Network

| Option             | Description                                         |
|--------------------|-----------------------------------------------------|
| `--network-url`    | URL to use for downloading data during network stress |


### 📉 **Resource Limiters**

| Option               | Description                                      |
|----------------------|--------------------------------------------------|
| `--limit-cpu`        | Max CPU usage percentage (e.g., 80 for 80%)      |
| `--limit-memory`     | Max memory usage in **MB**                       |
| `--limit-disk-io`    | Max disk I/O speed in **MB/s**                   |
| `--limit-network`    | Max network bandwidth in **Mbps**                |


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
- Upload `stresser.log` to a bucket that has to be defined in run.sh

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
java -jar target/VMStresser-1.0-SNAPSHOT.jar --cpu --cpu-threads 60 --duration 5 --wait-time 3
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
