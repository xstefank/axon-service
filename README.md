# axon-service

example application using [Axon framework](http://www.axonframework.org/) with distributed Saga.

*this project is moved from the repository [xstefank/learning-tests](https://github.com/xstefank/learning-tests), prior development can be tracked there*

## Build and running

### Docker compose

the esasiest way to run this application:

1. build
    * `mvn clean install`

1. start
    * `docker-compose up -d --build`
    
1. stop
    * `docker-compose down --remove-orphans`

### Manual start

The project consists of three microservices connected throuch spring cloud Eureka server for command dispatching and throuch AMQP queue for event handling.

1. install service-model jar into local maven repository
    * `cd service-model`
    * `mvn clean install`

1. download and start rabbitmq for your operating system
    * `sudo systemctl start rabbitmq-server.service`
    
1. download and start H2 database
    * http://www.h2database.com/html/download.html
    * `cd H2_HOME && chmod +x bin/h2.sh && bin/h2.sh -webPort 8083`
        * NOTE: by default H2 runs on the port 8082 which is used for invoice-service

1. run the Eureka registration server
    * `cd registration-server`
    * `mvn clean package exec:java`
    * application runs on the port 8761
    
1. in different terminal - run the order service providing the saga management
    * `cd order-service`
    * `mvn clean package exec:java`
    * application runs on the port 8080
    
1. in different terminal - run the shipment service
    * `cd shipment-service`
    * `mvn clean package exec:java`
    * application runs on the port 8081
    
1. in different terminal - run the invoice service
    * `cd invoice-service`
    * `mvn clean package exec:java`
    * application runs on the port 8082
    
You should see the registration of the services in registration-server log or you can go to [localhost:8761](http://localhost:8761) to check that all services are registered.

After the Spring Cloud sends the heartbeats to all service (need to boot up Axon's distributed command bus) you can test the application by issuing request for order


`curl -X POST -H "Content-Type: application/json" -d '{"productId":"testProduct", "comment":"testComment", "price":"20"}' http://localhost:8080/order`

For the compensation scenario you can run:

`curl -X POST -H "Content-Type: application/json" -d '{"productId":"fail-saga", "comment":"testComment", "price":"20"}' http://localhost:8080/order`




