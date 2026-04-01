# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Blaze-Query is a multi-platform querying library that lets you run SQL queries against cloud infrastructure and SaaS platform APIs. It uses Apache Calcite for SQL parsing/optimization and fetches data from cloud providers via a connector pattern. Data is cached in-memory within a QuerySession.

## Build Commands

```bash
# Build and run all tests
./gradlew check -Plog-test-progress=true --stacktrace

# Run tests for a single module
./gradlew :blaze-query-connector-aws-ec2:check

# Format check (CI uses this)
./gradlew spotlessCheck

# Apply formatting (runs automatically before compile)
./gradlew spotlessApply
```

Java 17 is required. The build uses Gradle with parallel execution enabled.

## Code Style

- Tab indentation (4-space width)
- All source files must have the SPDX Apache 2.0 license header (managed by Spotless)
- Spotless runs automatically before compilation (`compileJava.dependsOn spotlessApply`)
- Unused imports are removed automatically

## Architecture

### Core (`core/`)

- **`core/api`** — Public interfaces: `QueryContext`, `QuerySession`, `TypedQuery`, `Metamodel`
- **`core/api` SPI** (`com.blazebit.query.spi`) — `DataFetcher<T>`, `DataFormat`, `QuerySchemaProvider`, `DataFetcherConfig<T>`
- **`core/impl`** — Query engine implementation using Apache Calcite. Parses SQL, builds execution plans, calls DataFetchers, converts types, returns results.

Entry point: `Queries.createQueryContextBuilder().loadServices().build()` creates a `QueryContext`. From there, `createSession()` gives a `QuerySession` (not thread-safe) that caches fetched data and executes queries.

### Connector Pattern (`connector/`)

Each connector module follows this structure:

1. **Schema objects** — POJOs/wrappers around cloud SDK types (e.g., `GcpInstance` wraps `com.google.cloud.compute.v1.Instance`)
2. **DataFetcher implementation** — Implements `DataFetcher<T>`, calls cloud SDK to fetch data, returns wrapped objects. Defines its `DataFormat` using `DataFormats` utility from connector-base with conventions like `beansConvention()` or `fieldsConvention()`.
3. **QuerySchemaProvider** — Implements `QuerySchemaProvider`, registered via `META-INF/services/com.blazebit.query.spi.QuerySchemaProvider`. Returns the set of DataFetcher instances for the module.
4. **ConventionContext** (optional) — Filters which fields from cloud SDK types are exposed as queryable columns.

### Connector Base (`connector/base/`)

Provides `DataFormats` utility for introspecting Java types into `DataFormat` definitions, and `ConventionContext` for filtering fields. All connectors depend on this.

### Module Naming

```
blaze-query-connector-{platform}-{service}
blaze-query-connector-{platform}-{service}-{variant}  # e.g., -jersey3
```

Gradle project names use colons: `:blaze-query-connector-aws-ec2`

### Connector Platforms

- **AWS** (`connector/aws/`) — EC2, ECS, EKS, Lambda, RDS, S3, IAM, KMS, CloudWatch, CloudTrail, SNS, etc. Uses `connector/aws/base` for shared AWS config.
- **Azure** (`connector/azure/`) — Resource Manager, Microsoft Graph
- **Google Cloud** (`connector/google/gcp/`) — Compute, IAM, Storage. Uses `connector/google/gcp/base` for shared GCP config.
- **Google Workspace** (`connector/google/workspace/`) — Directory, Drive
- **GitHub** (`connector/github*/`) — REST (OpenAPI-generated) and GraphQL
- **GitLab** (`connector/gitlab/`)
- **Jira** (`connector/jira/`) — Cloud and DataCenter variants with Jersey3 alternatives
- **Kandji** (`connector/kandji/`)

## Adding a New Connector

1. Create module directory under `connector/{platform}/{service}/`
2. Add to `settings.gradle` with include and projectDir mapping
3. Implement `DataFetcher<T>` with `getDataFormat()` and `fetch()`
4. Implement `QuerySchemaProvider` returning your fetcher(s)
5. Register via `META-INF/services/com.blazebit.query.spi.QuerySchemaProvider`
6. Use `DataFormats.beansConvention()` or `fieldsConvention()` for DataFormat definition
7. Optionally implement `ConventionContext` to filter out non-queryable fields
