### Domain Driven Design 

<span style="color:gray">by example</span>

[https://github.com/idugalic/digital-restaurant](https://github.com/idugalic/digital-restaurant)

---

### Sample application

<span style="color:gray">d-restaurant</span>

  - Customers use the website applications to place food orders at local restaurants
  - Application coordinates a network of couriers who deliver the orders to customers
  
+++

### Sample application

<span style="color:gray">Technology</span>

  - It is built using Event Sourcing and CQRS patterns
  - It is written in Kotlin, and uses Spring Boot
  - It is built using Axonframework, which is focused on making applications based on the DDD principles
  - It is driven using Maven.
  
+++

### Sample application

<span style="color:gray">Clone & Build</span>

```bash
$ git clone https://github.com/idugalic/digital-restaurant
$ mvn clean install
```

+++

### Sample application

<span style="color:gray">Run monolith 1 (HTTP & Websockets)</span>

```bash
$ cd drestaurant-apps/drestaurant-monolith
$ mvn spring-boot:run
```

+++

### Sample application

<span style="color:gray">Run monolith 2 (REST)</span>

```bash
$ cd drestaurant-apps/drestaurant-monolith-rest
$ mvn spring-boot:run
```

+++

### Sample application

<span style="color:gray">Run monolith 3 (Websockets)</span>

```bash
$ cd drestaurant-apps/drestaurant-monolith-websockets
$ mvn spring-boot:run
```
+++

### Sample application

<span style="color:gray">Run microservices 1 (HTTP & Websockets & Apache Kafka)</span>

```bash
https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices
```

+++

### Sample application

<span style="color:gray">Run microservices 2 (REST & RabbitMQ)</span>

```bash
https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-rest
```

+++

### Sample application

<span style="color:gray">Run microservices 3 (Websockets, AxonHub & AxonDB)</span>

```bash
https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-websockets
```
---

### Domain layer

  - This layer contains information about the domain
  - This is the heart of the business software
  - The state of business objects is held here
  
+++

### Domain layer

<span style="color:gray">Subdomains - The problem</span>

  - getting different parts of an organization to agree on a single unified model is a monumental task. 
  - from the perspective of a given part of the organization, the model is overly complex for their needs. 
  - the domain model can be confusing since different parts of the organization might use either the same term for different concepts or different terms for the same concept. 

+++

### Domain layer

<span style="color:gray">Subdomains - Analyze</span>

  - Analyze the business and identify the different areas of expertise. The end result is very likely to be subdomains
  - http://eventstorming.com/

+++

### Domain layer

<span style="color:gray">Core subdomains</span>

Core subdomains are more important to the business

 - [Courier subdomain - drestaurant-libs/drestaurant-courier/](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-courier) 
 - [Restaurant subdomain - drestaurant-libs/drestaurant-restaurant/](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-restaurant)
 - [Customer subdomain - drestaurant-libs/drestaurant-customer/](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-customer)
 - [Order subdomain - drestaurant-libs/drestaurant-order/](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-order)

+++

### Domain layer

<span style="color:gray">Generic subdomains</span>

Generic subdomains facilitate the business, but are not core to the business. In general, these types of pieces can be purchased from a vendor or outsourced.

 - [Accounting subdomain - drestaurant-libs/drestaurant-accounting/](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-accounting)

+++

### Domain layer

<span style="color:gray">Eventsourcing</span>

 - We use [event sourcing](http://microservices.io/patterns/data/event-sourcing.html) to persist our [event sourced aggregates](https://docs.axonframework.org/part-ii-domain-logic/command-model#event-sourced-aggregates) as a sequence of events
 - Each event represents a state change of the aggregate
 - An application rebuild the current state of an aggregate by replaying the events

+++

### Domain layer

<span style="color:gray">Eventsourcing benefits</span>

 - It preserves the history of aggregates (100%), which is valuable for auditing and regulatory purposes
 - It reliably publishes domain events, which is particularly useful in a microservice architecture
 - You can use any database technology to store the state

+++
 
### Domain layer
 
<span style="color:gray">Eventsourcing drawbacks</span>
 
 - There is a learning curve because its a different way to write your business logic
 - Events will change shape over time
 - Querying the event store is often difficult, which forces you to use the Command Query Responsibility Segragation (CQRS) pattern

Consider using event sourcing within 'core subdomain' only!

+++

### Domain layer
 
<span style="color:gray">Eventsourcing & snapshotting</span>
 
 - A Snapshot is a denormalization of the current state of an aggregate at a given point in time
 - It represents the state when all events to that point in time have been replayed
 - They are used as a heuristic to prevent the need to load all events for the entire history of an aggregate
 - `@Aggregate(snapshotTriggerDefinition = "courierSnapshotTriggerDefinition")`

---

### Applications layer

 - This is a thin layer which coordinates the application activity
 - It does not contain business logic
 - It does not hold the state of the business objects

+++

### Applications layer

 - Monolith 1 - HTTP and WebSockets API
 - Monolith 2 - HTTP/REST API
 - Monolith 3 - WebSockets API
 - Microservices 1 - HTTP, WebSockets API and Kafka
 - Microservices 2 - REST and RabbitMQ
 - Microservices 3 - Websockets, AxonHub & AxonDB

+++

### Applications layer

<span style="color:gray">Monolith 1 (HTTP and WebSockets API)</span>

 - [Event handler](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/query/handler) consumes events, and creates query models
 - It can be replied (`@AllowReplay(true)`) to re-create (`@ResetHandler`) query model
 - It is publishing a WebSocket events to notify on update of a query model
 - Query models are exposed via [Spring Data Rest](https://github.com/idugalic/digital-restaurant/blob/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/query/repository/OrderRepository.kt)
 - One-to-many relation between a [command resource](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/web/CommandController.kt) and [query resource](https://github.com/idugalic/digital-restaurant/blob/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/query/repository/OrderRepository.kt)
 
 
+++

### Applications layer

<span style="color:gray">Monolith 2 (HTTP/REST API)</span>

 - [Event handler](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/query/handler) consumes events, and creates query models
 - It can be replied (`@AllowReplay(true)`) to re-create (`@ResetHandler`) query model
 - We emit 'any change on Query Model' to Axon subscription queries, and we subscribe on them within [CommandController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/web/CommandController.kt)
 - One-to-one relation between a [command resource](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/web/CommandController.kt) and [query resource (Spring Data Rest)](https://github.com/idugalic/digital-restaurant/blob/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/query/repository/OrderRepository.kt)


+++

### Applications layer

<span style="color:gray">Monolith 3 (WebSockets API)</span>
 
 - [Event handler](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-websockets/src/main/kotlin/com/drestaurant/query/handler) consumes domain events, and creates query models
 - It can be replied (`@AllowReplay(true)`) to re-create (`@ResetHandler`) query model
 - It is publishing a WebSocket messages to topics on every update of a query model
 - [WebController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-websockets/src/main/kotlin/com/drestaurant/web/WebController.kt) expose WebSocket message endpoints


+++

### Applications layer

<span style="color:gray">Microservices 1 (HTTP, WebSockets API and Apache Kafka)</span>

Each [microservice](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices):

 - has its own bounded context,
 - has its own JPA event store (we are not sharing the JPA Event Store)
 - and we distribute events between them via **Apache Kafka**
 
+++

### Applications layer

<span style="color:gray">Microservices 2 (REST and RabbitMQ)</span>

Each [microservice](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-rest):

 - has its own bounded context,
 - has its own JPA event store (we are not sharing the JPA Event Store)
 - and we distribute events between them via **RabbitMQ**
 
+++

### Applications layer

<span style="color:gray">Microservices 3 (Websockets, AxonHub & AxonDB)</span>

Each [microservice](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-websockets):

 - has its own bounded context,
 - has shared event(sourcing) storage (AxonDB)
 - and we distribute messages between them via AxonHub
 
---
### Thank you

 - Ivan Dugalic
 - [http://idugalic.pro/](http://idugalic.pro/)
 - [https://twitter.com/idugalic](https://twitter.com/idugalic)
 - [https://github.com/idugalic](https://github.com/idugalic)





