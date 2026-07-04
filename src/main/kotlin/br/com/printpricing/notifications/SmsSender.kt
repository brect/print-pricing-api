package br.com.printpricing.notifications

interface SmsSender {
    fun send(phone: String, message: String)
}
