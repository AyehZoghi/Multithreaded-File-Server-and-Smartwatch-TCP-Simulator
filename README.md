# Multithreaded File Server and Smartwatch TCP Simulator

This project was developed for an Operating Systems course. It implements a multithreaded TCP server in Java and demonstrates core operating-system concepts such as socket programming, thread pools, synchronization, mutual exclusion, file I/O, and shared-resource management.

The project has two main parts:

1. A multithreaded file server that handles multiple clients and supports basic file operations.
2. A smartwatch simulation in which multiple watch clients connect to the same server, periodically send health/location data, and receive commands from the server.

## Features

- TCP server implemented in Java
- Concurrent client handling using a fixed-size thread pool
- Support for multiple client connections
- Basic file operations:
  - Create file
  - Read file
  - Write to file
  - Delete file
- Synchronization for shared file access
- Per-file read/write lock structure
- Smartwatch client simulation
- Unique IMEI generation for watch clients
- Periodic health-data transmission
- Periodic location-data transmission
- Server-to-watch commands:
  - `FIND`
  - `POWEROFF`
- Thread-safe activity logging using a lock
- Simple file-manager test class

## Project Structure

```text
OS_PROJECT/
├── src/
│   ├── Server.java              # Main TCP server
│   ├── Client.java              # Basic file client
│   ├── ClientHandler.java       # Handles each connected client/watch
│   ├── ClientManager.java       # Interface for client command managers
│   ├── FileManager.java         # File operations and synchronization logic
│   ├── FileManagerTest.java     # Basic tests for file operations
│   ├── GeoPoint.java            # Stores latitude and longitude
│   ├── LogManager.java          # Thread-safe logging
│   ├── ReadWriteLock.java       # Simple read/write lock state class
│   ├── Watch.java               # Smartwatch client simulator
│   ├── WatchManager.java        # Handles watch messages on the server side
│   └── server_watch_log.txt     # Example watch activity log
├── final_project_os_1403.pdf    # Original project description
└── README.md
```

## Technologies Used

- Java
- TCP sockets
- Java threads
- ExecutorService
- ReentrantLock
- Condition variables
- File I/O

## Requirements

- Java JDK 14 or newer

The code uses modern Java switch syntax, so older Java versions may not compile it correctly.

## How to Compile

From the `src` directory, run:

```bash
javac *.java
```

## How to Run

### 1. Start the Server

Open a terminal in the `src` folder and run:

```bash
java Server
```

The server listens on:

```text
localhost:12345
```

### 2. Run a File Client

Open another terminal in the same `src` folder and run:

```bash
java Client
```

After connecting, the client sends the initial message `CLIENT` to the server. You can then type file commands from the client terminal.

Example commands:

```text
CREATE test.txt
WRITE test.txt Hello world
READ test.txt
DELETE test.txt
```

### 3. Run a Watch Client

Open another terminal and run:

```bash
java Watch
```

The watch client connects to the server, generates an IMEI, and starts sending simulated location and health data.

Example messages sent by the watch:

```text
3G*1662238756*HEALTH 91 107 118
3G*1662238756*UD 8.489684506403178 -166.15454914287142
```

### 4. Send Commands from the Server to a Watch

From the server terminal, commands can be sent to a connected watch using its IMEI.

Example:

```text
3G*1662238756*FIND
```

This simulates finding the watch by making it ring for 30 seconds.

Example:

```text
3G*1662238756*POWEROFF
```

This shuts down the watch client.

## File Client Commands

| Command | Format | Description |
|---|---|---|
| `CREATE` | `CREATE filename.txt` | Creates a new file |
| `READ` | `READ filename.txt` | Reads and prints the file content |
| `WRITE` | `WRITE filename.txt content` | Appends content to the file |
| `DELETE` | `DELETE filename.txt` | Deletes the file |

## Smartwatch Message Format

The smartwatch part follows the message format described in the project assignment.

| Message Type | Format | Direction |
|---|---|---|
| Power off | `3G*IMEI*POWEROFF` | Server to watch |
| Find watch | `3G*IMEI*FIND` | Server to watch |
| Health data | `3G*IMEI*HEALTH heartRate bloodPressureLow bloodPressureHigh` | Watch to server |
| Location data | `3G*IMEI*UD latitude longitude` | Watch to server |

## Synchronization Design

The project uses synchronization to protect shared resources and avoid race conditions.

In the file-server part, each file has a simple read/write lock state. The `FileManager` uses a global lock and condition variable to coordinate access to file operations. This prevents multiple conflicting operations from modifying the same file at the same time.

In the smartwatch part, the `LogManager` uses a lock to make sure multiple watch clients can safely write to the shared log file without corrupting the log output.

## Testing

The project includes a basic test class:

```bash
java FileManagerTest
```

This class tests file creation, deletion, reading, and writing behavior.

Example output:

```text
Test Create File: PASSED
Test Delete File: PASSED
Test Read File: PASSED
Test Write File: PASSED
```

## Notes

- The server uses port `12345`.
- The project uses the default Java package, so all `.java` files should stay in the same folder unless packages are added later.
- The smartwatch data is simulated using random values.
- The project is mainly educational and demonstrates operating-system concepts such as concurrency, synchronization, and shared-resource access.
