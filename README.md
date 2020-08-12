# RateLimiter

[![N|Solid](https://cldup.com/dTxpPi9lDf.thumb.png)](https://nodesource.com/products/nsolid)

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

## Introduction
RateLimiter is portable and extensible module which controle request rate. It has the following features
  - Dependency Injection with simple implementation (can be refactored with [Guice](https://github.com/google/guice))
  - Implemented two rate limiting algorithm
    - Token-based, supporting both memory and distributed (framework) implementation
    - Counter-based with sliding window
  - Extensible and Configurable modules

## Installation and Testing
Install the prerequisites.
- [jdk8](https://www.oracle.com/java/technologies/javase-jre8-downloads.html)
- [Maven](https://maven.apache.org/)

Compile and run unit test
```sh
$ mvn clean test
```

Run integration test
```sh
$ mvn failsafe:integration-test
```

Package generation and run all tests
```sh
$ mvn install
```

## Module Summary
### RateLimiter
`com.dguo.ratelimiter.RateLimiter` is the entry point of the module.

### Config
`com.dguo.ratelimiter.config`
Put the config manager implementation which is responsible for rate limit config for different client and request. In the current version, only the static config is implemented.

### Strategy
`com.dguo.ratelimiter.strategy`
Define the rate limiting strategy interface, and two popular strategies have been implemented. One is the **TokenBased** and the other is **CounterBased**.

### Token
`com.dguo.ratelimiter.token.consumer`
The memory-based token algorithm has been implemented.
Token-based rate limiting with active request mode is popular in distributed environments. For example, the **token-consumer** is running in multiple load-balancer servers; and the **token-producer** which is running on different server(s) is responsible for responding the token request from consumer, and maintaining the global token consistency. In current version, only the memory-based consumer has been implemented.

### Counter
`com.dguo.ratelimiter.counter`
Only the memory-based sliding-window counter algorithm has been implemented.

### Resource
`bean.properties` is the file to define **dependency injection**.

### Integration Test
`com.dguo.ratelimiter.integration.RateLimiterIT` implements integration test cases both for TokenMemoryBasedStrategy and SlideWindowCounterStrategy.

## Future Development
- Guice usage
- DynamoDB && File config manager
- Token Agent (REST API or RPC)
- Token Producer which is running on distributed platform
- Data Persistence
