package org.learn.axonframework.shipmentservice;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.messaging.interceptors.TransactionManagingInterceptor;
import org.axonframework.serialization.Serializer;
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

	@Bean
	public CommandBus commandBus(TransactionManager transactionManager) {
		SimpleCommandBus simpleCommandBus = new SimpleCommandBus();
		simpleCommandBus.registerDispatchInterceptor(new TransactionDispatchInterceptor<>(transactionManager));
		return simpleCommandBus;
	}

	@Autowired
	public void configure(AmqpAdmin admin) {
		admin.declareExchange(orderExchange());
		admin.declareQueue(orderQueue());
		admin.declareBinding(orderBinding());
	}

	@Bean
	public SpringAMQPMessageSource shipmentEvents(Serializer serializer) {
		return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

			@RabbitListener(queues = "ShipmentEvents")
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				LoggerFactory.getLogger("AMQP").info("received message " + message);
				super.onMessage(message, channel);
			}
		};
	}
}
