package org.learn.axonframework.orderservice;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.commandhandling.AsynchronousCommandBus;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.distributed.AnnotationRoutingStrategy;
import org.axonframework.commandhandling.distributed.CommandBusConnector;
import org.axonframework.commandhandling.distributed.CommandRouter;
import org.axonframework.commandhandling.distributed.DistributedCommandBus;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.SubscribingEventProcessor;
import org.axonframework.eventhandling.saga.AnnotatedSagaManager;
import org.axonframework.eventhandling.saga.ResourceInjector;
import org.axonframework.eventhandling.saga.repository.AnnotatedSagaRepository;
import org.axonframework.eventhandling.saga.repository.SagaStore;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.messaging.interceptors.TransactionManagingInterceptor;
import org.axonframework.serialization.Serializer;
import org.axonframework.springcloud.commandhandling.SpringCloudCommandRouter;
import org.axonframework.springcloud.commandhandling.SpringHttpCommandBusConnector;
import org.learn.axonframework.orderservice.saga.OrderManagementSaga;
import org.learn.axonframework.orderservice.swagger.SwaggerConfiguration;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PreDestroy;

@EnableDiscoveryClient
@SpringBootApplication
@Import(SwaggerConfiguration.class)
public class OrderServiceApplication {

    private SubscribingEventProcessor shipmentSubscribingEventProcessor;
    private SubscribingEventProcessor invoiceSubscribingEventProcessor;

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    public Exchange orderExchange() {
        return ExchangeBuilder.fanoutExchange("OrderEvents").durable(true).build();
    }

    @Bean
    public Queue queryOrderQueue() {
        return QueueBuilder.durable("QueryOrderQueue").build();
    }

    @Bean
    public Binding queryOrderBinding() {
        return BindingBuilder.bind(queryOrderQueue()).to(orderExchange()).with("*").noargs();
    }


    @Bean
    @Qualifier("orderShipmentEvents")
    public SpringAMQPMessageSource orderShipmentEvents(Serializer serializer) {
        return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

            @RabbitListener(queues = "OrderShipmentQueue")
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                System.out.println("XXXXXXXX");
                Thread.sleep(50);
                super.onMessage(message, channel);
            }
        };
    }

    @Bean
    @Qualifier("orderInvoiceEvents")
    public SpringAMQPMessageSource orderInvoiceEvents(Serializer serializer) {
        return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

            @RabbitListener(queues = "OrderInvoiceQueue")
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                System.out.println("YYYYYYYY");
                Thread.sleep(50);
                super.onMessage(message, channel);
            }
        };
    }

    @Autowired
    public void configure(AmqpAdmin admin) {
        admin.declareExchange(orderExchange());

        admin.declareQueue(queryOrderQueue());
        admin.declareBinding(queryOrderBinding());
    }

    //the manual registration of saga event subscription because of processing events from AMQP queue
    @Autowired
    @SuppressWarnings("unchecked")
    public void registerShipmentEvents(SagaStore sagaStore, @Qualifier("orderShipmentEvents") SpringAMQPMessageSource springAMQPMessageSource,
                           ResourceInjector resourceInjector, ParameterResolverFactory parameterResolverFactory, TransactionManager transactionManager) {
        String simpleName = OrderManagementSaga.class.getSimpleName();

        AnnotatedSagaRepository sagaRepository = new AnnotatedSagaRepository<>(OrderManagementSaga.class, sagaStore, resourceInjector, parameterResolverFactory);
        AnnotatedSagaManager<OrderManagementSaga> sagaManager = new AnnotatedSagaManager<>(OrderManagementSaga.class, sagaRepository, parameterResolverFactory);

        shipmentSubscribingEventProcessor = new SubscribingEventProcessor(simpleName + "Processor", sagaManager, springAMQPMessageSource);
        shipmentSubscribingEventProcessor.registerInterceptor(new TransactionManagingInterceptor<>(transactionManager));
        shipmentSubscribingEventProcessor.start();
        System.out.println("shipmentSubscribingEventProcessor STARTED");
    }

    @Autowired
    @SuppressWarnings("unchecked")
    public void registerInvoiceEvents(SagaStore sagaStore, @Qualifier("orderInvoiceEvents") SpringAMQPMessageSource springAMQPMessageSource,
                           ResourceInjector resourceInjector, ParameterResolverFactory parameterResolverFactory, TransactionManager transactionManager) {
        String simpleName = OrderManagementSaga.class.getSimpleName();

        AnnotatedSagaRepository sagaRepository = new AnnotatedSagaRepository<>(OrderManagementSaga.class, sagaStore, resourceInjector, parameterResolverFactory);
        AnnotatedSagaManager<OrderManagementSaga> sagaManager = new AnnotatedSagaManager<>(OrderManagementSaga.class, sagaRepository, parameterResolverFactory);

        invoiceSubscribingEventProcessor = new SubscribingEventProcessor(simpleName + "Processor", sagaManager, springAMQPMessageSource);
        invoiceSubscribingEventProcessor.registerInterceptor(new TransactionManagingInterceptor<>(transactionManager));
        invoiceSubscribingEventProcessor.start();
        System.out.println("invoiceSubscribingEventProcessor STARTED");
    }

    @PreDestroy
    public void destroy() {
        shipmentSubscribingEventProcessor.shutDown();
        invoiceSubscribingEventProcessor.shutDown();
    }

    //spring cloud settings - distributed command bus
    @Bean
    public CommandRouter springCloudCommandRouter(DiscoveryClient discoveryClient) {
        return new SpringCloudCommandRouter(discoveryClient, new AnnotationRoutingStrategy());
    }

    @Bean
    public RestOperations restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CommandBusConnector springHttpCommandBusConnector(@Qualifier("localSegment") CommandBus localSegment,
                                                             RestOperations restOperations,
                                                             Serializer serializer) {
        return new SpringHttpCommandBusConnector(localSegment, restOperations, serializer);
    }

    @Primary // to make sure this CommandBus implementation is used for autowiring
    @Bean
    public DistributedCommandBus springCloudDistributedCommandBus(CommandRouter commandRouter,
                                                                  CommandBusConnector commandBusConnector) {
        return new DistributedCommandBus(commandRouter, commandBusConnector);
    }

    @Bean(destroyMethod = "shutdown")
    @Qualifier("localSegment")
    public CommandBus localSegment(TransactionManager transactionManager) {
        AsynchronousCommandBus asynchronousCommandBus = new AsynchronousCommandBus();
        asynchronousCommandBus.registerDispatchInterceptor(new BeanValidationInterceptor<>());
        asynchronousCommandBus.registerHandlerInterceptor(new TransactionManagingInterceptor<>(transactionManager));

        return asynchronousCommandBus;
    }

}
