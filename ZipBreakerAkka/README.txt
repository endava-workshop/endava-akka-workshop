The ZipBreakerAkka module has the goal of attacking password-protected zip files by brute force.

To deploy and run the module, please follow these steps:

1) Compile and package the application as a jar file. Put the resulted jar in the deploy folder
of the Akka microkernel installation. Copy the application.conf file to the config folder of the
Akka microkernel installation. If the microkernel runs on the same physical machine as the local
application, ensure you use a different port number in the remote configuration than the local.

2) Start the Akka microkernel on one or several computers (you can also have several microkernel 
installations on the same physical computer if you provide a different port number for each).
To start it, you need to run the following command from the bin folder of the installation:
akka.bat akka.ws.pass.breaker.RemoteApplication

3) Edit the remoteAddresses.json file in src/main/resources. Provide in this file the details for
connecting to each of the remote kernels that are running with the application deployed.
Ensure the file contains only the valid remote addresses of the successfully running machines.

3) Start the local application. An example of code to do this is presented in 
akka.ws.pass.breaker.LocalApplication class.

4) For each zip file to be attacked, your code should send a BreakArchiveMessage message to the
ZipPasswordBreaker actor. An example of code to do this is presented in 
akka.ws.pass.breaker.LocalApplication class.


Additional notes:

* some configuration information (such as chunk size or password file name - for example) are 
currently mentioned in static final variables inside the actor classes. This situation will 
change in the future and they will go into properties files. However, if you want to configure 
anything at this moment, take a look in the code of the actors.