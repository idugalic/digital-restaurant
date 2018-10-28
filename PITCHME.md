### Domain Driven Design 

 @color[gray](by example)

[https://github.com/idugalic/digital-restaurant](https://github.com/idugalic/digital-restaurant)

---
@transition[none]

### Sample application

@color[gray](d-restaurant)

  - Customers use the website applications to place food orders at local restaurants
  - Application coordinates a network of couriers who deliver the orders to customers
  
+++
@transition[none]

@snap[north-west]
@color[gray](Sample application)
@snapend

#### Technology

@ul

- It is built using Event Sourcing and CQRS patterns
- It is written in [Kotlin](https://kotlinlang.org/), and uses [Spring Boot](https://spring.io/projects/spring-boot)
- It is built using [Axonframework](https://axoniq.io/), which is focused on making applications based on the DDD principles
- It is driven using [Maven](https://maven.apache.org/)

@ulend

+++
@transition[none]

@snap[north-west]
@color[gray](Sample application)
@snapend

#### Clone & Build

```bash
$ git clone https://github.com/idugalic/digital-restaurant
$ mvn clean install
```

+++
@transition[none]

@snap[north-west]
@color[gray](Sample application)
@snapend

#### Run monolith 1 (HTTP & Websockets)

```bash
$ cd drestaurant-apps/drestaurant-monolith
$ mvn spring-boot:run
```

+++
@transition[none]

@snap[north-west]
@color[gray](Sample application)
@snapend

#### Run monolith 2 (REST)

```bash
$ cd drestaurant-apps/drestaurant-monolith-rest
$ mvn spring-boot:run
```

+++
@transition[none]

@snap[north-west]
@color[gray](Sample application)
@snapend

#### Run monolith 3 (Websockets)

```bash
$ cd drestaurant-apps/drestaurant-monolith-websockets
$ mvn spring-boot:run
```
+++
@transition[none]

@snap[north-west]
@color[gray](Sample application)
@snapend

#### Run microservices 1 (Monolith 1 decomposed)

```bash
https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices
```

+++
@transition[none]

@snap[north-west]
@color[gray](Sample application)
@snapend

#### Run microservices 2 (Monolith 2 decomposed)

```bash
https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-rest
```

+++
@transition[none]

@snap[north-west]
@color[gray](Sample application)
@snapend

#### Run microservices 3 (Monolith 3 decomposed)

```bash
https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-websockets
```
---
@transition[none]

### Domain layer

@ul

- [This layer](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs) contains information about the domain
- This is the heart of the business software
- The state of business objects is held here

@ulend

+++
@transition[none]

@snap[north-west]
@color[gray](Domain layer)
@snapend

#### Subdomains

@ul

- getting different parts of an organization to agree on a single unified model is a monumental task. 
- from the perspective of a given part of the organization, the model is overly complex for their needs. 
- the domain model can be confusing since different parts of the organization might use either the same term for different concepts or different terms for the same concept. 

@ulend

+++
@transition[none]

@snap[north-west]
@color[gray](Domain layer)
@snapend

#### Subdomains - Analyze

  - Analyze the business and identify the different areas of expertise. The end result is very likely to be subdomains
  - http://eventstorming.com/

+++
@transition[none]

@snap[north-west]
@color[gray](Domain layer)
@snapend

#### Subdomains - Context

![](digital-restaurant.png)
  

+++
@transition[none]

@snap[north-west]
@color[gray](Domain layer)
@snapend

#### Core subdomains

Core subdomains are more important to the business

 - [Courier subdomain - drestaurant-libs/drestaurant-courier/](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-courier) 
 - [Restaurant subdomain - drestaurant-libs/drestaurant-restaurant/](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-restaurant)
 - [Customer subdomain - drestaurant-libs/drestaurant-customer/](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-customer)
 - [Order subdomain - drestaurant-libs/drestaurant-order/](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-order)


+++
@transition[none]

@snap[north-west]
@color[gray](Domain layer)
@snapend

#### Generic subdomains

Generic subdomains facilitate the business, but are not core to the business. In general, these types of pieces can be purchased from a vendor or outsourced.

 - [Accounting subdomain - drestaurant-libs/drestaurant-accounting/](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-accounting)

+++
@transition[none]

@snap[north-west]
@color[gray](Domain layer)
@snapend

#### Eventsourcing

@ul

- We use [event sourcing](http://microservices.io/patterns/data/event-sourcing.html) to persist our [event sourced aggregates](https://docs.axonframework.org/part-ii-domain-logic/command-model#event-sourced-aggregates) as a sequence of events
- Each event represents a state change of the aggregate
- An application rebuild the current state of an aggregate by replaying the events

@ulend

+++
@transition[none]

@snap[north-west]
@color[gray](Domain layer)
@snapend

#### Eventsourcing benefits

@ul

- It preserves the history of aggregates (100%), which is valuable for auditing and regulatory purposes
- It reliably publishes domain events, which is particularly useful in a microservice architecture
- You can use any database technology to store the state

@ulend

+++
@transition[none]
 
@snap[north-west]
@color[gray](Domain layer)
@snapend
 
#### Eventsourcing drawbacks
 
@ul

- There is a learning curve because its a different way to write your business logic
- Events will change shape over time
- Forces you to use the CQRS pattern
- Consider using event sourcing within 'core subdomain' only

@ulend

+++
@transition[none]

@snap[north-west]
@color[gray](Domain layer)
@snapend
 
#### Eventsourcing & snapshotting
 
@ul

- A Snapshot represents the state when all events to that point in time have been replayed
- They are used as a heuristic to prevent the need to load all events for the entire history of an aggregate

@ulend

```java
@Aggregate(snapshotTriggerDefinition = "courierSnapshotTriggerDefinition")
```

+++
@transition[none]

@snap[north-west]
@color[gray](Domain layer)
@snapend

#### Saga

@ul

- [Sagas](http://www.amundsen.com/downloads/sagas.pdf) are used to manage business transactions
- The saga transaction model is `ACD`. It lack of `Isolation`
- They respond on Events and may dispatch Commands, invoke external applications, ...
- Orchestration vs. Choreography
- Saga orchestrators as state machines

@ulend

+++
@transition[none]

@snap[north-west]
@color[gray](Domain layer)
@snapend

#### Saga example

```java
@Saga
@ProcessingGroup("restaurantordersaga")
class RestaurantOrderSaga {
    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway
    private lateinit var orderId: String

    @StartSaga
    @SagaEventHandler(associationProperty = "aggregateIdentifier")
    internal fun on(event: RestaurantOrderCreationInitiatedInternalEvent) {
        orderId = event.aggregateIdentifier
        associateWith("orderId", orderId)
        commandGateway.send(ValidateOrderByRestaurantInternalCommand(orderId, event.restaurantId, event.orderDetails.lineItems, event.auditEntry), LoggingCallback.INSTANCE)
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    internal fun on(event: OrderValidatedWithSuccessByRestaurantInternalEvent) = commandGateway.send(MarkRestaurantOrderAsCreatedInternalCommand(event.orderId, event.auditEntry), LoggingCallback.INSTANCE)
```

+++
@transition[none]

@snap[north-west]
@color[gray](Domain layer)
@snapend

#### State machine

<img src="digital-restaurant-state-machine.png" width="700">

---
@transition[none]

### Applications layer

@ul

- [This is a thin layer](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps) which coordinates the application activity
- It does not contain business logic
- It does not hold the state of the business objects

@ulend

+++
@transition[none]

@snap[north-west]
@color[gray](Applications layer)
@snapend

 - Monolith 1 - HTTP and WebSockets API
 - Monolith 2 - HTTP/REST API
 - Monolith 3 - WebSockets API
 - Microservices 1 - *Monolith 1 decomposed*
 - Microservices 2 - *Monolith 2 decomposed*
 - Microservices 3 - *Monolith 3 decomposed*

+++
@transition[none]

@snap[north-west]
@color[gray](Applications layer)
@snapend

#### Monolith 1 (HTTP and WebSockets API)

@ul

- [Event handler](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/query/handler) consumes events, and creates query models
- It can be replied (`@AllowReplay(true)`) to re-create (`@ResetHandler`) query model
- It is publishing a WebSocket events to notify on update of a query model
- Query models are exposed via [Spring Data Rest](https://github.com/idugalic/digital-restaurant/blob/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/query/repository/OrderRepository.kt)
- One-to-many relation between a [command resource](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/web/CommandController.kt) and [query resource](https://github.com/idugalic/digital-restaurant/blob/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/query/repository/OrderRepository.kt)
 
@ulend
 
+++
@transition[none]

@snap[north-west]
@color[gray](Applications layer)
@snapend

#### Monolith 2 (HTTP/REST API)

@ul

- [Event handler](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/query/handler) consumes events, and creates query models
- It can be replied (`@AllowReplay(true)`) to re-create (`@ResetHandler`) query model
- We emit 'any change on Query Model' to Axon subscription queries, and we subscribe on them within [CommandController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/web/CommandController.kt)
- One-to-one relation between a [command resource](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/web/CommandController.kt) and [query resource (Spring Data Rest)](https://github.com/idugalic/digital-restaurant/blob/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/query/repository/OrderRepository.kt)

@ulend

+++
@transition[none]

@snap[north-west]
@color[gray](Applications layer)
@snapend

#### Monolith 3 (WebSockets API)
 
@ul

- [Event handler](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-websockets/src/main/kotlin/com/drestaurant/query/handler) consumes domain events, and creates query models
- It can be replied (`@AllowReplay(true)`) to re-create (`@ResetHandler`) query model
- It is publishing a WebSocket messages to topics on every update of a query model
- [WebController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-websockets/src/main/kotlin/com/drestaurant/web/WebController.kt) expose WebSocket message endpoints

@ulend

+++
@transition[none]

@snap[north-west]
@color[gray](Applications layer)
@snapend

#### Microservices 1 (Monolith 1 decomposed)

Each [microservice](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices):

@ul

- has its own bounded context,
- has its own JPA event store (we are not sharing the JPA Event Store)
- we distribute *events* between them via **Apache Kafka**
- we distribute *commands* via **Spring Cloud discovery and registry service (Eureka)**
 
@ulend

+++
@transition[none]

@snap[north-west]
@color[gray](Applications layer)
@snapend

#### Microservices 2 (Monolith 2 decomposed)

Each [microservice](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-rest):

@ul

- has its own bounded context,
- has its own JPA event store (we are not sharing the JPA Event Store)
- we distribute *events* between them via **RabbitMQ**
- we distribute *commands* via **Spring Cloud discovery and registry service (Eureka)**

@ulend

+++
@transition[none]

@snap[north-west]
@color[gray](Applications layer)
@snapend

#### Microservices 3 (Monolith 3 decomposed)

Each [microservice](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-websockets):

@ul

- has its own bounded context,
- has shared event(sourcing) storage (**[AxonServer](https://axoniq.io/product-overview/axon-server)**)
- and we distribute messages (*commands*, *events* and *queries*) between them via **AxonServer**

@ulend
---
@transition[none]

### Thank you
<hr />
@fa[terminal] Ivan Dugalic, software engineer

@fa[globe] http://idugalic.pro

@fa[envelope] idugalic@gmail.com

@fa[twitter](https://twitter.com/idugalic)

@fa[github](https://github.com/idugalic)





