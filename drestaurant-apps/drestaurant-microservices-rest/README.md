## Microservices / REST / RabbitMQ
#### :octocat: [digital-restaurant](https://github.com/idugalic/digital-restaurant)/drestaurant-apps/drestaurant-monolith-rest :octocat:

*This is a thin layer which coordinates the application activity. It does not contain business logic. It does not hold the state of the business objects*

We designed and structured our [loosely coupled components](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs) in a modular way, 
and that enable us to choose different deployment strategy and take first step towards Microservices architectural style.

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
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-discovery-server
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-customer
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-restaurant
$ mvn spring-boot:run
$ cd digital-restaurant/drestaurant-apps/drestaurant-microservices-rest/drestaurant-microservices-rest-order
$ mvn spring-boot:run
```


### Infrastructure and Platform (As A Service)
- [H2 - java SQL databse][h2]
- [RabbitMQ][rabbitMQ]

[mvn]: https://maven.apache.org/
[kotlin]: https://kotlinlang.org/
[spring]: https://spring.io/
[axonframework]: https://axoniq.io/
[mysql]: https://www.mysql.com/
[h2]: http://h2database.com/html/main.html
[rabbitMQ]: https://www.rabbitmq.com/
[kafka]: https://kafka.apache.org/
[pivotalCF]: https://run.pivotal.io/
