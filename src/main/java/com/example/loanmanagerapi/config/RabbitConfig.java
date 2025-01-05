package com.example.loanmanagerapi.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class sets up our RabbitMQ configuration.
 */
@Configuration
public class RabbitConfig {

    /**
     * Creates a non-durable queue named "productQueue."
     */
    @Bean
    public Queue productQueue() {
        return new Queue("productQueue", false);
    }

    /**
     * Uses Jackson to convert messages to and from JSON.
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Configures a RabbitTemplate to send and receive messages as JSON.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    /**
     * Creates a TopicExchange named "Message-Exchange."
     */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange("Message-Exchange");
    }
}
