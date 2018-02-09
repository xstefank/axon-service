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
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.CommandGatewayFactory;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.commandhandling.gateway.IntervalRetryScheduler;
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
import org.axonframework.spring.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.springcloud.commandhandling.SpringCloudCommandRouter;
import org.axonframework.springcloud.commandhandling.SpringHttpCommandBusConnector;
import org.learn.axonframework.orderservice.saga.OrderManagementSaga;
import org.learn.axonframework.orderservice.swagger.SwaggerConfiguration;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@EnableDiscoveryClient
@SpringBootApplication
@Import(SwaggerConfiguration.class)
public class OrderServiceApplication {

	private SubscribingEventProcessor subscribingEventProcessor;

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Bean
	public Exchange orderExchange() {
		return ExchangeBuilder.fanoutExchange("OrderEvents").durable(true).build();
	}

	@Bean
	public Queue queryQueue() {
		return QueueBuilder.durable("QueryQueue").build();
	}

	@Bean
	public Binding queryBinding() {
		return BindingBuilder.bind(queryQueue()).to(orderExchange()).with("*").noargs();
	}


	@Bean
	public SpringAMQPMessageSource orderEvents(Serializer serializer) {
		return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

			@RabbitListener(queues = "OrderQueue")
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				//necessary because message is not delivered to saga otherwise
				Thread.sleep(1000);
				super.onMessage(message, channel);
			}
		};
	}

	//the manual registration of saga event subscription because of processing events from AMQP queue
	@Autowired
	@SuppressWarnings("unchecked")
	public void createSaga(SagaStore sagaStore, SpringAMQPMessageSource springAMQPMessageSource, ResourceInjector resourceInjector, ParameterResolverFactory parameterResolverFactory, TransactionManager transactionManager)
	{
		String simpleName = OrderManagementSaga.class.getSimpleName();

		AnnotatedSagaRepository sagaRepository = new AnnotatedSagaRepository<>(OrderManagementSaga.class, sagaStore, resourceInjector, parameterResolverFactory);
		AnnotatedSagaManager<OrderManagementSaga> sagaManager = new AnnotatedSagaManager<>(OrderManagementSaga.class, sagaRepository, parameterResolverFactory);

		this.subscribingEventProcessor = new SubscribingEventProcessor(simpleName + "Processor", sagaManager, springAMQPMessageSource);
		this.subscribingEventProcessor.registerInterceptor(new TransactionManagingInterceptor<>(transactionManager));
		this.subscribingEventProcessor.start();
		System.out.println("subscribingEventProcessor STARTED");
	}

	@PreDestroy
	public void destroy()
	{
		this.subscribingEventProcessor.shutDown();
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
