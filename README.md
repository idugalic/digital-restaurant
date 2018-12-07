# [projects](http://idugalic.github.io/projects)/digital-restaurant [![Build Status](https://travis-ci.org/idugalic/digital-restaurant.svg?branch=kotlin)](https://travis-ci.org/idugalic/digital-restaurant) [![GitPitch](https://gitpitch.com/assets/badge.svg)](https://gitpitch.com/idugalic/digital-restaurant/master?grs=github&t=white)

'd-restaurant' is an example of an application that is built using Event Sourcing and CQRS. The application is written in [Kotlin][kotlin], and uses [Spring][spring] Boot.
It is built using [Axon](https://axoniq.io/product-overview/axon), which is end-to-end development and infrastructure platform for smoothly evolving event-driven microservices focused on [CQRS](https://microservices.io/patterns/data/cqrs.html) and [Event Sourcing](https://microservices.io/patterns/data/event-sourcing.html) patterns.

**Customers use the website application to place food orders at local restaurants. Application coordinates a network of couriers who deliver the orders.**


## Table of Contents

  * [Domain](#domain)
     * [Core subdomains](#core-subdomains)
        * [Event sourcing](#event-sourcing)
        * [Snapshoting](#snapshoting)
     * [Generic subdomains](#generic-subdomains)
     * [Organisation vs encapsulation](#organisation-vs-encapsulation)
     * [Context mapping](#context-mapping)
  * [Applications](#applications)
  * [Monorepo](#monorepo)
  * [Technology](#technology)
     * [Language](#language)
     * [Frameworks and Platforms](#frameworks-and-platforms)
     * [Continuous Integration and Delivery](#continuous-integration-and-delivery)
     * [Infrastructure and Platform (As A Service)](#infrastructure-and-platform-as-a-service)
  * [References and further reading](#references-and-further-reading)



## Domain

This layer contains information about the domain. This is the heart of the business software. The state of business objects is held here.

Business capabilities of 'Digital Restaurant' include:
- [Courier component](drestaurant-libs/drestaurant-courier) 
  - Managing courier information
  - A courier view of an order (managing the delivery of orders)
- [Restaurant component](drestaurant-libs/drestaurant-restaurant)
  - Managing restaurant menus and other information including location and opening hours
  - A restaurant view of an order  (managing the preparation of orders at a restaurant kitchen)
- [Customer component](drestaurant-libs/drestaurant-customer)
  - Managing information about customers/consumers
  - A customer view of an order (managing the order-customer invariants, e.g order limits)
- [Order component](drestaurant-libs/drestaurant-order)
  - Order taking and fulfillment management
- [Accounting component](drestaurant-libs/drestaurant-accounting)
  - Consumer accounting - managing billing of consumers
  - Restaurant accounting - managing payments to restaurants 
  - Courier accounting - managing payments to couriers

As you try to model a larger domain, it gets progressively harder to build a single unified model for the entire enterprise. In such a model, there would be, for example, a single definition of each business entity such as customer, order etc. 
The problem with this kind of modeling is that:
 - getting different parts of an organization to agree on a single unified model is a monumental task. 
 - from the perspective of a given part of the organization, the model is overly complex for their needs. 
 - the domain model can be confusing since different parts of the organization might use either the same term for different concepts or different terms for the same concept. 

[Domain-driven design (DDD)](https://en.wikipedia.org/wiki/Domain-driven_design) avoids these problems by defining a separate domain model for each subdomain/component.

Subdomains are identified using the same approach as identifying business capabilities: analyze the business and identify the different areas of expertise. 
The end result is very likely to be subdomains that are similar to the business capabilities.
Each sub-domain model belongs to exactly one bounded context.

![](digital-restaurant.png)

### Core subdomains

Some sub-domains are more important to the business then others. This are the subdomains that you want your most experienced people working on. Those are **core subdomains**:

- [Courier subdomain](drestaurant-libs/drestaurant-courier) 
- [Restaurant subdomain](drestaurant-libs/drestaurant-restaurant)
- [Customer subdomain](drestaurant-libs/drestaurant-customer)
- [Order subdomain](drestaurant-libs/drestaurant-order)

The [Order](drestaurant-libs/drestaurant-order/src/main/kotlin/com/drestaurant/order/domain/Order.kt) ([RestaurantOrder](drestaurant-libs/drestaurant-restaurant/src/main/kotlin/com/drestaurant/restaurant/domain/RestaurantOrder.kt), [CustomerOrder](drestaurant-libs/drestaurant-customer/src/main/kotlin/com/drestaurant/customer/domain/CustomerOrder.kt), [CourierOrder](drestaurant-libs/drestaurant-courier/src/main/kotlin/com/drestaurant/courier/domain/CourierOrder.kt)) [aggregate](https://docs.axonframework.org/part-ii-domain-logic/command-model#aggregate) class in each subdomain model represent different term of the same 'Order' business concept.

 - The Restaurant component has a simpler view of an order aggregate (RestaurantOrder). Its version of an Order simply consist of a status and a list of line item, which tell the restaurant what to prepare. We use event-driven mechanism called [sagas](https://docs.axoniq.io/reference-guide/1.2-domain-logic/sagas) to [manage invariants between Restaurant aggregate and RestaurantOrder aggregate](drestaurant-libs/drestaurant-restaurant/src/main/kotlin/com/drestaurant/restaurant/domain/RestaurantOrderSaga.kt) (e.g. Restaurant order should have only menu items that are on the Restaurant menu). Alternatively, you could choose to spawn RestaurantOder aggregate from Restaurant aggregate directly, and not use saga. Benefits are that you check invariants internally, and you can do this with less internal events. Consequences are that you will introduce more coupling between this aggregates, which is fine as long as they belong to the same bounded context.

 - The Courier component has a different view of an order aggregate (CourierOrder). Its version of an Order simply consist of a status and a address, which tell the courier how and where to deliver the order. We use saga to [manage invariants between Courier aggregate and CourierOrder aggregate](drestaurant-libs/drestaurant-courier/src/main/kotlin/com/drestaurant/courier/domain/CourierOrderSaga.kt) (e.g. Courier can deliver a limited number of orders) Alternatively, you could choose to spawn CourierOder aggregate from Courier aggregate directly, and not use saga. Benefits are that you check invariants internally, and you can do this with less internal events. Consequences are that you will introduce more coupling between this aggregates, which is fine as long as they belong to the same bounded context.

 - The Customer component has a different view of an order aggregate (CustomerOrder). Its version of an Order simply consist of a status and a address, which tell the courier how and where to deliver the order. We use saga to [manage invariants between Customer aggregate and CustomerOrder aggregate](drestaurant-libs/drestaurant-customer/src/main/kotlin/com/drestaurant/customer/domain/CustomerOrderSaga.kt) (e.g. Customer has an order limit) Alternatively, you could choose to spawn CustomerOder aggregate from Customer aggregate directly, and not use saga. Benefits are that you check invariants internally, and you can do this with less internal events. Consequences are that you will introduce more coupling between this aggregates, which is fine as long as they belong to the same bounded context.

 - We must maintain consistency between these different 'order' aggregates in different components/domains. For example, once the Order component has initiated order creation it must trigger the creation of CustomerOrder in the Customer component and RestaurantOrder in the Restaurant component. We [maintain consistency between components/bounded-context using sagas](drestaurant-libs/drestaurant-order/src/main/kotlin/com/drestaurant/order/domain/OrderSaga.kt).

![](digital-restaurant-state-machine.png)

#### Event sourcing

We use [event sourcing](http://microservices.io/patterns/data/event-sourcing.html) to persist our [event sourced aggregates](https://docs.axonframework.org/part-ii-domain-logic/command-model#event-sourced-aggregates) as a sequence of events. Each event represents a state change of the aggregate. An application rebuild the current state of an aggregate by replaying the events.

Event sourcing has several important benefits:
 - It preserves the history of aggregates (100%), which is valuable for auditing and regulatory purposes
 - It reliably publishes domain events, which is particularly useful in a microservice architecture.
 - You can use any database technology to store the state.
 
Event sourcing also has drawbacks:
 - There is a learning curve because its a different way to write your business logic. 
 - Events will change shape over time.
 - Querying the event store is often difficult, which forces you to use the Command Query Responsibility Segragation (CQRS) pattern.

Consider using event sourcing within 'core subdomain' only!

#### Snapshoting

By use of evensourcing pattern the application rebuild the current state of an aggregate by replaying the events.
This can be bad for performances if you have a long living aggregate that is replayed by big amount of events.  

 - A Snapshot is a denormalization of the current state of an aggregate at a given point in time
 - It represents the state when all events to that point in time have been replayed
 - They are used as a heuristic to prevent the need to load all events for the entire history of an aggregate
 
Each aggregate defines a snapshot trigger:
 
 - [`@Aggregate(snapshotTriggerDefinition = "courierSnapshotTriggerDefinition")`](drestaurant-libs/drestaurant-courier/src/main/kotlin/com/drestaurant/courier/domain/SpringCourierConfiguration.kt)
 - Feel free to configure a treshold (number of events) that should trigger the snapshot creation. This treshold is externalized as a property `axon.snapshot.trigger.treshold.courier`


### Generic subdomains
Other subdomains facilitate the business, but are not core to the business. In general, these types of pieces can be purchased from a vendor or outsourced. Those are **generic subdomains**:

- [Accounting subdomain](drestaurant-libs/drestaurant-accounting)

Event sourcing is probably not needed within your 'generic subdomain'.

As Eric puts it into numbers, the 'core domain' should deliver about 20% of the total value of the entire system, be about 5% of the code base, and take about 80% of the effort.

### Organisation vs encapsulation 

When you make all types in your application public, the packages are simply an organisation mechanism (a grouping, like folders) rather than being used for encapsulation. Since public types can be used from anywhere in a codebase, you can effectively ignore the packages.

The way Java types are placed into packages (components) can actually make a huge difference to how accessible (or inaccessible) those types can be when Java's access modifiers are applied appropriately. Bundling the types into a smaller number of packages allows for something a little more radical. Since there are fewer inter-package dependencies, you can start to restrict the access modifiers.
Kotlin language doesn't have 'package' modifier as Java has. It has 'internal' modifier which restricts accessiblity of the class to the whole module (compile unit, jar file...) which can hold many packages.

The goal (maybe it is a rule) is to bundle all of the functionality related to a single component into a single Java/Kotlin package, and restrict accessiblity of the classes to `package`, were possible.
For example, our [Customer component](drestaurant-libs/drestaurant-customer) classes are placed in one `com.drestaurant.customer.domain` package, with all classes marked as 'internal'.
Public classes are placed in `com.drestaurant.customer.domain.api` and they are forming an API for this component. This API consist of [commands and events](drestaurant-libs/drestaurant-core-api/src/main/kotlin/com/drestaurant/customer/domain/api).
As we have one maven module holding one package/component we are using Kotlin `internal` modifier as an encapsulation mechanism on the package level as well. This rule is handled by the compiler, which is good, and we can achieve loose coupling and high cohesion effectively.

If you prefer to organize more packages/components into one maven module (maybe use single maven module for all components, and not multi maven module by component as we have now) you should use different encapsulation mechanism because Kotlin is lacking of `package` modifier.

We use an [ArchUnit test](drestaurant-libs/drestaurant-customer/src/test/kotlin/com/drestaurant/CustomerModuleTest.kt) to *force* 'package' scope of the Kotlin classes. This rule is handled on the Unit test level, which is not perfect, but still valuable.

### Context Mapping

Bounded contexts (and teams that produce them) can be in different relationships:

 - partnership (two contexts/teams succeed or fail together)
 - customer-supplier (two teams in upstream/downstream relationship - upstream can succeed interdependently of downstream team)
 - conformist (two teams in upstream/downstream relationship - upstream has no motivation to provide to downstream, and downstream team does not put effort in translation)
 - shared kernel (sharing a part of the model - must be kept small)
 - separate ways (cut them loose)
 - anticorruption layer


You may be wondering how Domain Events can be consumed by another Bounded Context and not force that consuming Bounded Context into a Conformist relationship. 

Consumers should not use the [event types (e.g., classes)](drestaurant-libs/drestaurant-core-api/src/main/kotlin/com/drestaurant) of an event publisher. Rather, they should depend only on the schema of the events, that is, their *Published Language*. This generally means that if the events are published as JSON, or perhaps a more economical object format, the consumer should consume the events by parsing them to obtain their data attributes. This rise complexity (consider [consumer driven contracts](https://www.martinfowler.com/articles/consumerDrivenContracts.html) testing), but enables loose coupling.

Our demo application demonstrate `conformist` pattern, as we are using [strongly typed events](drestaurant-libs/drestaurant-core-api/src/main/kotlin/com/drestaurant).

As our application evolve from monolithic to microservices we should consider diverging from `conformist` and converging to `customer-supplier` bounded context relationship depending only on the schema of the events (with consumer driven contracts included).

## Applications

We have created more 'web' applications (standalone Spring Boot applications) to demonstrate the use of different architectural styles, API designs (adapters) and deployment strategies by utilizing components from the domain layer in different way.

This is a thin layer of [adapters](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together) that surrounds our domain layer, and exposes it to the outside world. It does not contain business logic. It does not hold the state of the business objects.

Our Driving Adapters are mostly Controllers (REST and/or WebSockets) who are **injected in their constructor with the concrete implementation of the interface (port) from the Domain layer**.
These interfaces (ports) and their implementations are provided by Axon platform:
 - `command bus` (`command gataway` as an convenient facade)
 - `query bus` (`query gataway` as an convenient facade)

Adapters are adapting the HTTP and/or WebSocket interfaces to the domain interfaces (ports) by converting requests to messages/domain API (commands, queries) and publishing them on the bus.


Our Driven Adapters **are implementations of domain interfaces (ports)** that are responsible for persisting (e.g event sourced aggregates), publishing and handling domain events.
Event handlers are creating read only projections that are persisted in JPA repositories, forming the query (read) side.
These interfaces (ports) and their implementations are provided by Axon platform
 - `evensourcing repository`
 - `event bus`
 - ...

**Monolithic**

 - [Monolith 1](drestaurant-apps/drestaurant-monolith) 
    - **HTTP and WebSockets API** by segregating Command and Query
    - we don't synchronize on the backend
    - we provide WebSockets for the frontend to handle async nature of the backend
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)
 - [Monolith 2](drestaurant-apps/drestaurant-monolith-rest)
    - **REST API** by not segregating Command and Query
    - we synchronize on the backend side
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)
 - [Monolith 3](drestaurant-apps/drestaurant-monolith-websockets)
    - **WebSockets API**
    - we are async all the way
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)

**Microservices (`decomposed` monoliths)**

 - [Microservices 1](drestaurant-apps/drestaurant-microservices)
    - **HTTP and WebSockets API** by segregating Command and Query
    - [Monolith 1](drestaurant-apps/drestaurant-monolith) as a monolithic version
    - we don't synchronize on the backend
    - we provide WebSockets for the frontend to handle async nature of the backend
    - we use Apache Kafka to distribute events between services
    - we use Spring Cloud discovery and registry service (Eureka) to distribute commands between services (bounded contexts)
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)

 - [Microservices 2.1](drestaurant-apps/drestaurant-microservices-rest)
    - **REST API** by not segregating Command and Query
    - [Monolith 2](drestaurant-apps/drestaurant-monolith-rest) as a monolithic version
    - we synchronize on the backend side
    - we use RabbitMQ to distribute events between services (bounded contexts)
    - we use Spring Cloud discovery and registry service (Eureka) to distribute commands between services (bounded contexts)
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)
    
 - [Microservices 2.2](drestaurant-apps/drestaurant-microservices-rest-2)
     - **REST API** by not segregating Command and Query
     - [Monolith 2](drestaurant-apps/drestaurant-monolith-rest) as a monolithic version
     - we synchronize on the backend side
     - we are async all the way
     - we use [AxonServer](https://axoniq.io/product-overview/axon-server) as event store, and to distribute messages (commands, events and queries)
     - we use H2 SQL database to store materialized views (query side)
     - additionally, we distribute `queries` with AxonServer, so we can extract `query` side in a separate service (ver `Microservices 2.1` does not support this)
     
 - [Microservices 3](drestaurant-apps/drestaurant-microservices-websockets)
    - Monolith 3 decomposed
    - **WebSockets API**
    - [Monolith 3](drestaurant-apps/drestaurant-monolith-websockets) as a monolithic version
    - we are async all the way
    - we use [AxonServer](https://axoniq.io/product-overview/axon-server) as event store, and to distribute messages (commands, events and queries)
    - we use H2 SQL database to store materialized views (query side)

## Monorepo

We have one deployment pipeline for all applications and libraries within this repository. In addition, all projects in the repository share the same dependencies. Hence, there are no version conflicts because everyone has to use the same/the latest (SNAPSHOTS) version. And you don't need to deal with a private NPM (JavaScript) or Maven (Java) registry when you just want to use your own libraries.
This setup and project structure is usually addressed as a [monorepo](https://medium.com/@maoberlehner/monorepos-in-the-wild-33c6eb246cb9).

## Technology

### Language
- [Kotlin][kotlin]

### Frameworks and Platforms
- [Spring (SpringBoot, SpringCloud, SpringData, SpringDataRest)][spring]
- [Axon (AxonFramework, AxonServer)][axonframework]

### Continuous Integration and Delivery 
- Travis

### Infrastructure and Platform (As A Service)
- [H2 - java SQL databse][h2]
- [Apache Kafka][kafka]
- [RabbitMQ][rabbitMQ]
- [AxonServer](https://axoniq.io/product-overview/axon-server)

## References and further reading

  - https://www.manning.com/books/microservices-patterns
  - https://docs.axoniq.io/reference-guide/
  - http://www.codingthearchitecture.com/2016/04/25/layers_hexagons_features_and_components.html
  - https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together
 

[mvn]: https://maven.apache.org/
[kotlin]: https://kotlinlang.org/
[spring]: https://spring.io/
[axonframework]: https://axoniq.io/
[mysql]: https://www.mysql.com/
[h2]: http://h2database.com/html/main.html
[rabbitMQ]: https://www.rabbitmq.com/
[kafka]: https://kafka.apache.org/
[pivotalCF]: https://run.pivotal.io/

---
Created by [Ivan Dugalic][idugalic]

[idugalic]: http://idugalic.pro
