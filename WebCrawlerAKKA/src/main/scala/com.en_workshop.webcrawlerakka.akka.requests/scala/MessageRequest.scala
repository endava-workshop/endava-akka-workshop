package com.en_workshop.webcrawlerakka.akka.requests.scala

import com.en_workshop.webcrawlerakka.entities.{WebUrl, WebDomain}

abstract class MessageRequest(id: Long)

case class CrawleDomainRequest(webDomain: WebDomain) extends MessageRequest(System.currentTimeMillis())
case class NextLinkRequest(id: Int, webDomain: WebDomain) extends MessageRequest(System.currentTimeMillis())
case class DownloadUrlRequest(id: Int, webDomain: WebDomain) extends MessageRequest(System.currentTimeMillis())
case class ProcessContentRequest(id: Int, source: WebUrl, content: String) extends MessageRequest(System.currentTimeMillis())
case class StartDomainMasterRequest() extends MessageRequest(System.currentTimeMillis())
case class StartMasterRequest() extends MessageRequest(System.currentTimeMillis())
case class StartProcessingMasterRequest() extends MessageRequest(System.currentTimeMillis())
case class StopDomainMasterRequest() extends MessageRequest(System.currentTimeMillis())
case class StopMasterRequest() extends MessageRequest(System.currentTimeMillis())
case class StopProcessingMasterRequest() extends MessageRequest(System.currentTimeMillis())
