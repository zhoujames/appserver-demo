# Lightweight Web Server

## Description
This is a lightweight web server that can be embedded into both J2EE and Osgi container.
Current version only support GET method which could be used as a simple file server.
CQ5 version could be deployed as a bundle in CQ5 container. This version allows you to download any JCR text/binary file without intercepting by CQ5 platform.
The main purpose of OSGI version is to set up a simple API mock server which can feed mock data in the authoring mode. 
 
### Features
* Thread pooling 
* Support Http 1.1
* Json based config files
* Support Virtual Host
* Support Multiple Context and Alias
* OSGI compatible, can be directly deployed into AEM

## Build and Run

### Standalone version
Run `mvn clean install`
Run `java -Dconf.path=YOUR_CONF_PATH -jar dist/appserver.jar`

### Deploy to CQ5
Run `mvn -Dcq.host=XXX -Dcq.passowrd=XXX -Dcq.user=XXX -PdeployAemPackage clean install`


