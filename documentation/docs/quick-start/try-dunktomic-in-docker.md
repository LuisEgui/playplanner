---
sidebar_position: 1
---

# Local Docker Deployment

This installation doc will help you start Dunktomic standalone instance on your local machine.

## Requirements

Please ensure your host system meets the requirements listed below.

### OSX and Linux

#### Docker (version 20.10.0 or greater)

[Docker](https://docs.docker.com/get-started/overview/) is an open-source platform for developing, shipping, and running applications. It enables you to separate your applications from your infrastructure, so you can deliver software quickly using OS-level virtualization. It helps deliver software in packages called Containers.

To check the version of Docker you have, use the following command.

```bash
docker --version
```

#### Docker Compose (version v2.1.1 or greater)

The Docker ```compose``` package enables you to define and run multi-container Docker applications. The compose command integrates compose functions into the Docker platform, making them available from the Docker command-line interface ( CLI) . The Java packages you will install in the procedure below use compose to deploy Dunktomic.

* **MacOS X**: Docker on MacOS X ships with compose already available in the Docker CLI.
* **Linux**: To install compose on Linux systems, please visit the Docker CLI command documentation and follow the instructions.

To verify that the docker compose command is installed and accessible on your system, run the following command.

```bash
docker compose version
```

Upon running this command you should see output similar to the following.

```bash
Docker Compose version v2.21.0-desktop.1
```
