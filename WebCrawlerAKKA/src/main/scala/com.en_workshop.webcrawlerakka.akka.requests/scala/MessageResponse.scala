package com.en_workshop.webcrawlerakka.akka.requests.scala

import com.en_workshop.webcrawlerakka.entities.Link

abstract class MessageResponse(messageRequest: MessageRequest, id: Long)

case class DownloadUrlResponse(downloadUrlRequest: DownloadUrlRequest) extends MessageResponse(downloadUrlRequest, System.currentTimeMillis())
case class NextLinkResponse(nextLinkRequest: NextLinkRequest, webUrl: Link) extends MessageResponse(nextLinkRequest, System.currentTimeMillis())