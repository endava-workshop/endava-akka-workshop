package com.endava.command

import org.neo4j.graphdb.GraphDatabaseService
import org.neo4j.graphdb.Node
import org.neo4j.graphdb.Transaction
import org.neo4j.graphdb.factory.GraphDatabaseFactory
import com.endava.command.dto.Domain
import com.endava.command.dto.DomainLink
import org.neo4j.graphdb.factory.GraphDatabaseSettings
import org.neo4j.graphdb.DynamicLabel
import org.neo4j.cypher.ExecutionEngine
import org.neo4j.unsafe.batchinsert.BatchInserter
import org.neo4j.unsafe.batchinsert.BatchInserterIndexProvider
import org.neo4j.unsafe.batchinsert.BatchInserterIndex
import org.neo4j.index.lucene.unsafe.batchinsert.LuceneBatchInserterIndexProvider
import org.neo4j.unsafe.batchinsert.BatchInserters
import org.neo4j.helpers.collection.MapUtil
import scala.collection.JavaConversions
import scala.collection.mutable.HashMap

import collection.JavaConversions._

object CommandService {

  val storeDir: String = "D:/database/Neo4J/testdb"

  var graphDb: GraphDatabaseService = null

  def initGraphDb() {
    if (graphDb != null)
      return

    //    graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(storeDir)

    graphDb = new GraphDatabaseFactory()
      .newEmbeddedDatabaseBuilder(storeDir)
      .setConfig(GraphDatabaseSettings.nodestore_mapped_memory_size, "10M")
      .setConfig(GraphDatabaseSettings.string_block_size, "60")
      .setConfig(GraphDatabaseSettings.array_block_size, "300")
      .newGraphDatabase();

    //    registerShutdownHook(graphDb);

    val tx = startTransaction(graphDb)

    graphDb.schema().constraintFor(DynamicLabel.label("Domain"))
      .assertPropertyIsUnique("dname")
      .create();

    completeTransaction(tx)

  }

  def addDomainLinks(domainLinkList: List[DomainLink]): Boolean = {
    val inserter: BatchInserter = BatchInserters.inserter(storeDir);
    println("got inserter: " + System.currentTimeMillis())
    val indexProvider: BatchInserterIndexProvider =
      new LuceneBatchInserterIndexProvider(inserter);
    println("got index provider: " + System.currentTimeMillis())
    val domainIndex: BatchInserterIndex =
      indexProvider.nodeIndex("domainIndex", MapUtil.stringMap("type", "exact"));

    domainIndex.setCacheCapacity("dname", 100000);

    println("got domain index: " + System.currentTimeMillis())

    var added = 0;
    for (
      domainLink <- domainLinkList if domainIndex.get("dname", domainLink.domain.name).size() == 0
    ) {
      val properties: Map[String, Any] = Map("dname" -> domainLink.domain.name,
        "dstatus" -> domainLink.domain.status,
        "coolDownPeriod" -> domainLink.domain.coolDownPeriod,
        "crawledAt" -> domainLink.domain.crawledAt);

      val javaMap = mapAsJavaMap(properties).asInstanceOf[java.util.Map[java.lang.String, java.lang.Object]]

      val node: Long = inserter.createNode(javaMap);
      domainIndex.add(node, javaMap);
      added = added.+(1)
    }
    println("prepare flush for " + added + " : " + System.currentTimeMillis())
    //make the changes visible for reading, use this sparsely, requires IO!
    domainIndex.flush();

    println("got flush: " + System.currentTimeMillis())
    // Make sure to shut down the index provider as well
    indexProvider.shutdown();
    inserter.shutdown();
    println("got shutdown: " + System.currentTimeMillis())

    true
  }

  def addDomain(domain: Domain): Boolean = {
    println(domain name)

    var engine: ExecutionEngine = new ExecutionEngine(graphDb);
    val tx = startTransaction(graphDb)
    println("tx started")

    val query = "CREATE (n:Domain { dname : '" + domain.name + "', dstatus : '" + domain.status + "', coolDownPeriod : " + domain.coolDownPeriod + " , crawledAt : '" + domain.crawledAt + "'  })"

    println(query)
    engine.execute(query)

    println("tx complete")
    completeTransaction(tx)

    println("finished")
    true
  }

  def registerShutdownHook(graphDb: GraphDatabaseService) {
    graphDb.shutdown();
  }

  def startTransaction(graphDb: GraphDatabaseService): Transaction = {
    graphDb.beginTx()
  }

  def completeTransaction(tx: Transaction) = {
    tx.success()
  }

}