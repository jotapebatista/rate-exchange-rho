# Rate Exchange API

The Rate Exchange API is a service that provides currency exchange rate information and allows you to perform currency conversion operations.

## Table of Contents
- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage with Maven](#usage-with-maven)
- [Usage with Docker](#usage-with-docker)
- [Endpoints](#endpoints)
- [Documentation](#documentation)

## Getting Started

This section provides an overview of how to set up and run the Rate Exchange API locally for development or testing purposes.

### Prerequisites

Before you begin, ensure you have met the following requirements:

- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/javase-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Docker](https://www.docker.com/get-started) (optional, for containerization)

### Installation

1. Clone the repository:

   ```shell
   git clone https://github.com/yourusername/rate-exchange-api.git
   ```

2. Navigate to the project directory:

   ```shell
   cd rate-exchange-api
   ```

3. Build the project using Maven:

   ```shell
   mvn clean install
   ```

### Usage with Maven

To run the Rate Exchange API locally using Maven, use the following command:

```shell
mvn spring-boot:run
```

By default, the API will be available at `http://localhost:8080`.

### Usage with Docker

To containerize the Rate Exchange API using Docker, follow these steps:

1. Make sure you have Docker installed on your system.

2. Build the Docker image:

   ```shell
   docker build -t rate-exchange-api .
   ```

3. Run the Docker container:

   ```shell
   docker run -d -p 8080:8080 rate-exchange-api
   ```

The Rate Exchange API will be available within the Docker container, and you can access it at `http://localhost:8080` on your machine.

## Endpoints

The Rate Exchange API provides the following endpoints:

- `GET /api/exchange-rate`: Get the exchange rate between two currencies.
- `GET /api/rates`: Get exchange rates for a specific base currency.
- `GET /api/convert`: Convert a specific amount from one currency to another.
- `GET /api/convert-multiple`: Convert an amount to multiple target currencies.

## Documentation

The API documentation can be found at `http://localhost:8080/swagger-ui/index.html`. It provides detailed information on each API endpoint, request parameters, and responses.
