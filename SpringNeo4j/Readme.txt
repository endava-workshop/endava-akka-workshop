Simple Spring Data Neo4j example
For running this project, please follow the following steps:
	- in spring-appContext.xml file choose what graphDatabaseService you want to use
		- EmbeddedGraphDatabase - set the path where you want to save on disk the created database, no other configuration is needed
		- SpringRestGraphDatabase - before executing the project, start your installed neo4j server
									- with this option the transactions will be ignored, cannot keep a transaction over the rest calls
	- run the test class TestUrlService.java which has several methods for testing divert use cases
	
Good luck !!!