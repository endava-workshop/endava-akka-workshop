The ZipBreakerAkka module has the goal of attacking password-protected zip files by brute force.

To deploy and run the module, please follow these steps:

1) Compile and package the application as a jar file. Put the resulted jar in the deploy folder
of the Akka microkernel installation. Copy the application.conf file to the config folder of the
Akka microkernel installation. If the microkernel runs on the same physical machine as the local
application, ensure you use a different port number in the remote configuration than the local.
Note: You can deploy the ZipBreakerAkka as a single jar file (all dependencies embeded), by
issuing the following command line from the root of the project:
mvn clean package assembly:single
Note2: Ensure you have correctly set the IP address of the localhost in each application.conf both
in the remote and in the local application. Avoid using "localhost" or "127.0.0.1".

2) Start the Akka microkernel on one or several computers (you can also have several microkernel 
installations on the same physical computer if you provide a different port number for each).
To start it, you need to run the following command from the bin folder of the installation:
akka.bat akka.ws.pass.breaker.RemoteApplication

3) Edit the remoteAddresses.json file in src/main/resources. Provide in this file the details for
connecting to each of the remote kernels that are running with the application deployed.
Ensure the file contains only the valid remote addresses of the successfully running machines.

3) Start the local application. An example of code to do this is presented in 
akka.ws.pass.breaker.LocalApplication class.

4) For each zip file to be attacked, your code should send a StartProcessMessage message to the
ZipPasswordBreaker actor. An example of code to do this is presented in 
akka.ws.pass.breaker.LocalApplication class.
Please note that the sample LocalApplication is targeting one of the "experiment<index>.zip"
from src/main/resources. These files are provided as resources for experimenting. The first one
is encrypted with the password no. 1.000.001 from the common_passwords.txt (which is "buildasite"), 
experiment2.zip is protected with the password no. 2.000.001 from the same file and so on. Thus, 
breaking one of these archives by brute-force means trying a certain number of passwords (assuming 
the passwords are read from this file in sequence).


Configuration files:

* application.conf -> contains the configuration for Akka framework; there should be 
one application.conf in src/main/resources of the local application and one in each conf directory
of each akka microkernel running on remote machines; if remote kernels are ran on the same
physical machine, please ensure you are using different ports than for the local.
* remoteAddresses.json -> contains the information about the remote kernels in order for the
local application to know how to connect to those kernels; for each remote machine, it is also
compulsory to specify how many workers should run for each attacked zip archive; this should be
a positive integer greater than zero; the local application will equaly distribute the work to
each remote worker - which means you can increase the load on one physical machine by forcing
more workers to be deployed on it, or limit the work to be done on one machine by allocating fewer
workers on it.
* settings.properties -> contains the configuration for the resources and configurable information
used by the actors; please ensure you have configured these values before running; also ensure
the files and folders denoted by given paths exist.