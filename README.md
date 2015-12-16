Predix Rest Client
==============

Welcome to Predix Boot, a Predix Backend Microservice Template.  

The predix-rest-client project provides standard GET, PUT, POST, DELETE with helpers to manage Predix OAuth Security in the cloud.  See the property files and [IOauthRestConfig.java](https://github.com/PredixDev/predix-rest-client/blob/master/src/main/java/com/ge/predix/solsvc/restclient/config/IOauthRestConfig.java) which allow a microservice to connect to Predix UAA (User Authentication and Authorization) servers in the cloud. All the reference app microservices use this utility to make Rest calls in the cloud.

##Package the rest client
Run mvn clean package to build and package the Rest Client

##Dependencies
|Required - latest unless specified | Note |
| ------------- | :----- |
| Java 8 | |
| GitHub Acct | logged in |
| Git | |
| Maven | https://artifactory.predix.io/artifactory/PREDIX-EXT |
| CloudFoundry ClI 6.12.2 |  https://github.com/cloudfoundry/cli/tree/v6.12.2#downloads.  There is bug on this page, so you have to manually get the URL and the add "&version=6.12.2".  For example for Windows32 it would look like this...https://cli.run.pivotal.io/stable?release=windows32&source=github&version=6.12.2 |
