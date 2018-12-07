## Microservices / HTTP & Websockets / Apache Kafka
#### :octocat: /drestaurant-apps/drestaurant-microservices :octocat:

*This is a thin layer which coordinates the application activity. It does not contain business logic. It does not hold the state of the business objects*

We designed and structured our [loosely coupled components](../../drestaurant-libs) in a modular way, 
and that enable us to choose different deployment strategy and take first step towards Microservices architectural style.

Each microservice:

 - has its own bounded context,
 - has its own JPA event(sourcing) store (we are not sharing the JPA Event Store)
 - we distribute events between them via Apache Kafka (we do not use Kafka as event(sourcing) store)
 - and we distribute commands (Command Bus) by Spring Cloud discovery and registry service (Eureka) 
 
#### Apache Kafka & event messages

Apache Kafka is a distributed streaming platform. It is used to route and distribute `events`.

##### Order of events (kafka topics & partitions)

The order of events matters in our scenario (eventsourcing).
For example, we might expect that a customer is created before anything else can happen to a customer.
When using Kafka, you can preserve the order of those events by putting them all in the same Kafka **partition**.
They must be in the same Kafka **topic** because different topics mean different partitions.

We [configured our Kafka instance](docker-compose.yml) to crate only one topic (**axon-events**) with one partition initially.
 
##### Queue vs publish-subscribe (kafka groups)

If all consumers are from the same group, the Kafka model functions as a traditional **message queue** would.
All the records and processing is then load balanced.
**Each message would be consumed by one consumer of the group only.**
Each partition is connected to at most one consumer from a group.
 
When multiple consumer groups exist, the flow of the data consumption model aligns with the traditional **publish-subscribe** model.
**The messages are broadcast to all consumer groups.**

We [configured our (micro)services](drestaurant-microservices-command-customer/src/main/resources/application.yml) to use publish-subscribe model, by setting unique consumer group id for each (micro)service.

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


### Infrastructure and Platform (As A Service)
- [H2 - java SQL databse][h2]
- [Apache Kafka][kafka]


[mvn]: https://maven.apache.org/
[kotlin]: https://kotlinlang.org/
[spring]: https://spring.io/
[axonframework]: https://axoniq.io/
[mysql]: https://www.mysql.com/
[h2]: http://h2database.com/html/main.html
[rabbitMQ]: https://www.rabbitmq.com/
[kafka]: https://kafka.apache.org/
[pivotalCF]: https://run.pivotal.io/
