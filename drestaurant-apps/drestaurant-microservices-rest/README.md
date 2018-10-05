### Microservices / REST / RabbitMQ

*This is a thin layer which coordinates the application activity. It does not contain business logic. It does not hold the state of the business objects*

We designed and structured our [loosely coupled components](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs) in a modular way, 
and that enable us to choose different deployment strategy and take first step towards Microservices architectural style.

Each [microservice](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-rest):

 - has its own bounded context,
 - has its own JPA event(sourcing) store (we are not sharing the JPA Event Store)
 - and we distribute events between them via RabbitMQ
 
#### RabbitMQ

RabbitMQ is the most popular open source message broker.
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


### Run microservices

NOTE: Docker is required. We use it to start RabbitMQ

```bash
$ docker run -d --hostname my-rabbit --name some-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-courier
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-customer
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-restaurant
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-order
$ mvn spring-boot:run
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

### Infrastructure
- [H2, MySQL (event store, materialized views)][mysql]
- [RabbitMQ][rabbitMQ]

 

[mvn]: https://maven.apache.org/
[kotlin]: https://kotlinlang.org/
[spring]: https://spring.io/
[axonframework]: https://axoniq.io/
[mysql]: https://www.mysql.com/
[rabbitMQ]: https://www.rabbitmq.com/
[kafka]: https://kafka.apache.org/
[pivotalCF]: https://run.pivotal.io/
