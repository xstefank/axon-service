package org.learn.axonframework.queryservice;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.serialization.Serializer;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QueryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueryServiceApplication.class, args);
	}

	@Bean
	@Qualifier("queryOrderEvents")
	public SpringAMQPMessageSource queryOrderEvents(Serializer serializer) {
		return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

			@RabbitListener(queues = "QueryOrderQueue")
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				super.onMessage(message, channel);
			}
		};
	}

	@Bean
	@Qualifier("queryShipmentEvents")
	public SpringAMQPMessageSource queryShipmentEvents(Serializer serializer) {
		return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

			@RabbitListener(queues = "QueryShipmentQueue")
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				super.onMessage(message, channel);
			}
		};
	}

	@Bean
	@Qualifier("queryInvoiceEvents")
	public SpringAMQPMessageSource queryInvoiceEvents(Serializer serializer) {
		return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

			@RabbitListener(queues = "QueryInvoiceQueue")
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				super.onMessage(message, channel);
			}
		};
	}
}
