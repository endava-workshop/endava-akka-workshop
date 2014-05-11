package com.akka.ws.neo4j.entity;

public class Relation {

	private String sourceName;
	private String destName;
	private String relationType;

	public Relation(String sourceName, String destName, String relationType) {
		this.sourceName = sourceName;
		this.destName = destName;
		this.relationType = relationType;
	}

	/**
	 * @return the sourceName
	 */
	public String getSourceName() {
		return sourceName;
	}

	/**
	 * @return the destName
	 */
	public String getDestName() {
		return destName;
	}

	/**
	 * @return the relationType
	 */
	public String getRelationType() {
		return relationType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Relation) {
			Relation rel = (Relation) obj;
			return this.destName.equals(rel.getDestName()) && this.sourceName.equals(rel.getSourceName())
					&& this.relationType.equals(rel.getRelationType());
		}
		return false;
	}

	@Override
	public String toString() {
		return "Relation{" + "sourceName='" + sourceName + ", destName=" + destName + ", relationType=" + relationType + '}';
	}

}
