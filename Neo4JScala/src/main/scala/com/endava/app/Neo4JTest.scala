package com.endava.app

import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.RelationshipType
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import org.neo4j.graphdb.DynamicRelationshipType

object Neo4JTest extends App {

  println("start")

  val neo: GraphDatabaseService = new GraphDatabaseFactory().newEmbeddedDatabase("D:/Database/Neo4J/db")

  var tx: Transaction = neo.beginTx()

  var first: Node = null
  var second: Node = null

  try {
    first = neo.createNode()
    first.setProperty("name", "first")

    second = neo.createNode()
    second.setProperty("name", "second")

    first.createRelationshipTo(second, DynamicRelationshipType.withName("isRelatedTo"))

    tx.success()
  } finally {
    println("finished transaction 1")
  }

}