# [projects](http://idugalic.github.io/projects)/digital-restaurant [![Build Status](https://travis-ci.org/idugalic/digital-restaurant.svg?branch=kotlin)](https://travis-ci.org/idugalic/digital-restaurant)

*'d-restaurant' is an example of an application that is built using Event Sourcing and CQRS. The application is written in Kotlin, and uses Spring Boot. It is built using Axonframework, which is an application framework based on event sourcing and CQRS.*

Customers use the website application to place food orders at local restaurants. Application coordinates a network of couriers who deliver the orders.


## Table of Contents

  * [Domain](#domain)
     * [Core subdomains](#core-subdomains)
     * [Generic subdomains](#generic-subdomains)
     * [Organisation vs encapsulation](#organisation-vs-encapsulation)
  * [Application/s](#applications)
     * [Monolith (HTTP and WebSockets API by segregating Command and Query)](#monolith-http-and-websockets-api-by-segregating-command-and-query)
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
     * [Microservices](#microservices)
  * [Development](#development)
     * [Clone](#clone)
     * [Build](#build)
     * [Run monolith (HTTP and WebSockets API by segregating Command and Query)](#run-monolith-http-and-websockets-api-by-segregating-command-and-query)
     * [Run monolith 2 (REST API by not segregating Command and Query)](#run-monolith-2-rest-api-by-not-segregating-command-and-query)
     * [Run monolith 3 (STOMP over WebSockets API. We are async all the way)](#run-monolith-3-stomp-over-websockets-api-we-are-async-all-the-way)
  * [Continuous delivery](#continuous-delivery)
  * [Technology](#technology)
     * [Language](#language)
     * [Frameworks and Platforms](#frameworks-and-platforms)
     * [Continuous Integration and Delivery](#continuous-integration-and-delivery)
     * [Infrastructure and Platform (As A Service)](#infrastructure-and-platform-as-a-service)
  * [References and further reading](#references-and-further-reading)


## Domain

*This layer contains information about the domain. This is the heart of the business software. The state of business objects is held here. Persistence of the business objects and possibly their state is delegated to the infrastructure layer*

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

 - The Restaurant component has a simpler view of an order aggregate (RestaurantOrder). Its version of an Order simply consist of a status and a list of line item, which tell the restaurant what to prepare. Additionally, we use event-driven mechanism called [sagas](https://docs.axonframework.org/part-ii-domain-logic/sagas) to [manage invariants between Restaurant aggregate and RestaurantOrder aggregate](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-restaurant/src/main/kotlin/com/drestaurant/restaurant/domain/RestaurantOrderSaga.kt) (e.g. Restaurant order should have only menu items that are on the Restaurant menu)

 - The Courier component has a different view of an order aggregate (CourierOrder). Its version of an Order simply consist of a status and a address, which tell the courier how and where to deliver the order. Additionally, we use saga to [manage invariants between Courier aggregate and CourierOrder aggregate](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-courier/src/main/kotlin/com/drestaurant/courier/domain/CourierOrderSaga.kt) (e.g. Courier can deliver a limited number of orders)

 - The Customer component has a different view of an order aggregate (CustomerOrder). Its version of an Order simply consist of a status and a address, which tell the courier how and where to deliver the order. Additionally, we use saga to [manage invariants between Customer aggregate and CustomerOrder aggregate](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-customer/src/main/kotlin/com/drestaurant/customer/domain/CustomerOrderSaga.kt) (e.g. Customer has an order limit)

 - We must maintain consistency between these different 'order' aggregates in different components/domains. For example, once the Order component has initiated order creation it must trigger the creation of RestaurantOrder in the Restaurant component. We will [maintain consistency between components/bounded-context using sagas](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-order/src/main/kotlin/com/drestaurant/order/domain/OrderSaga.kt).

![](digital-restaurant-state-machine.png)

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



### Generic subdomains
Other subdomains facilitate the business, but are not core to the business. In general, these types of pieces can be purchased from a vendor or outsourced. Those are **generic subdomains**:

- [Accounting subdomain](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-accounting)

Event sourcing is probably not needed within your 'generic subdomain'.

As Eric puts it into numbers, the 'core domain' should deliver about 20% of the total value of the entire system, be about 5% of the code base, and take about 80% of the effort.

### Organisation vs encapsulation 

When you make all types in your application public, the packages are simply an organisation mechanism (a grouping, like folders) rather than being used for encapsulation. Since public types can be used from anywhere in a codebase, you can effectively ignore the packages.

The way Java types are placed into packages (components) can actually make a huge difference to how accessible (or inaccessible) those types can be when Java's access modifiers are applied appropriately. Bundling the types into a smaller number of packages allows for something a little more radical. Since there are fewer inter-package dependencies, you can start to restrict the access modifiers.
Kotlin language doesn't have 'package' modifer as Java has. It has 'internal' modifer which restricts accessiblity of the class to the whole module (compile unit, jar file...). This makes a difference, and you have more freedom to structure your source code, and provide good public API of the component.

For example, our [Customer component](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-customer) classes are placed in one `com.drestaurant.customer.domain` package, with all classes marked as 'internal'.
Public classes are placed in `com.drestaurant.customer.domain.api` and they are forming an API for this component. This API consist of [commands](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-customer/src/main/kotlin/com/drestaurant/customer/domain/api) and [events](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs/drestaurant-common/src/main/kotlin/com/drestaurant/customer/domain/api) only.

## Application/s

*This is a thin layer which coordinates the application activity. It does not contain business logic. It does not hold the state of the business objects*

We have created more 'web' applications with different architectural styles, API designs and deployment strategies by utilizing components from the domain layer in different way:

**Monolithic**

 - Monolith 1 (HTTP and WebSockets API **by segregating Command and Query**. We don't synchronize on the backend, but we provide WebSockets for the frontend to handle async nature of the backend)
 - Monolith 2 (REST API **by not segregating Command and Query**. We synchronize on the backend side)
 - Monolith 3 (WebSockets API. We are async all the way ;))

**Microservices**

 - Microservices 1 (HTTP and WebSockets API **by segregating Command and Query**. We don't synchronize on the backend, but we provide WebSockets for the frontend to handle async nature of the backend)
 
### Monolith (HTTP and WebSockets API by segregating Command and Query)

Source code: [https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith)

A recurring question with CQRS and EventSourcing is how to put a synchronous HTTP front-end on top of an asynchronous CQRS back-end.

In general there are two approaches:

 - **segregating Command and Query** - resources representing Commands (request for changes) and resources representing Query Models (the state of the domain) are decoupled
 - **not segregating Command and Query** - one-to-one relation between a Command Model resource and a Query Model resource
 
 This application is using the first approach ('segregating Command and Query') by exposing capabilities of our 'domain' via the HTTP/REST API components that are responsible for
 - dispatching commands - [CommandController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/web/CommandController.kt)
 - querying the 'query model' (materialized views) - [Spring REST repositories](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/query/repository)

**There is no one-to-one relation between a Command resource and a Query Model resource. This makes easier to implement multiple representations of the same underlying domain entity as separate resources.**


[Event listener](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith/src/main/kotlin/com/drestaurant/query/handler) is a central component. It consumes events, and creates 'query models' (materialized views) of aggregates.
This makes querying of event-sourced aggregates easy.

Event listener is publishing a WebSocket events on every update of a query model. 
This can be useful on the front-end to re-fetch the data via HTTP/REST endpoints. 

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

Sometimes, you are simply being required to deliver REST API.

A recurring question with CQRS and EventSourcing is how to put a synchronous REST front-end on top of an asynchronous CQRS back-end.

In general there are two approaches:

 - **segregating Command and Query** - resources representing Commands (request for changes) and resources representing Query Models (the state of the domain) are decoupled
 - **not segregating Command and Query** - one-to-one relation between a Command Model resource and a Query Model resource
 
This application is using the second approach ('NOT segregating Command and Query') by exposing capabilities of our 'domain' via the REST API components that are responsible for
 
 - dispatching commands - [CommandController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/web/CommandController.kt)
 - querying the 'query model' (materialized views) - [Spring REST repositories](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/query/repository)


**We create one-to-one relation between a Command Model resource and a Query Model (materialized view) resource.**
Note that we are utilizing Spring Rest Data project to implement REST API, and that will position us on the third level of [Richardson Maturity Model](https://martinfowler.com/articles/richardsonMaturityModel.html)

[Event listener](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/query/handler) is a central component. It consumes events, and creates Query Model / materialized views of aggregates.
Additionally, it will emit 'any change on Query Model' to Axon subscription queries, and let us subscribe on them within our [CommandController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-rest/src/main/kotlin/com/drestaurant/web/CommandController.kt) keeping our architecture clean.

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
 - [Event listener](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-websockets/src/main/kotlin/com/drestaurant/query/handler) is a central component. It consumes domain events, and creates 'query models' (materialized views) of aggregates. Aditonally, our event listener is publishing a WebSocket messages to topics on every update of a query model. 

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


### Microservices

We designed and structured our loosely coupled components in a modular way, 
and that enables us to choose different deployment strategy and take first step towards Microservices architectural style.

Microservices are followed with the team/s cultural change, and with complicated continuous delivery process.
The general plan would be for each [microservice](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices) to live in its own bounded context, and to distribute events between them via Kafka or RabbitMQ (by NOT sharing the Event Store).

This is out of the scope for this project. There is a regularly maintained [lab project](http://lab.idugalic.pro/) that is covering this topic in more details.

## Development

This project is driven using [Maven][mvn].

### Clone
```bash
$ git clone https://gitlab.com/d-restaurant/d-restaurant-backend.git
```

### Build

```bash
$ cd d-restaurant-backend
$ ./mvnw clean install
```
### Run monolith (HTTP and WebSockets API by segregating Command and Query)

```bash
$ cd d-restaurant-backend/drestaurant-apps/drestaurant-monolith
$ ../../mvnw spring-boot:run
```

### Run monolith 2 (REST API by not segregating Command and Query)

```bash
$ cd d-restaurant-backend/drestaurant-apps/drestaurant-monolith-rest
$ ../../mvnw spring-boot:run
```

### Run monolith 3 (STOMP over WebSockets API. We are async all the way)

```bash
$ cd d-restaurant-backend/drestaurant-apps/drestaurant-monolith-websockets
$ ../../mvnw spring-boot:run
```

## Continuous delivery

We have one deployment pipeline for all applications and libraries within this repository. In addition, all projects in the repository share the same dependencies. Hence, there are no version conflicts because everyone has to use the same/the latest (SNAPSHOTS) version. And you don't need to deal with a private NPM (JavaScript) or Maven (Java) registry when you just want to use your own libraries.
This setup and project structure is usually addressed as a [monorepo](https://medium.com/@maoberlehner/monorepos-in-the-wild-33c6eb246cb9).

## Technology

### Language
- [Kotlin][kotlin]

### Frameworks and Platforms
- [Spring (spring boot, spring cloud, spring data, spring data rest)][spring]
- [Axonframework (eventsourcing, CQRS)][axonframework]

### Continuous Integration and Delivery 
- Travis

### Infrastructure and Platform (As A Service)
- [H2, MySQL (event store, materialized views)][mysql]

## References and further reading
**Inspired by the book "Microservices Patterns" - Chris Richardson**

  - https://github.com/microservice-patterns/ftgo-application
  - https://spring.io/blog/2018/04/11/event-storming-and-spring-with-a-splash-of-ddd
  - http://www.codingthearchitecture.com/2016/04/25/layers_hexagons_features_and_components.html
  
 

[mvn]: https://maven.apache.org/
[kotlin]: https://kotlinlang.org/
[spring]: https://spring.io/
[axonframework]: https://axoniq.io/
[mysql]: https://www.mysql.com/
[rabbitMQ]: https://www.rabbitmq.com/
[pivotalCF]: https://run.pivotal.io/
