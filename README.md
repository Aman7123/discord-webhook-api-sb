[![SOFT](https://github.com/Aman7123/discord-webhook-api-sb/actions/workflows/SOFT.yml/badge.svg)](https://github.com/Aman7123/discord-web3-api-sb/actions/workflows/SOFT.yml) [![RELEASE](https://github.com/Aman7123/discord-webhook-api-sb/actions/workflows/RELEASE.yml/badge.svg)](https://github.com/Aman7123/discord-webhook-api-sb/actions/workflows/RELEASE.yml)

### discord-webhook-api-sb
* Version: 1.0.8
* Most up to date implementation will be found in branch `develop`.
* Creator: Aaron Renner
* This API project was created in Spring-Boot

### Introduction
This RESTful API provides database resources. Allow for building and sending Discord messages from an integration natively.
Unlike with a typical Discord Webhook this API sends messages from an actual bot which you register and provide credentials
to as HTTP Headers, those headers are used to temporarily start a bot in memory using @Javacord. Because this microservice
uses an integration response information like Message ID is available which allows for creating reply messages which cannot be done in Discord's native webhooks.

### Documentation
* For endpoint examples see the OpenAPI Spec here: `https://app.swaggerhub.com/apis/bananaz-tech-2/discord-webhook-api/1.0.0`

### Getting Started
**Running Locally using IDE**
This project uses Spring profiles, and corresponding application properties .yaml files.
All values from the application properties can be overwritten by the environment!
* Use the following environment variables: 
   * ```spring.profiles.active=<env>```
   
The profiles active environment variable is for selecting active config values. This project has a dev and prod but credentials are hidden!

Note: IDE specific development
* Eclipse - When modifying this API in Eclipse the VM arguments added to the runtime configuration will be prefixed with `-D`.
  * Example: `-Dspring.profiles.active=dev`

**Running on the Command Line**
The command arguments in a terminal also follow the prefix `-D` rule.
```
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

**Additional Values**
Some important config values to look for are the `server.port` and `server.servlet.context-path` which 
are managed by Spring and allow modifying properties of the startup listeners

### Docker & Compose & Maven
This project includes a lightweight, portable maven executable that can be used in place of having maven installed.
You will still need Java installed.

When building this application for production I have included the Dockerfile that can allow for building, preparing
and executing all source code in the base directory. Using CI/CD this can all be automated and I will try to include
an example for using Github workflows.

### Contact

* Aaron Renner (admin@arenner.io)