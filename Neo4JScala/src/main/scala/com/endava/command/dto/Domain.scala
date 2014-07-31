package com.endava.command.dto

case class Domain (val name:String, val coolDownPeriod:Int, val crawledAt:String, val status:String)
case class Link (val domain:String, val url:String, val status:String)
case class Relation (val sourceName:String, val destName:String, val relationType:String)
case class DomainLink(val domain:Domain, val link:Link)