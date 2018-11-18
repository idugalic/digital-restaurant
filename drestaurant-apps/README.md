## Applications
#### :octocat: [digital-restaurant](https://github.com/idugalic/digital-restaurant)/drestaurant-apps :octocat:

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

 - [Monolith 1](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith)
    - **HTTP and WebSockets API** by segregating Command and Query
    - we don't synchronize on the backend
    - we provide WebSockets for the frontend to handle async nature of the backend
 - [Monolith 2](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest)
    - **REST API** by not segregating Command and Query
    - we synchronize on the backend side
 - [Monolith 3](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-websockets)
    - **WebSockets API**
    - we are async all the way

**Microservices (decomposed monoliths)**

 - [Microservices 1](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices)
    - Monolith 1 decomposed
    - **HTTP and WebSockets API** by segregating Command and Query
    - we don't synchronize on the backend
    - we provide WebSockets for the frontend to handle async nature of the backend
    - we use Apache Kafka to distribute events between services
    - we use Spring Cloud discovery and registry service (Eureka) to distribute commands between services (bounded contexts)
 - Microservices 2

    - [Version 1](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-rest)
      - Monolith 2 decomposed
      - **REST API** by not segregating Command and Query
      - we synchronize on the backend side
      - we use RabbitMQ to distribute events between services (bounded contexts)
      - we use Spring Cloud discovery and registry service (Eureka) to distribute commands between services (bounded contexts)
      - **each micorservice has `command` and `query` side components included -> there is NO standalone `query` microservice**
    - [Version 2](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-rest-2)
      - Monolith 2 decomposed
      - **REST API** by not segregating Command and Query
      - we synchronize on the backend side
      - we use [AxonServer](https://axoniq.io/product-overview/axon-server) to route and distribute all type of messages (commands, events and **queries**) between services (bounded contexts)
      - **each micorservice has `command` side component included only -> there is standalone `query` microservice -> we can scale better**
 - [Microservices 3](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-websockets)
    - Monolith 3 decomposed
    - **WebSockets API**
    - we are async all the way
    - we use [AxonServer](https://axoniq.io/product-overview/axon-server) to route and distribute all type of messages (commands, events, queries) between services (bounded contexts)
