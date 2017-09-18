package org.learn.axonframework.orderservice;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.SubscribingEventProcessor;
import org.axonframework.eventhandling.saga.AnnotatedSagaManager;
import org.axonframework.eventhandling.saga.ResourceInjector;
import org.axonframework.eventhandling.saga.repository.AnnotatedSagaRepository;
import org.axonframework.eventhandling.saga.repository.SagaStore;
import org.axonframework.messaging.annotation.ParameterResolverFactory;
import org.axonframework.messaging.interceptors.TransactionManagingInterceptor;
import org.axonframework.serialization.Serializer;
import org.learn.axonframework.orderservice.saga.OrderManagementSaga;
import org.slf4j.LoggerFactory;
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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.annotation.PreDestroy;

//@EnableDiscoveryClient
@SpringBootApplication
public class OrderServiceApplication {

	private SubscribingEventProcessor subscribingEventProcessor;

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Bean
	public Exchange shipmentExchange() {
		return ExchangeBuilder.fanoutExchange("ShipmentEvents").durable(true).build();
	}

	@Bean
	public Queue shipmentQueue() {
		return QueueBuilder.durable("ShipmentEvents").build();
	}

	@Bean
	public Binding shipmentBinding() {
		return BindingBuilder.bind(shipmentQueue()).to(shipmentExchange()).with("*").noargs();
	}

	@Autowired
	public void configure(AmqpAdmin admin) {
		admin.declareExchange(shipmentExchange());
		admin.declareQueue(shipmentQueue());
		admin.declareBinding(shipmentBinding());
	}

	@Bean
	public SpringAMQPMessageSource orderEvents(Serializer serializer) {
		return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

			@RabbitListener(queues = "OrderEvents")
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
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
}
