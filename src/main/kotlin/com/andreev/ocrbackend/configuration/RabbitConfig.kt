package com.andreev.ocrbackend.configuration

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
class RabbitConfig {

    @Bean
    fun transactionManager(connectionFactory: CachingConnectionFactory): PlatformTransactionManager {
        return RabbitTransactionManager(connectionFactory)
    }
}