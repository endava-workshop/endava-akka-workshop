Simple Spring Data Neo4j example
For running this project, please follow the following steps:
	- in spring-appContext.xml file choose what graphDatabaseService you want to use
		- EmbeddedGraphDatabase - set the path where you want to save on disk the created database, no other configuration is needed
		- SpringRestGraphDatabase - before executing the project, start your installed neo4j server
									- with this option the transactions will be ignored, cannot keep a transaction over the rest calls
	- run the test class TestUrlService.java which has several methods for testing divert use cases

To get access to the REST endpoint:
* start the app:
```
mvn exec:java
```
* play around with the API
** GET    /purge - remove all domains
** GET    /domains - list all domains
** GET    /domain/<address> - list URLs for an address
** POST   /domainURL/<domainName>/<domainURL>  - add domain a domain URL
** DELETE /domainURL/<domainURL> - add remove a domain URL
** POST   /simpleURL/<domainName>/<simpleURL>/<name> to add a simple URL
** DELETE /simpleURL/<simpleURL> to remove a simple URL
** GET    /stop to stop the actor system (i.e. stop the app)



Good luck !!!