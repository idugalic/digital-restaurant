# [digital-restaurant](https://github.com/idugalic/digital-restaurant)/drestaurant-apps/drestaurant-monolith-websockets

## Monolith 3 (STOMP over WebSockets API. We are async all the way)

*This is a thin layer which coordinates the application activity. It does not contain business logic. It does not hold the state of the business objects*

This application is utilizing STOMP over Websockets protocol to expose capabilities of our 'domain' via components:
 
 - [WebController](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-websockets/src/main/kotlin/com/drestaurant/web/WebController.kt)
 - [Event listener](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-monolith-websockets/src/main/kotlin/com/drestaurant/query/handler) is a central component. It consumes domain events, and creates 'query models' (materialized views) of aggregates. Aditonally, our event listener is publishing a WebSocket messages to topics on every update of a query model. 

### STOMP over WebSockets API

WebSocket SockJS endpoint: `ws://localhost:8080/drestaurant/websocket`

#### Topics:

 - `/topic/couriers.updates` (courier list has been updated, e.g. new courier has been created)
 - `/topic/customers.updates` (customer list has been updated, e.g. new customer has been created)
 - `/topic/orders.updates` (order list has been updated, e.g. new order has been created)
 - `/topic/restaurants.updates` (restaurant list has been updated, e.g. new restaurant has been created)
 - `/topic/couriers/orders.updates` (courier order list has been updated, e.g. new courier order has been created)
 - `/topic/restaurants/orders.updates`(restaurant order list has been updated, e.g. new restaurant order has been created)

 
#### Message endpoints:

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


### Run the application

```bash
$ cd digital-restaurant/drestaurant-apps/drestaurant-monolith-websockets
$ mvn spring-boot:run
```


### Continuous integration

We have one deployment pipeline for all applications and libraries within this repository. In addition, all projects in the repository share the same dependencies. Hence, there are no version conflicts because everyone has to use the same/the latest (SNAPSHOTS) version. And you don't need to deal with a private NPM (JavaScript) or Maven (Java) registry when you just want to use your own libraries.
This setup and project structure is usually addressed as a [monorepo](https://medium.com/@maoberlehner/monorepos-in-the-wild-33c6eb246cb9).

### Technology

#### Language
- [Kotlin][kotlin]

#### Frameworks and Platforms
- [Spring (SpringBoot, SpringCloud, SpringData, SpringDataRest)][spring]
- [AxonFramework][axonframework]

#### Continuous Integration and Delivery 
- Travis

#### Infrastructure
- [H2 - java SQL databse][h2]
 

[mvn]: https://maven.apache.org/
[kotlin]: https://kotlinlang.org/
[spring]: https://spring.io/
[axonframework]: https://axoniq.io/
[mysql]: https://www.mysql.com/
[h2]: http://h2database.com/html/main.html