<a href="http://predixdev.github.io/predix-rest-client/javadocs/index.html" target="_blank" >
	<img height="50px" width="100px" src="images/javadoc.png" alt="view javadoc"></a>
&nbsp;
<a href="http://predixdev.github.io/predix-rest-client" target="_blank">
	<img height="50px" width="100px" src="images/pages.jpg" alt="view github pages">
</a>
Predix Rest Client
==============



Welcome to Predix Rest Client, a [Microcomponent](https://github.com/PredixDev/predix-rmd-ref-app/blob/master/docs/microcomponents.md) Utility.

The predix-rest-client project provides standard GET, PUT, POST, DELETE with helpers to manage Predix OAuth Security in the cloud.  See the property files and [IOauthRestConfig.java](https://github.com/PredixDev/predix-boot/blob/master/predix-rest-client/src/main/java/com/ge/predix/solsvc/restclient/config/IOauthRestConfig.java) which allow a microservice to connect to Predix UAA (User Authentication and Authorization) servers in the cloud. All the reference app microservices use this utility to make Rest calls in the cloud.

1. Most of the time, you will make a dependency to predix-rest-client by adding this to your pom.xml
  
  ```xml
 	<dependency>
		<groupId>com.ge.predix.solsvc</groupId>
		<artifactId>predix-rest-client</artifactId>
		<version>${predix-rest-client.version}</version>
	</dependency>
  ```
  
1. If you started with a [Predix Microservice Template](https://github.com/predixdev/predix-microservice-templates) you will have a property in config/application.properties which spins up certain beans marked with the Local Profile.  Otherwise, add this property to your project.
  ```
  spring.profiles.active=local
  ```
  
1. You also will want to check that Autowiring of these package is occurring by adding this file to your spring context 
  ```xml
  	"classpath:META-INF/spring/predix-rest-client-scan-context.xml" 
  	
  	which contains
	<context:component-scan
            base-package="
		com.ge.predix.solsvc.restclient.config
		com.ge.predix.solsvc.restclient.impl
        " />

  ```
  
1. Or to view the source code you can download the project  
  ```sh
  $ git clone https://github.com/PredixDev/predix-rest-client.git  
  
  $ cd predix-rest-client
  
  $ mvn clean package  
  
    note: mvn clean install may run integration tests against services you may not have set up yet
  ```
  
##Dependencies
|Required - latest unless specified | Note |
| ------------- | :----- |
| Java 8 | |
| Git | |
| Maven | https://artifactory.predix.io/artifactory/PREDIX-EXT |
| CloudFoundry ClI 6.12.2 |  https://github.com/cloudfoundry/cli/tree/v6.12.2#downloads.  There is bug on this page, so you have to manually get the URL and the add "&version=6.12.2".  For example for Windows32 it would look like this...https://cli.run.pivotal.io/stable?release=windows32&source=github&version=6.12.2 |

[![Analytics](https://ga-beacon.appspot.com/UA-82773213-1/predix-rest-client/readme?pixel)](https://github.com/PredixDev)
