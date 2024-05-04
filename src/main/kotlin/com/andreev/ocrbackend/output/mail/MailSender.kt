package com.andreev.ocrbackend.output.mail

import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class MailSender(
    private val mailSender: JavaMailSender,
    @Value("\${spring.mail.username}")
    private val username: String
) {

    fun sendTo(emailTo: String, subject: String, message: String) {
        val mailMessage = SimpleMailMessage()

        mailMessage.from = username
        mailMessage.setTo(emailTo)
        mailMessage.subject = subject
        mailMessage.text = message

        mailSender.send(mailMessage)
    }
}