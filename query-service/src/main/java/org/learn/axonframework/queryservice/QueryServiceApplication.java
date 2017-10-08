package org.learn.axonframework.queryservice;

import com.rabbitmq.client.Channel;
import org.axonframework.amqp.eventhandling.DefaultAMQPMessageConverter;
import org.axonframework.amqp.eventhandling.spring.SpringAMQPMessageSource;
import org.axonframework.serialization.Serializer;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class QueryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(QueryServiceApplication.class, args);
	}

	@Bean
	public SpringAMQPMessageSource queryEvents(Serializer serializer) {
		return new SpringAMQPMessageSource(new DefaultAMQPMessageConverter(serializer)) {

			@RabbitListener(queues = "QueryQueue")
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				//necessary because message is not delivered to saga otherwise
				LoggerFactory.getLogger(QueryServiceApplication.class).info("message - " + message.toString());
				Thread.sleep(1000);
				super.onMessage(message, channel);
			}
		};
	}
}
