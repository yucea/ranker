package kr.co.esjee.ranker.mq;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import kr.co.esjee.ranker.config.RabbitMQConfig;
import kr.co.esjee.ranker.util.CalendarUtil;
import kr.co.esjee.ranker.util.JobUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestRabbitMQ {

	String ROUTINGKEY = "#";

	String host = "192.168.0.142";
	String username = "admin";
	String password = "1234";
	int port = 5672;
	String queueName = "ranker-scheduler";

	@Test
	public void testRunner() {
		try {
			RabbitTemplate rabbitTemplate = rabbitTemplate(queueName);

			JSONObject json = new JSONObject();
			json.put("name", queueName);
			json.put("time", CalendarUtil.getCurrentDateTime());

			 rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, JobUtil.getRoutingKey(RabbitMQConfig.QUEUE_NAME), json.toString());

			log.info("{}", queueName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
		connectionFactory.setUsername(username);
		connectionFactory.setPassword(password);
		connectionFactory.setPort(port);
		return connectionFactory;
	}

	public RabbitTemplate rabbitTemplate(String queueName) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory());
		template.setRoutingKey(JobUtil.getRoutingKey(RabbitMQConfig.QUEUE_NAME));
		template.setDefaultReceiveQueue(queueName);
		return template;
	}

}
