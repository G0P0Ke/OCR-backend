package com.andreev.ocrbackend.output.rabbit

import mu.KLogging
import org.springframework.amqp.AmqpException
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.ReturnedMessage
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class RabbitSender(
    private val rabbitTemplate: RabbitTemplate
) {

    companion object : KLogging()

    init {
        rabbitTemplate.setReturnsCallback { returnedMessage: ReturnedMessage ->
            logger.warn(
                "Message returned. Reply Code: {}, Reply Text: {}, Exchange: {}, Routing Key: {}",
                returnedMessage.replyCode, returnedMessage.replyText,
                returnedMessage.exchange, returnedMessage.routingKey
            )
        }
        rabbitTemplate.isChannelTransacted = true
    }

    fun send(
        exchange: String,
        message: Any,
        routingKey: String
    ) {
        return try {
            logger.info {
                "Publishing RabbitMQ message: $message on exchange=$exchange, routingKey=$routingKey"
            }
            rabbitTemplate.convertAndSend(exchange, routingKey, message)
        } catch (e: RuntimeException) {
            logger.error(e) {
                "Exception during publishing RabbitMQ message $message on exchange=$exchange, routingKey=$routingKey"
            }
        }
    }
}