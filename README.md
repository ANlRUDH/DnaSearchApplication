# DNA Sequence Search Engine

A high-performance DNA sequence search engine built with Spring Boot, featuring parallel processing and advanced similarity search capabilities.

## Features

- Parallel DNA sequence searching
- Similarity-based sequence matching
- Configurable thread pools for different workloads
- Performance monitoring and metrics
- RESTful API endpoints

## Technical Stack

- Java 11
- Spring Boot 2.7.0
- PostgreSQL
- Spring Security
- JWT Authentication
- Lombok

## API Endpoints

### Search Operations
- `GET /api/dna/search/parallel?pattern={pattern}&chunkSize={size}` - Parallel pattern search
- `GET /api/dna/similar/parallel?sequence={seq}&threshold={thresh}&maxThreads={threads}` - Parallel similarity search
- `GET /api/dna/metrics` - Get search metrics

## Getting Started

1. Clone the repository
2. Configure PostgreSQL database
3. Update application.properties with your database credentials
4. Run the application using Maven:
   ```bash
   mvn spring-boot:run
   ```

## Configuration

The application uses multiple thread pools for different operations:
- DNA Search Thread Pool: Core=4, Max=8
- Sequence Analysis Thread Pool: Core=2, Max=4
- Monitored Thread Pool: Core=4, Max=8

## Performance

The application implements parallel processing for:
- Pattern matching
- Sequence similarity calculations
- Chunk-based processing
- Dynamic thread allocation 