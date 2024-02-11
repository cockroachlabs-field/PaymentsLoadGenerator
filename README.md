# Payments Load Generator

## Purpose

This service is meant to generate input transactions for a card payments process.  It generates random amounts, names, card numbers, etc.  The number of concurrent threads and the total number of requests generated are configurable properties.

It is meant to be run in conjunction with the Payment Gateway mockup service in this same repo.

Multiple instances of this application can be run simultaneously.

## Installation

### Prerequisites

- **Java SDK version 17** installed, on the server where this code is to be run (the project was written with Java 17 in mind, although it will probably work fine with versions higher than 17)<br/><br/>You can check your java version by running `java -version`<br/><br/>
- **Maven**, on the server where you have the code<br/><br/>You can verify Maven is installed by running `mvn -version`<br/><br/>

To install and run this code, you need to do the following

1. Pull the code locally from the github repository.<br/><br/>

2. Set the JAVA_HOME environment variable pointing to your Java 17 SDK; for example

       export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home

3. Run `mvn package` from the project directory to package this executable as a JAR file<br/><br/>After this command finishes, you should see a jar file in the target directory:

       $ ls target/*.jar
       target/PaymentsLoadGenerator-0.0.1-SNAPSHOT.jar

4. Configure the settings used by the service.  The settings are configured in the code in the application.properties file.  You can override the properties that were packaged up with the JAR file by specifying the location of an external properties file:

       java -jar <jar file> --spring.config.location=generator.properties

   The file should look like this (see: src/main/resources/application.properties):

       payment_demo.load_gen.number_of_threads=20
       payment_demo.load_gen.number_of_transactions=100
       payment_demo.gateway_url=http://localhost:8081/transactions

5. Run the service by executing a command like the following.  It is a good idea to specify the max heap memory for this process.  In the example below, we're specifying a max heap size of 2GB with `-Xmx2g`.

       java -Xmx2g -jar target/PaymentsLoadGenerator-0.0.1-SNAPSHOT.jar --spring.config.location=generator.properties