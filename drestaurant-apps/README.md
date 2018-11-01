## Application/s layer
#### :octocat: [digital-restaurant](https://github.com/idugalic/digital-restaurant)/drestaurant-apps :octocat:

This is a thin layer which coordinates the application activity. It does not contain business logic. It does not hold the state of the business objects

We have created more 'web' applications (standalone Spring Boot applications) to demonstrate the use of different architectural styles, API designs and deployment strategies by utilizing [components from the domain layer](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-libs) in a different way:

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

**Microservices**

 - [Microservices 1](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices)
    - Monolith 1 decomposed
    - **HTTP and WebSockets API** by segregating Command and Query
    - we don't synchronize on the backend
    - we provide WebSockets for the frontend to handle async nature of the backend
    - we use Apache Kafka to distribute events between services
    - we use Spring Cloud discovery and registry service (Eureka) to distribute commands between services (bounded contexts)
 - [Microservices 2](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-rest)
    - Monolith 2 decomposed
    - **REST API** by not segregating Command and Query
    - we synchronize on the backend side
    - we use RabbitMQ to distribute events between services (bounded contexts)
    - we use Spring Cloud discovery and registry service (Eureka) to distribute commands between services (bounded contexts)
 - [Microservices 3](https://github.com/idugalic/digital-restaurant/tree/master/drestaurant-apps/drestaurant-microservices-websockets)
    - Monolith 3 decomposed
    - **WebSockets API**
    - we are async all the way
    - we use [AxonServer](https://axoniq.io/product-overview/axon-server) to route and distribute all type of messages (commands, events, queries) between services (bounded contexts)
