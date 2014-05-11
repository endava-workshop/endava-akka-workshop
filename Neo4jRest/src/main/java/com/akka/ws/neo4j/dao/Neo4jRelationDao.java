package com.akka.ws.neo4j.dao;

import java.util.Set;

import com.akka.ws.neo4j.entity.Relation;

public interface Neo4jRelationDao {

	public void addRelationsInBatch(Set<Relation> relationSet);

	public boolean addRelation(Relation relation);

	public boolean relationExists(Relation relation);

	public long countAllRelations(String relationType);
	
	public void cleanDatabase();

}
