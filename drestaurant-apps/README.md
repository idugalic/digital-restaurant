## Applications
#### :octocat: /drestaurant-apps :octocat:

We have created more 'web' applications (standalone Spring Boot applications) to demonstrate the use of different architectural styles, API designs (adapters) and deployment strategies by utilizing components from the domain layer in different way.

This is a thin layer of [adapters](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together) that surrounds our domain layer, and exposes it to the outside world. It does not contain business logic. It does not hold the state of the business objects.

Our Driving Adapters are mostly Controllers (REST and/or WebSockets) who are **injected in their constructor with the concrete implementation of the interface (port) from the Domain layer**.
These interfaces (ports) and their implementations are provided by Axon platform:
 - `command bus` (`command gataway` as an convenient facade)
 - `query bus` (`query gataway` as an convenient facade)

Adapters are adapting the HTTP and/or WebSocket interfaces to the domain interfaces (ports) by converting requests to messages/domain API (commands, queries) and publishing them on the bus.


Our Driven Adapters **are implementations of domain interfaces (ports)** that are responsible for persisting (e.g event sourced aggregates), publishing and handling domain events mostly.
Event handlers are creating read only projections that are persisted in JPA repositories, forming the query (read) side.
These interfaces (ports) and their implementations are provided by Axon platform
 - `evensourcing repository`
 - `event bus`
 - ...

**Monolithic**

 - [Monolith 1](drestaurant-monolith) 
    - **HTTP and WebSockets API** by segregating Command and Query
    - we don't synchronize on the backend
    - we provide WebSockets for the frontend to handle async nature of the backend
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)
 - [Monolith 2](drestaurant-monolith-rest)
    - **REST API** by not segregating Command and Query
    - we synchronize on the backend side
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)
 - [Monolith 3](drestaurant-monolith-websockets)
    - **WebSockets API**
    - we are async all the way
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)

**Microservices (`decomposed` monoliths)**

 - [Microservices 1](drestaurant-microservices)
    - **HTTP and WebSockets API** by segregating Command and Query
    - [Monolith 1](drestaurant-monolith) as a monolithic version
    - we don't synchronize on the backend
    - we provide WebSockets for the frontend to handle async nature of the backend
    - we use Apache Kafka to distribute events between services
    - we use Spring Cloud discovery and registry service (Eureka) to distribute commands between services (bounded contexts)
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)

 - [Microservices 2.1](drestaurant-microservices-rest)
    - **REST API** by not segregating Command and Query
    - [Monolith 2](drestaurant-monolith-rest) as a monolithic version
    - we synchronize on the backend side
    - we use RabbitMQ to distribute events between services (bounded contexts)
    - we use Spring Cloud discovery and registry service (Eureka) to distribute commands between services (bounded contexts)
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)
    
 - [Microservices 2.2](drestaurant-microservices-rest-2)
     - **REST API** by not segregating Command and Query
     - [Monolith 2](drestaurant-monolith-rest) as a monolithic version
     - we synchronize on the backend side
     - we are async all the way
     - we use [AxonServer](https://axoniq.io/product-overview/axon-server) as event store, and to distribute messages (commands, events and queries)
     - we use H2 SQL database to store materialized views (query side)
     - additionally, we distribute `queries` with AxonServer, so we can extract `query` side in a separate service (ver `Microservices 2.1` does not support this)
     
 - [Microservices 3](drestaurant-microservices-websockets)
    - Monolith 3 decomposed
    - **WebSockets API**
    - [Monolith 3](drestaurant-monolith-websockets) as a monolithic version
    - we are async all the way
    - we use [AxonServer](https://axoniq.io/product-overview/axon-server) as event store, and to distribute messages (commands, events and queries)
    - we use H2 SQL database to store materialized views (query side)
