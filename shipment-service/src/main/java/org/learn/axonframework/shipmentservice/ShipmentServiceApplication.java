package org.learn.axonframework.shipmentservice;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.distributed.AnnotationRoutingStrategy;
import org.axonframework.commandhandling.distributed.CommandBusConnector;
import org.axonframework.commandhandling.distributed.CommandRouter;
import org.axonframework.commandhandling.distributed.DistributedCommandBus;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.serialization.Serializer;
import org.axonframework.springcloud.commandhandling.SpringCloudCommandRouter;
import org.axonframework.springcloud.commandhandling.SpringHttpCommandBusConnector;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestOperations;

@EnableDiscoveryClient
@SpringBootApplication
public class ShipmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShipmentServiceApplication.class, args);
	}

	@Bean
	public Exchange orderExchange() {
		return ExchangeBuilder.fanoutExchange("OrderEvents").durable(true).build();
	}

	@Bean
	public Queue orderQueue() {
		return QueueBuilder.durable("OrderEvents").build();
	}

	@Bean
	public Binding orderBinding() {
		return BindingBuilder.bind(orderQueue()).to(orderExchange()).with("*").noargs();
	}

	@Autowired
	public void configure(AmqpAdmin admin) {
		admin.declareExchange(orderExchange());
		admin.declareQueue(orderQueue());
		admin.declareBinding(orderBinding());
	}

	//spring cloud settings - distributed command bus
	@Bean
	public CommandRouter springCloudCommandRouter(DiscoveryClient discoveryClient) {
		return new SpringCloudCommandRouter(discoveryClient, new AnnotationRoutingStrategy());
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
}
