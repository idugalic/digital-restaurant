# [projects](http://idugalic.github.io/projects)/digital-restaurant [![Build Status](https://travis-ci.org/idugalic/digital-restaurant.svg?branch=kotlin)](https://travis-ci.org/idugalic/digital-restaurant) [![GitPitch](https://gitpitch.com/assets/badge.svg)](https://gitpitch.com/idugalic/digital-restaurant/master?grs=github&t=white)

'd-restaurant' is an example of an application that is built using Event Sourcing and CQRS. The application is written in [Kotlin][kotlin], and uses [Spring][spring] Boot.
It is built using [Axon](https://axoniq.io/product-overview/axon), which is end-to-end development and infrastructure platform for smoothly evolving event-driven microservices focused on [CQRS](https://microservices.io/patterns/data/cqrs.html) and [Event Sourcing](https://microservices.io/patterns/data/event-sourcing.html) patterns.

**Customers use the website application to place food orders at local restaurants. Application coordinates a network of couriers who deliver the orders.**


## Table of Contents

  * [Domain layer](#domain-layer)
     * [Core subdomains](#core-subdomains)
        * [Event sourcing](#event-sourcing)
        * [Snapshoting](#snapshoting)
     * [Generic subdomains](#generic-subdomains)
     * [Organisation vs encapsulation](#organisation-vs-encapsulation)
  * [Application/s layer](#applications-layer)
     * [Monolith 1 (HTTP and WebSockets API by segregating Command and Query)](#monolith-1-http-and-websockets-api-by-segregating-command-and-query)
        * ['Command' HTTP API](#command-http-api)
           * [Create new Restaurant](#create-new-restaurant)
           * [Create/Register new Customer](#createregister-new-customer)
           * [Create/Hire new Courier](#createhire-new-courier)
           * [Create/Place the Order](#createplace-the-order)
           * [Restaurant marks the Order as prepared](#restaurant-marks-the-order-as-prepared)
           * [Courier takes/claims the Order that is ready for delivery (prepared)](#courier-takesclaims-the-order-that-is-ready-for-delivery-prepared)
           * [Courier marks the Order as delivered](#courier-marks-the-order-as-delivered)
        * ['Query' HTTP API](#query-http-api)
        * [Administration](#administration)
           * [Read all event processors](#read-all-event-processors)
           * [Event processors reset](#event-processors-reset)
           * [Event processor status](#event-processor-status)
        * [WebSocket (STOMP) API](#websocket-stomp-api)
     * [Monolith 2 (REST API by not segregating Command and Query)](#monolith-2-rest-api-by-not-segregating-command-and-query)
        * [Restaurant management](#restaurant-management)
           * [Read all restaurants](#read-all-restaurants)
           * [Create new restaurant](#create-new-restaurant-1)
           * [Mark restaurant order as prepared](#mark-restaurant-order-as-prepared)
        * [Customer management](#customer-management)
           * [Read all customers](#read-all-customers)
           * [Create/Register new Customer](#createregister-new-customer-1)
        * [Courier management](#courier-management)
           * [Read all couriers](#read-all-couriers)
           * [Create/Hire new Courier](#createhire-new-courier-1)
           * [Courier takes/claims the Order that is ready for delivery (prepared)](#courier-takesclaims-the-order-that-is-ready-for-delivery-prepared-1)
           * [Courier marks the order as delivered](#courier-marks-the-order-as-delivered-1)
        * [Order management](#order-management)
           * [Read all orders](#read-all-orders)
           * [Create/Place the Order](#createplace-the-order-1)
        * [Administration](#administration-1)
           * [Read all event processors](#read-all-event-processors-1)
           * [Event processors reset](#event-processors-reset-1)
           * [Event processor status](#event-processor-status-1)
     * [Monolith 3 (STOMP over WebSockets API. We are async all the way)](#monolith-3-stomp-over-websockets-api-we-are-async-all-the-way)
        * [STOMP over WebSockets API](#stomp-over-websockets-api)
           * [Topics:](#topics)
           * [Message endpoints:](#message-endpoints)
     * [Microservices 1 (HTTP, Websockets, Apache Kafka)](#microservices-1-http-websockets-apache-kafka)
        * [Apache Kafka &amp; event messages](#apache-kafka--event-messages)
           * [Order of events (kafka topics &amp; partitions)](#order-of-events-kafka-topics--partitions)
           * [Queue vs publish-subscribe (kafka groups)](#queue-vs-publish-subscribe-kafka-groups)
        * [Spring Cloud connector &amp; command messages](#spring-cloud-connector--command-messages)
        * ['Command' HTTP API](#command-http-api-1)
           * [Create new Restaurant](#create-new-restaurant-2)
           * [Create/Register new Customer](#createregister-new-customer-2)
           * [Create/Hire new Courier](#createhire-new-courier-2)
           * [Create/Place the Order](#createplace-the-order-2)
           * [Restaurant marks the Order as prepared](#restaurant-marks-the-order-as-prepared-1)
           * [Courier takes/claims the Order that is ready for delivery (prepared)](#courier-takesclaims-the-order-that-is-ready-for-delivery-prepared-2)
           * [Courier marks the Order as delivered](#courier-marks-the-order-as-delivered-2)
        * ['Query' HTTP API](#query-http-api-1)
     * [Microservices 2 (REST, RabbitMQ)](#microservices-2-rest-rabbitmq)
        * [RabbitMQ &amp; event messages](#rabbitmq--event-messages)
           * [Publish-subscribe](#publish-subscribe)
        * [Spring Cloud connector &amp; command messages](#spring-cloud-connector--command-messages-1)
        * [Restaurant management](#restaurant-management-1)
           * [Read all restaurants](#read-all-restaurants-1)
           * [Create new restaurant](#create-new-restaurant-3)
           * [Mark restaurant order as prepared](#mark-restaurant-order-as-prepared-1)
        * [Customer management](#customer-management-1)
           * [Read all customers](#read-all-customers-1)
           * [Create/Register new Customer](#createregister-new-customer-3)
        * [Courier management](#courier-management-1)
           * [Read all couriers](#read-all-couriers-1)
           * [Create/Hire new Courier](#createhire-new-courier-3)
           * [Courier takes/claims the Order that is ready for delivery (prepared)](#courier-takesclaims-the-order-that-is-ready-for-delivery-prepared-3)
           * [Courier marks the order as delivered](#courier-marks-the-order-as-delivered-3)
        * [Order management](#order-management-1)
           * [Read all orders](#read-all-orders-1)
           * [Create/Place the Order](#createplace-the-order-3)
     * [Microservices 3 (Websockets, AxonServer)](#microservices-3-websockets-axonserver)
        * [AxonServer &amp; event messages &amp; command messages &amp; query messages](#axonserver--event-messages--command-messages--query-messages)
        * [STOMP over WebSockets API](#stomp-over-websockets-api-1)
           * [Customer (command side)](#customer-command-side)
           * [Courier (command side)](#courier-command-side)
           * [Restaurant (command side)](#restaurant-command-side)
           * [Order (command side)](#order-command-side)
           * [Query side](#query-side)
  * [Development](#development)
     * [Clone](#clone)
     * [Build](#build)
     * [Run monolith 1 (HTTP and WebSockets API by segregating Command and Query)](#run-monolith-1-http-and-websockets-api-by-segregating-command-and-query)
     * [Run monolith 2 (REST API by not segregating Command and Query)](#run-monolith-2-rest-api-by-not-segregating-command-and-query)
     * [Run monolith 3 (STOMP over WebSockets API. We are async all the way)](#run-monolith-3-stomp-over-websockets-api-we-are-async-all-the-way)
     * [Run microservices 1 (HTTP, Websockets, Apache Kafka)](#run-microservices-1-http-websockets-apache-kafka)
     * [Run microservices 2 (REST, RabbitMQ)](#run-microservices-2-rest-rabbitmq)
     * [Run microservices 3 (Websockets, AxonDB and AxonHub)](#run-microservices-3-websockets-axondb-and-axonhub)
  * [Continuous delivery](#continuous-delivery)
  * [Technology](#technology)
     * [Language](#language)
     * [Frameworks and Platforms](#frameworks-and-platforms)
     * [Continuous Integration and Delivery](#continuous-integration-and-delivery)
     * [Infrastructure and Platform (As A Service)](#infrastructure-and-platform-as-a-service)
  * [References and further reading](#references-and-further-reading)



## Domain layer

This layer contains information about the domain. This is the heart of the business software. The state of business objects is held here. Persistence of the business objects and possibly their state is delegated to the infrastructure layer

Business capabilities of 'Digital Restaurant' include:
- [Courier component](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-courier) 
  - Managing courier information
  - A courier view of an order (managing the delivery of orders)
- [Restaurant component](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-restaurant)
  - Managing restaurant menus and other information including location and opening hours
  - A restaurant view of an order  (managing the preparation of orders at a restaurant kitchen)
- [Customer component](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-customer)
  - Managing information about customers/consumers
  - A customer view of an order (managing the order-customer invariants, e.g order limits)
- [Order component](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-order)
  - Order taking and fulfillment management
- [Accounting component](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-accounting)
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

- [Courier subdomain](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-courier) 
- [Restaurant subdomain](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-restaurant)
- [Customer subdomain](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-customer)
- [Order subdomain](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-order)

The [Order](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-order/src/main/kotlin/com/drestaurant/order/domain/Order.kt) ([RestaurantOrder](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-restaurant/src/main/kotlin/com/drestaurant/restaurant/domain/RestaurantOrder.kt), [CustomerOrder](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-customer/src/main/kotlin/com/drestaurant/customer/domain/CustomerOrder.kt), [CourierOrder](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-courier/src/main/kotlin/com/drestaurant/courier/domain/CourierOrder.kt)) [aggregate](https://docs.axonframework.org/part-ii-domain-logic/command-model#aggregate) class in each subdomain model represent different term of the same 'Order' business concept.

 - The Restaurant component has a simpler view of an order aggregate (RestaurantOrder). Its version of an Order simply consist of a status and a list of line item, which tell the restaurant what to prepare. We use event-driven mechanism called [sagas](https://docs.axoniq.io/reference-guide/1.2-domain-logic/sagas) to [manage invariants between Restaurant aggregate and RestaurantOrder aggregate](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-restaurant/src/main/kotlin/com/drestaurant/restaurant/domain/RestaurantOrderSaga.kt) (e.g. Restaurant order should have only menu items that are on the Restaurant menu). Alternatively, you could choose to spawn RestaurantOder aggregate from Restaurant aggregate directly, and not use saga. Benefits are that you check invariants internally, and you can do this with less internal events. Consequences are that you will introduce more coupling between this aggregates, which is fine as long as they belong to the same bounded context.

 - The Courier component has a different view of an order aggregate (CourierOrder). Its version of an Order simply consist of a status and a address, which tell the courier how and where to deliver the order. We use saga to [manage invariants between Courier aggregate and CourierOrder aggregate](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-courier/src/main/kotlin/com/drestaurant/courier/domain/CourierOrderSaga.kt) (e.g. Courier can deliver a limited number of orders) Alternatively, you could choose to spawn CourierOder aggregate from Courier aggregate directly, and not use saga. Benefits are that you check invariants internally, and you can do this with less internal events. Consequences are that you will introduce more coupling between this aggregates, which is fine as long as they belong to the same bounded context.

 - The Customer component has a different view of an order aggregate (CustomerOrder). Its version of an Order simply consist of a status and a address, which tell the courier how and where to deliver the order. We use saga to [manage invariants between Customer aggregate and CustomerOrder aggregate](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-customer/src/main/kotlin/com/drestaurant/customer/domain/CustomerOrderSaga.kt) (e.g. Customer has an order limit) Alternatively, you could choose to spawn CustomerOder aggregate from Customer aggregate directly, and not use saga. Benefits are that you check invariants internally, and you can do this with less internal events. Consequences are that you will introduce more coupling between this aggregates, which is fine as long as they belong to the same bounded context.

 - We must maintain consistency between these different 'order' aggregates in different components/domains. For example, once the Order component has initiated order creation it must trigger the creation of CustomerOrder in the Customer component and RestaurantOrder in the Restaurant component. We [maintain consistency between components/bounded-context using sagas](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-order/src/main/kotlin/com/drestaurant/order/domain/OrderSaga.kt).

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
 
 - [`@Aggregate(snapshotTriggerDefinition = "courierSnapshotTriggerDefinition")`](https://github.com/idugalic/digital-restaurant/blob/master/drestaurant-libs/drestaurant-courier/src/main/kotlin/com/drestaurant/courier/domain/SpringCourierConfiguration.kt)
 - Feel free to configure a treshold (number of events) that should trigger the snapshot creation. This treshold is externalized as a property `axon.snapshot.trigger.treshold.courier`


### Generic subdomains
Other subdomains facilitate the business, but are not core to the business. In general, these types of pieces can be purchased from a vendor or outsourced. Those are **generic subdomains**:

- [Accounting subdomain](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-accounting)

Event sourcing is probably not needed within your 'generic subdomain'.

As Eric puts it into numbers, the 'core domain' should deliver about 20% of the total value of the entire system, be about 5% of the code base, and take about 80% of the effort.

### Organisation vs encapsulation 

When you make all types in your application public, the packages are simply an organisation mechanism (a grouping, like folders) rather than being used for encapsulation. Since public types can be used from anywhere in a codebase, you can effectively ignore the packages.

The way Java types are placed into packages (components) can actually make a huge difference to how accessible (or inaccessible) those types can be when Java's access modifiers are applied appropriately. Bundling the types into a smaller number of packages allows for something a little more radical. Since there are fewer inter-package dependencies, you can start to restrict the access modifiers.
Kotlin language doesn't have 'package' modifier as Java has. It has 'internal' modifier which restricts accessiblity of the class to the whole module (compile unit, jar file...). This makes a difference, and you have more freedom to structure your source code, and provide good public API of the component.

For example, our [Customer component](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-customer) classes are placed in one `com.drestaurant.customer.domain` package, with all classes marked as 'internal'.
Public classes are placed in `com.drestaurant.customer.domain.api` and they are forming an API for this component. This API consist of [commands and events](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-common/src/main/kotlin/com/drestaurant/customer/domain/api).

### Context Mapping

Bounded contexts (and teams that produce them) can be in different relationships:

 - partnership (two contexts/teams succeed or fail together)
 - customer-supplier (two teams in upstream/downstream relationship - upstream can succeed interdependently of downstream team)
 - conformist (two teams in upstream/downstream relationship - upstream has no motivation to provide to downstream, and downstream team does not put effort in translation)
 - shared kernel (sharing a part of the model - must be kept small)
 - separate ways (cut them loose)
 - anticorruption layer


You may be wondering how Domain Events can be consumed by another Bounded Context and not force that consuming Bounded Context into a Conformist relationship. 

Consumers should not use the [event types (e.g., classes)](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-common/src/main/kotlin/com/drestaurant/customer/domain/api) of an event publisher. Rather, they should depend only on the schema of the events, that is, their *Published Language*. This generally means that if the events are published as JSON, or perhaps a more economical object format, the consumer should consume the events by parsing them to obtain their data attributes. This rise complexity (consider [consumer driven contracts](https://www.martinfowler.com/articles/consumerDrivenContracts.html) testing), but enables loose coupling. 

Our demo application demonstrate `conformist` pattern, as we are using [strongly typed events](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-common/src/main/kotlin/com/drestaurant).

As our application evolve from monolithic to microservices we should consider diverging from `conformist` and converging to `customer-supplier` bounded context relationship depending only on the schema of the events (with consumer driven contracts included).

## Application/s layer

This is a thin layer which coordinates the application activity. It does not contain business logic. It does not hold the state of the business objects

We have created more 'web' applications (standalone Spring Boot applications) to demonstrate the use of different architectural styles, API designs and deployment strategies by utilizing components from the domain layer in different way:

**Monolithic**

 - [Monolith 1](#monolith-1-http-and-websockets-api-by-segregating-command-and-query) 
    - **HTTP and WebSockets API** by segregating Command and Query
    - we don't synchronize on the backend
    - we provide WebSockets for the frontend to handle async nature of the backend
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)
 - [Monolith 2](#monolith-2-rest-api-by-not-segregating-command-and-query)
    - **REST API** by not segregating Command and Query
    - we synchronize on the backend side
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)
 - [Monolith 3](#monolith-3-stomp-over-websockets-api-we-are-async-all-the-way)
    - **WebSockets API**
    - we are async all the way
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)

**Microservices (decomposed monoliths)**

 - [Microservices 1](#microservices-1-http-websockets-apache-kafka)
    - Monolith 1 decomposed
    - **HTTP and WebSockets API** by segregating Command and Query
    - [Monolith 1](#monolith-1-http-and-websockets-api-by-segregating-command-and-query) as a monolithic version
    - we don't synchronize on the backend
    - we provide WebSockets for the frontend to handle async nature of the backend
    - we use Apache Kafka to distribute events between services
    - we use Spring Cloud discovery and registry service (Eureka) to distribute commands between services (bounded contexts)
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)

 - [Microservices 2](#microservices-2-rest-rabbitmq)
    - Monolith 2 decomposed
    - **REST API** by not segregating Command and Query
    - [Monolith 2](#monolith-2-rest-api-by-not-segregating-command-and-query) as a monolithic version
    - we synchronize on the backend side
    - we use RabbitMQ to distribute events between services (bounded contexts)
    - we use Spring Cloud discovery and registry service (Eureka) to distribute commands between services (bounded contexts)
    - we use H2 SQL database as event store
    - we use H2 SQL database to store materialized views (query side)
    - [version 2](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-rest-2) is available, utilizing [AxonServer](https://axoniq.io/product-overview/axon-server) -> we distribute queries as well -> independent `query` microservice -> scales better
    
 - [Microservices 3](#microservices-3-websockets-axondb-and-axonhub)
    - Monolith 3 decomposed
    - **WebSockets API**
    - [Monolith 3](#monolith-3-stomp-over-websockets-api-we-are-async-all-the-way) as a monolithic version
    - we are async all the way
    - we use [AxonServer](https://axoniq.io/product-overview/axon-server) as event store, and to distibute messages (commands, events and queries)
    - we use H2 SQL database to store materialized views (query side)
      
### Monolith 1 (HTTP and WebSockets API by segregating Command and Query)

Source code: [https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith)

A recurring question with CQRS and EventSourcing is how to put a synchronous HTTP front-end on top of an asynchronous CQRS back-end.

In general there are two approaches:

 - **segregating Command and Query** - resources representing Commands (request for changes) and resources representing Query Models (the state of the domain) are decoupled
 - **not segregating Command and Query** - one-to-one relation between a Command Model resource and a Query Model resource
 
 This application is using the first approach ('segregating Command and Query') by exposing capabilities of our 'domain' via the HTTP/REST API components that are responsible for
 - dispatching commands - [CommandController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/web/CommandController.kt)
 - querying the 'query model' (materialized views) - [Spring REST repositories](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/query/repository)

**There is no one-to-one relation between a Command resource and a Query Model resource. This makes easier to implement multiple representations of the same underlying domain entity as separate resources.**


[Event handler](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/query/handler) is a central component.
It consumes events, and creates 'query models' (materialized views) of aggregates.
This makes querying of event-sourced aggregates easy.

Event handler is publishing a WebSocket events on every update of a query model. 
This can be useful on the front-end to re-fetch the data via HTTP/REST endpoints. 

Each event handler allows 'reply' of events. Please note that 'reset handler' will be called before replay/reset starts to clear out the query model tables.
[AdminController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/web/AdminController.kt) expose endpoints for reseting tracking event processors/handlers.

#### 'Command' HTTP API

##### Create new Restaurant
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
  "menuItems": [
    {
      "id": "id1",
      "name": "name1",
      "price": 100
    }
  ],
  "name": "Fancy"
}' 'http://localhost:8080/api/command/restaurant/createcommand'
```
##### Create/Register new Customer
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
  "firstName": "Ivan",
  "lastName": "Dugalic",
  "orderLimit": 1000
}' 'http://localhost:8080/api/command/customer/createcommand'
```
##### Create/Hire new Courier
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
  "firstName": "John",
  "lastName": "Doe",
  "maxNumberOfActiveOrders": 20
}' 'http://localhost:8080/api/command/courier/createcommand'
```
##### Create/Place the Order
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
  "customerId": "CUSTOMER_ID",
  "orderItems": [
    {
      "id": "id1",
      "name": "name1",
      "price": 100,
      "quantity": 0
    }
  ],
  "restaurantId": "RESTAURANT_ID"
}' 'http://localhost:8080/api/command/order/createcommand'
```
Note: Replace CUSTOMER_ID and RESTAURANT_ID with concrete values.

##### Restaurant marks the Order as prepared
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' 'http://localhost:8080/api/command/restaurant/order/RESTAURANT_ORDER_ID/markpreparedcommand'

```
Note: Replace RESTAURANT_ORDER_ID with concrete value.

##### Courier takes/claims the Order that is ready for delivery (prepared)
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' 'http://localhost:8080/api/command/courier/COURIER_ID/order/COURIER_ORDER_ID/assigncommand'

```
Note: Replace COURIER_ID and COURIER_ORDER_ID with concrete values.

##### Courier marks the Order as delivered
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' 'http://localhost:8080/api/command/courier/order/COURIER_ORDER_ID/markdeliveredcommand'

```


#### 'Query' HTTP API
Application is using an event handler to subscribe to all interested domain events. Events are materialized in SQL database schema. 

HTTP/REST API for browsing the materialized data:

```
curl http://localhost:8080/api/query
```

#### Administration

##### Read all event processors
```
curl http://localhost:8080/api/administration/eventprocessors
```
##### Event processors reset

In cases when you want to rebuild projections (query models), replaying past events comes in handy. The idea is to start from the beginning of time and invoke all event handlers. 

```
curl -i -X POST 'http://localhost:8080/api/administration/eventprocessors/{EVENT PROCESSOR NAME}/reply'
```
##### Event processor status

Returns a map where the key is the segment identifier, and the value is the event processing status.
Based on this status we can determine whether the Processor is caught up and/or is replaying.
This can be used for Blue-Green deployment. You don't want to send queries to 'query model' if processor is not caught up and/or is replaying.

```
curl http://localhost:8080/api/administration/eventprocessors/{EVENT PROCESSOR NAME}/status

```
#### WebSocket (STOMP) API

WebSocket API (ws://localhost:8080/drestaurant/websocket) topics:

 - /topic/couriers.updates (noting that courier list has been updated, e.g. new courier has been created)
 - /topic/customers.updates (noting that customer list has been updated, e.g. new customer has been created)
 - /topic/orders.updates (noting that order list has been updated, e.g. new order has been created)
 - /topic/restaurants.updates (noting that restaurant list has been updated, e.g. new restaurant has been created)


Frontend part of the solution is available here [http://idugalic.github.io/digital-restaurant-angular](http://idugalic.github.io/digital-restaurant-angular/)

### Monolith 2 (REST API by not segregating Command and Query)

Source code: [https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest)

This application is using the second approach ('NOT segregating Command and Query') by exposing capabilities of our 'domain' via the REST API components that are responsible for
 
 - dispatching commands - [CommandController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/web/CommandController.kt)
 - querying the 'query model' (materialized views) - [Spring REST repositories](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/query/repository)


**We create one-to-one relation between a Command Model resource and a Query Model (materialized view) resource.**
We are using Spring Rest Data project to implement REST API, which will position us on the third level of [Richardson Maturity Model](https://martinfowler.com/articles/richardsonMaturityModel.html)

[Event handler](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/query/handler) is a central component. It consumes events, and creates Query Model / materialized views of aggregates.
Additionally, it will emit 'any change on Query Model' to Axon subscription queries, and let us subscribe on them within our [CommandController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/web/CommandController.kt) keeping our architecture clean.

Each event handler allows 'reply' of events. Please note that 'reset handler' will be called before replay/reset starts to clear out the query model tables.
[AdminController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/web/AdminController.kt) expose endpoints for reseting tracking event processors/handlers.

Although fully asynchronous designs may be preferable for a number of reasons, it is a common scenario that back-end teams are forced to provide a synchronous REST API on top of asynchronous CQRS+ES back-ends.

#### Restaurant management

##### Read all restaurants
```
curl http://localhost:8080/restaurants
```
##### Create new restaurant
```
curl -i -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
"menuItems": [
 {
   "id": "id1",
   "name": "name1",
   "price": 100
 }
],
"name": "Fancy"
}' 'http://localhost:8080/restaurants'
```
##### Mark restaurant order as prepared
```
curl -i -X PUT --header 'Content-Type: application/json' --header 'Accept: */*' 'http://localhost:8080/restaurants/RESTAURANT_ID/orders/RESTAURANT_ORDER_ID/markprepared'

```
#### Customer management

##### Read all customers
```
curl http://localhost:8080/customers
```
##### Create/Register new Customer
```
curl -i -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
"firstName": "Ivan",
"lastName": "Dugalic",
"orderLimit": 1000
}' 'http://localhost:8080/customers'
```

#### Courier management

##### Read all couriers
```
curl http://localhost:8080/couriers
```
##### Create/Hire new Courier
```
curl -i -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
"firstName": "John",
"lastName": "Doe",
"maxNumberOfActiveOrders": 20
}' 'http://localhost:8080/couriers'
```
##### Courier takes/claims the Order that is ready for delivery (prepared)
```
curl -i -X PUT --header 'Content-Type: application/json' --header 'Accept: */*' 'http://localhost:8080/couriers/COURIER_ID/orders/COURIER_ORDER_ID/assign'
```

##### Courier marks the order as delivered
```
curl -i -X PUT --header 'Content-Type: application/json' --header 'Accept: */*' 'http://localhost:8080/couriers/COURIER_ID/orders/COURIER_ORDER_ID/markdelivered'
```

#### Order management

##### Read all orders
```
 curl http://localhost:8080/orders
```

##### Create/Place the Order
```
curl -i -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
"customerId": "CUSTOMER_ID",
"orderItems": [
 {
   "id": "id1",
   "name": "name1",
   "price": 100,
   "quantity": 0
 }
],
"restaurantId": "RESTAURANT_ID"
}' 'http://localhost:8080/orders'
```
 Note: Replace CUSTOMER_ID and RESTAURANT_ID with concrete values.
 
#### Administration

##### Read all event processors
```
curl http://localhost:8080/administration/eventprocessors
```
##### Event processors reset

In cases when you want to rebuild projections (query models), replaying past events comes in handy. The idea is to start from the beginning of time and invoke all event handlers. 

```
curl -i -X POST 'http://localhost:8080/administration/eventprocessors/{EVENT PROCESSOR NAME}/reply'
```
##### Event processor status

Returns a map where the key is the segment identifier, and the value is the event processing status.
Based on this status we can determine whether the Processor is caught up and/or is replaying.
This can be used for Blue-Green deployment. You don't want to send queries to 'query model' if processor is not caught up and/or is replaying.

```
curl http://localhost:8080/administration/eventprocessors/{EVENT PROCESSOR NAME}/status

```
### Monolith 3 (STOMP over WebSockets API. We are async all the way)

Source code: [https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-websockets](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-websockets)


The WebSocket protocol (RFC 6455) defines an important new capability for web applications: full-duplex, two-way communication between client and server. It is an exciting new capability on the heels of a long history of techniques to make the web more interactive including Java Applets, XMLHttpRequest, Adobe Flash, ActiveXObject, various Comet techniques, server-sent events, and others.

This application is utilizing STOMP over Websockets protocol to expose capabilities of our 'domain' via components:
 
 - [WebController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-websockets/src/main/kotlin/com/drestaurant/web/WebController.kt)
 - [Event handler](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-websockets/src/main/kotlin/com/drestaurant/query/handler) is a central component. It consumes domain events, and creates 'query models' (materialized views) of aggregates. Aditonally, our event listener is publishing a WebSocket messages to topics on every update of a query model.
 - [AdminController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-websockets/src/main/kotlin/com/drestaurant/web/AdminController.kt) expose endpoints for reseting/replying tracking event processors/handlers.  Each event handler allows 'reply' of events. Please note that 'reset handler' will be called before replay/reset starts to clear out the query model tables.



#### STOMP over WebSockets API

WebSocket SockJS endpoint: `ws://localhost:8080/drestaurant/websocket`

##### Topics:

 - `/topic/couriers.updates` (courier list has been updated, e.g. new courier has been created)
 - `/topic/customers.updates` (customer list has been updated, e.g. new customer has been created)
 - `/topic/orders.updates` (order list has been updated, e.g. new order has been created)
 - `/topic/restaurants.updates` (restaurant list has been updated, e.g. new restaurant has been created)
 - `/topic/couriers/orders.updates` (courier order list has been updated, e.g. new courier order has been created)
 - `/topic/restaurants/orders.updates`(restaurant order list has been updated, e.g. new restaurant order has been created)

 
##### Message endpoints:

 - `/customers/createcommand`, messageType=[MESSAGE]
 - `/customers`, messageType=[SUBSCRIBE]
 - `/customers/{id}`, messageType=[SUBSCRIBE]
 - `/couriers/createcommand`, messageType=[MESSAGE]
 - `/couriers`, messageType=[SUBSCRIBE]
 - `/couriers/{id}`, messageType=[SUBSCRIBE]
 - `/couriers/orders/assigncommand`, messageType=[MESSAGE]
 - `/couriers/orders/markdeliveredcommand`, messageType=[MESSAGE]
 - `/couriers/orders`, messageType=[SUBSCRIBE]
 - `/couriers/orders/{id}`, messageType=[SUBSCRIBE]
 - `/restaurants/createcommand`, messageType=[MESSAGE]
 - `/restaurants`, messageType=[SUBSCRIBE]
 - `/restaurants/{id}`, messageType=[SUBSCRIBE]
 - `/orders/createcommand`, messageType=[MESSAGE]
 - `/orders`, messageType=[SUBSCRIBE]
 - `/orders/{id}`, messageType=[SUBSCRIBE]
 - `/restaurants/orders/markpreparedcommand`, messageType=[MESSAGE]
 - `/restaurants/orders`, messageType=[SUBSCRIBE]
 - `/restaurants/orders/{id}`, messageType=[SUBSCRIBE]
 - `/eventprocessors/{groupName}/reply`, messageType=[MESSAGE]
 - `/eventprocessors/{groupName}`, messageType=[SUBSCRIBE]
 - `/eventprocessors`, messageType=[SUBSCRIBE]


### Microservices 1 (HTTP, Websockets, Apache Kafka)

We designed and structured our [domain components](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs) in a modular way, 
and that enables us to choose different deployment strategy and decompose [Monolith 1](#monolith-1-http-and-websockets-api-by-segregating-command-and-query) to microservices. 

Each [microservice](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices):

 - has its own bounded context,
 - has its own JPA event store (we are not sharing the JPA Event Store between service)
 - we distribute events between them via Apache Kafka (we do not use Kafka as event(sourcing) store)
 - and we distribute commands (Command Bus) by Spring Cloud discovery and registry service (Eureka) 

 
#### Apache Kafka & event messages

Apache Kafka is a distributed streaming platform. It is used to route and distribute `events`.

##### Order of events (kafka topics & partitions)

The order of events matters in our scenario (eventsourcing).
For example, we might expect that a customer is created before anything else can happen to a customer.
When using Kafka, you can preserve the order of those events by putting them all in the same Kafka **partition**.
They must be in the same Kafka **topic** because different topics mean different partitions.

We [configured our Kafka instance](https://github.com/idugalic/digital-restaurant/blob/master/drestaurant-apps/drestaurant-microservices/docker-compose.yml) to crate only one topic (**axon-events**) with one partition initially.
 
##### Queue vs publish-subscribe (kafka groups)

If all consumers are from the same group, the Kafka model functions as a traditional **message queue** would.
All the records and processing is then load balanced.
**Each message would be consumed by one consumer of the group only.**
Each partition is connected to at most one consumer from a group.
 
When multiple consumer groups exist, the flow of the data consumption model aligns with the traditional **publish-subscribe** model.
**The messages are broadcast to all consumer groups.**

We [configured our (micro)services](https://github.com/idugalic/digital-restaurant/blob/master/drestaurant-apps/drestaurant-microservices/drestaurant-microservices-command-customer/src/main/resources/application.yml) to use publish-subscribe model, by setting unique consumer group id for each (micro)service.

#### Spring Cloud connector & command messages

The [Spring Cloud connector](https://docs.axoniq.io/reference-guide/1.3-infrastructure-components/command-dispatching#spring-cloud-connector) setup uses the service registration and discovery mechanism described by Spring Cloud for distributing the command bus (`commands`).
You are thus left free to choose which Spring Cloud implementation to use to distribute your commands.
An example implementation is the Eureka Discovery/Eureka Server combination.



#### 'Command' HTTP API

##### Create new Restaurant
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
  "menuItems": [
    {
      "id": "id1",
      "name": "name1",
      "price": 100
    }
  ],
  "name": "Fancy"
}' 'http://localhost:8084/api/command/restaurant/createcommand'
```
##### Create/Register new Customer
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
  "firstName": "Ivan",
  "lastName": "Dugalic",
  "orderLimit": 1000
}' 'http://localhost:8082/api/command/customer/createcommand'
```
##### Create/Hire new Courier
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
  "firstName": "John",
  "lastName": "Doe",
  "maxNumberOfActiveOrders": 20
}' 'http://localhost:8081/api/command/courier/createcommand'
```
##### Create/Place the Order
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
  "customerId": "CUSTOMER_ID",
  "orderItems": [
    {
      "id": "id1",
      "name": "name1",
      "price": 100,
      "quantity": 0
    }
  ],
  "restaurantId": "RESTAURANT_ID"
}' 'http://localhost:8083/api/command/order/createcommand'
```
Note: Replace CUSTOMER_ID and RESTAURANT_ID with concrete values.

##### Restaurant marks the Order as prepared
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' 'http://localhost:8084/api/command/restaurant/order/RESTAURANT_ORDER_ID/markpreparedcommand'

```
Note: Replace RESTAURANT_ORDER_ID with concrete value.

##### Courier takes/claims the Order that is ready for delivery (prepared)
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' 'http://localhost:8081/api/command/courier/COURIER_ID/order/COURIER_ORDER_ID/assigncommand'

```
Note: Replace COURIER_ID and COURIER_ORDER_ID with concrete values.

##### Courier marks the Order as delivered
```
curl -X POST --header 'Content-Type: application/json' --header 'Accept: */*' 'http://localhost:8081/api/command/courier/order/COURIER_ORDER_ID/markdeliveredcommand'

```

#### 'Query' HTTP API
Application is using an event handlers to subscribe to interested domain events. Events are materialized in SQL database schema and distributed over Apache Kafka

HTTP/REST API for browsing the materialized data:

```
curl http://localhost:8085/api/query
```

### Microservices 2 (REST, RabbitMQ)

We designed and structured our [domain components](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs) in a modular way, 
and that enables us to choose different deployment strategy and decompose [Monolith 2](#monolith-2-rest-api-by-not-segregating-command-and-query) to microservices. 


Each [microservice](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-rest):

 - has its own bounded context,
 - has its own JPA event(sourcing) store (we are not sharing the JPA Event Store)
 - we distribute events between them via RabbitMQ
 - and we distribute commands (Command Bus) by Spring Cloud discovery and registry service (Eureka) 

 
#### RabbitMQ & event messages

RabbitMQ is the most popular open source message broker. It is used to route and distribute `events`.
It supports several messaging protocols, directly and through the use of plugins:

 - AMQP
 - STOMP
 - MQTT
 - HTTP ...
 
##### Publish-subscribe

This messaging pattern supports delivering a message to multiple consumers.

We [configured our (micro)services](https://github.com/idugalic/digital-restaurant/blob/master/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-customer/src/main/resources/application.yml) to use publish-subscribe model, by setting unique queue for each (micro)service.
This queues are bind to one common exchange (`events.fanout.exchange`).

RabbitMQ allows more sophisticated message routing then Apache Kafka can offer.
Having one exchange bind to every service queue covered our scenario, but you can do more if you like.

#### Spring Cloud connector & command messages

The [Spring Cloud connector](https://docs.axoniq.io/reference-guide/1.3-infrastructure-components/command-dispatching#spring-cloud-connector) setup uses the service registration and discovery mechanism described by Spring Cloud for distributing the command bus (`commands`).
You are thus left free to choose which Spring Cloud implementation to use to distribute your commands.
An example implementation is the Eureka Discovery/Eureka Server combination.

NOTE: [version 2](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-rest-2) is available, utilizing [AxonServer](https://axoniq.io/product-overview/axon-server).We distribute `queries` now, out of the box, and we extract independent `query` microservice.


#### Restaurant management

##### Read all restaurants
```
curl http://localhost:8084/restaurants
```
##### Create new restaurant
```
curl -i -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
"menuItems": [
 {
   "id": "id1",
   "name": "name1",
   "price": 100
 }
],
"name": "Fancy"
}' 'http://localhost:8084/restaurants'
```
##### Mark restaurant order as prepared
```
curl -i -X PUT --header 'Content-Type: application/json' --header 'Accept: */*' 'http://localhost:8084/restaurants/RESTAURANT_ID/orders/RESTAURANT_ORDER_ID/markprepared'

```
#### Customer management

##### Read all customers
```
curl http://localhost:8082/customers
```
##### Create/Register new Customer
```
curl -i -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
"firstName": "Ivan",
"lastName": "Dugalic",
"orderLimit": 1000
}' 'http://localhost:8082/customers'
```

#### Courier management

##### Read all couriers
```
curl http://localhost:8081/couriers
```
##### Create/Hire new Courier
```
curl -i -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
"firstName": "John",
"lastName": "Doe",
"maxNumberOfActiveOrders": 20
}' 'http://localhost:8081/couriers'
```
##### Courier takes/claims the Order that is ready for delivery (prepared)
```
curl -i -X PUT --header 'Content-Type: application/json' --header 'Accept: */*' 'http://localhost:8081/couriers/COURIER_ID/orders/COURIER_ORDER_ID/assign'
```

##### Courier marks the order as delivered
```
curl -i -X PUT --header 'Content-Type: application/json' --header 'Accept: */*' 'http://localhost:8081/couriers/COURIER_ID/orders/COURIER_ORDER_ID/markdelivered'
```

#### Order management

##### Read all orders
```
 curl http://localhost:8083/orders
```

##### Create/Place the Order
```
curl -i -X POST --header 'Content-Type: application/json' --header 'Accept: */*' -d '{
"customerId": "CUSTOMER_ID",
"orderItems": [
 {
   "id": "id1",
   "name": "name1",
   "price": 100,
   "quantity": 0
 }
],
"restaurantId": "RESTAURANT_ID"
}' 'http://localhost:8083/orders'
```
 Note: Replace CUSTOMER_ID and RESTAURANT_ID with concrete values.
 
### Microservices 3 (Websockets, AxonServer)

We designed and structured our [domain components](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs) in a modular way, 
and that enables us to choose different deployment strategy and decompose [Monolith 3](#monolith-3-stomp-over-websockets-api-we-are-async-all-the-way) to microservices. 

Each [microservice](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-rest):

 - has its own bounded context,
 - has shared event(sourcing) storage (AxonServer)
 - and we route and distribute messages (`events`, `commands`, `queries`) between them via AxonServer
 
#### AxonServer & event messages & command messages & query messages

Both [AxonFramework](https://axoniq.io/product-overview/axon-framework) and [AxonServer](https://axoniq.io/product-overview/axon-server) form [Axon platform](https://docs.axoniq.io/reference-guide/).

The key characteristics of [AxonServer](https://axoniq.io/product-overview/axon-server) are:
 - Dedicated infrastructure for exchanging three types of messages (`commands`, `events`, `queries`) in a message-driven micro-services environment
 - Purpose-built database system optimized for the storage of event data of the type that is generated by applications that use the event sourcing architecture pattern
 - Built-in knowledge on CQRS message patterns
 - Easy-to-use and easy-to-manage

AxonServer connector is configured by default:

 ```
 <dependency>
     <groupId>org.axonframework</groupId>
     <artifactId>axon-spring-boot-starter</artifactId>
     <version>${axon.version}</version>
 </dependency>

 ```

Alternatively, you can exclude AxonServer connector and fallback to JPA event store and storage in general.
In that case you have to choose (and configure, and operate) other options (Spring cloud, Kafka, RabbitMQ, ...) to distribute your messages.
We did this already in other apps (Micorservices1, Microservices2, ...) as a proof that you can benefit from AxonFramework programming model only (without AxonServer as an infrastructural component)

```
<dependency>
    <groupId>org.axonframework</groupId>
    <artifactId>axon-spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.axonframework</groupId>
            <artifactId>axon-server-connector</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```


AxonFramework and AxonServer are `open source`. [Axon Server Enterprise](https://axoniq.io/product-overview/axon-enterprise) is targeted towards mission-critical, medium to large scale production deployments of Axon.


#### STOMP over WebSockets API


##### Customer (command side)

WebSocket SockJS endpoint: `ws://localhost:8081/customer/websocket`


 - `/app/customers/createcommand`, messageType=[MESSAGE]
 
##### Courier (command side) 

WebSocket SockJS endpoint: `ws://localhost:8082/courier/websocket`


 - `/app/couriers/createcommand`, messageType=[MESSAGE]
 - `/app/couriers/orders/assigncommand`, messageType=[MESSAGE]
 - `/app/couriers/orders/markdeliveredcommand`, messageType=[MESSAGE]
 
##### Restaurant (command side)

WebSocket SockJS endpoint: `ws://localhost:8084/restaurant/websocket`


 - `/app/restaurants/createcommand`, messageType=[MESSAGE]
 - `/app/restaurants/orders/markpreparedcommand`, messageType=[MESSAGE]
 
##### Order (command side)

WebSocket SockJS endpoint: `ws://localhost:8083/order/websocket`

 - `/app/orders/createcommand`, messageType=[MESSAGE]

##### Query side

WebSocket SockJS endpoint: `ws://localhost:8085/query/websocket`


 - `/app/customers`, messageType=[SUBSCRIBE]
 - `/app/customers/{id}`, messageType=[SUBSCRIBE]
 - `/app/couriers`, messageType=[SUBSCRIBE]
 - `/app/couriers/{id}`, messageType=[SUBSCRIBE]
 - `/app/couriers/orders`, messageType=[SUBSCRIBE]
 - `/app/couriers/orders/{id}`, messageType=[SUBSCRIBE]
 - `/app/restaurants`, messageType=[SUBSCRIBE]
 - `/app/restaurants/{id}`, messageType=[SUBSCRIBE]
 - `/app/orders`, messageType=[SUBSCRIBE]
 - `/app/orders/{id}`, messageType=[SUBSCRIBE]
 - `/app/restaurants/orders`, messageType=[SUBSCRIBE]
 - `/app/restaurants/orders/{id}`, messageType=[SUBSCRIBE]
 - `/topic/couriers.updates` (courier list has been updated, e.g. new courier has been created)
 - `/topic/customers.updates` (customer list has been updated, e.g. new customer has been created)
 - `/topic/orders.updates` (order list has been updated, e.g. new order has been created)
 - `/topic/restaurants.updates` (restaurant list has been updated, e.g. new restaurant has been created)
 - `/topic/couriers/orders.updates` (courier order list has been updated, e.g. new courier order has been created)
 - `/topic/restaurants/orders.updates`(restaurant order list has been updated, e.g. new restaurant order has been created)



## Development

This project is driven using [Maven][mvn].

### Clone
```bash
$ git clone https://github.com/idugalic/digital-restaurant
```

### Build

```bash
$ cd digital-restaurant
$ mvn clean install
```
### Run monolith 1 (HTTP and WebSockets API by segregating Command and Query)

```bash
$ cd digital-restaurant/drestaurant-apps/drestaurant-monolith
$ mvn spring-boot:run
```

### Run monolith 2 (REST API by not segregating Command and Query)

```bash
$ cd digital-restaurant/drestaurant-apps/drestaurant-monolith-rest
$ mvn spring-boot:run
```

### Run monolith 3 (STOMP over WebSockets API. We are async all the way)

```bash
$ cd digital-restaurant/drestaurant-apps/drestaurant-monolith-websockets
$ mvn spring-boot:run
```

### Run microservices 1 (HTTP, Websockets, Apache Kafka)

NOTE: Docker is required. We use it to start Apache Kafka with Zookeeper

```bash
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices
$ docker-compose up -d
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices/drestaurant-microservices-discovery-server
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices/drestaurant-microservices-command-courier
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices/drestaurant-microservices-command-customer
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices/drestaurant-microservices-command-restaurant
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices/drestaurant-microservices-command-order
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices/drestaurant-microservices-query
$ mvn spring-boot:run
```

### Run microservices 2 (REST, RabbitMQ)

NOTE: Docker is required. We use it to start RabbitMQ

```bash
$ docker run -d --hostname my-rabbit --name some-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-courier
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-courier
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-customer
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-restaurant
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-order
$ mvn spring-boot:run
```
### Run microservices 3 (Websockets, AxonDB and AxonHub)

[AxonServer](https://axoniq.io/product-overview/axon-server) is required.

```bash
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-websockets/drestaurant-microservices-websockets-comand-courier
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-websockets/drestaurant-microservices-websockets-comand-customer
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-websockets/drestaurant-microservices-websockets-comand-restaurant
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-websockets/drestaurant-microservices-websockets-comand-order
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-websockets/drestaurant-microservices-websockets-query
$ mvn spring-boot:run
```

## Continuous delivery

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
