package kr.co.esjee.ranker.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import kr.co.esjee.ranker.util.JobUtil;
import kr.co.esjee.ranker.webapp.AppConstant;

@Configuration
@EnableRabbit
public class RabbitMQConfig implements AppConstant {

	public static final String QUEUE_NAME = "ranker-schedule";
	public static final String EXCHANGE_NAME = QUEUE_NAME + "-exchange";

	@Value("${spring.rabbitmq.host}")
	private String host;
	@Value("${spring.rabbitmq.username}")
	private String username;
	@Value("${spring.rabbitmq.password}")
	private String password;
	@Value("${spring.rabbitmq.port}")
	private int port;

	@Bean
	public RabbitTemplate rabbitTemplate() {
		if (host == null)
			return null;

		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setRoutingKey(JobUtil.getRoutingKey(QUEUE_NAME));
		template.setDefaultReceiveQueue(QUEUE_NAME);
//		template.setMessageConverter(jsonMessageConverter());
		return template;
	}

//	@Bean
//	public Queue queue() {
//		return new Queue(QUEUE_NAME, true);
//	}
//
//	@Bean
//	public TopicExchange exchange() {
//		return new TopicExchange(EXCHANGE_NAME);
//	}
//
//	@Bean
//	public Binding binding(Queue queue, TopicExchange exchange) {
//		return BindingBuilder.bind(queue).to(exchange).with(JobUtil.getRoutingKey(QUEUE_NAME));
//	}

//	@Bean
//	public Jackson2JsonMessageConverter jsonMessageConverter() {
//		return new Jackson2JsonMessageConverter();
//	}

	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory factory = new CachingConnectionFactory();
		factory.setHost(host);
		factory.setUsername(username);
		factory.setPassword(password);
		factory.setPort(port);
		return factory;
	}

}
