package br.com.printpricing.notifications

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class LogSmsSender : SmsSender {
    private val logger = LoggerFactory.getLogger(LogSmsSender::class.java)

    override fun send(phone: String, message: String) {
        logger.info("SMS para telefone {}: {}", phone, message)
    }
}
