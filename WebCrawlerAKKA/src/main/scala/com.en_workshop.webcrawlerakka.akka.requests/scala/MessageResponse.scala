package com.en_workshop.webcrawlerakka.akka.requests.scala

import com.en_workshop.webcrawlerakka.entities.WebUrl

abstract class MessageResponse(messageRequest: MessageRequest, id: Long)

case class DownloadUrlResponse(downloadUrlRequest: DownloadUrlRequest) extends MessageResponse(downloadUrlRequest, System.currentTimeMillis())
case class NextLinkResponse(nextLinkRequest: NextLinkRequest, webUrl: WebUrl) extends MessageResponse(nextLinkRequest, System.currentTimeMillis())