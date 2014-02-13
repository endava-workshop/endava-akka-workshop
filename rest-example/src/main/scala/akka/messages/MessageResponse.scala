package akka.messages

abstract class Neo4JMessage

case class Seed() extends Neo4JMessage
case class RetrieveLink(link: String) extends Neo4JMessage