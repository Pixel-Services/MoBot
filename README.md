<div align='center'>

<h1>MoBot</h1>

![image](https://github.com/user-attachments/assets/ac8ec29f-fb02-45c3-aba3-0bf985a935fc)
<h4> </span> <a href="https://mobot.siea.dev/mudular"> Modular Discord Bot </a> <span> · </span> <a href="https://github.com/orgs/VitacraftOrg/MoBot/issues"> Report Bug </a> <span> · </span> <a href="https://github.com/orgs/VitacraftOrg/MoBot/issues"> Request Feature </a> </h4>
<br>
</div>

## Overview
MoBot is a modular bot framework for Discord. It allows developers to create, extend, and manage custom modules that hook into the bot to add rich, interactive features.

---

<details>
<summary><strong>📋 Table of Contents</strong></summary>

- [Installation](#installation)
- [Creating a Module](#creating-a-module)

</details>

---

## Installation
To set up MoBot, download the latest release from the releases page and run it.
```bash
java -jar MoBot.jar
```
On the first run, MoBot will create a `bot.yml` file in the same directory. 
This file contains all the necessary configurations for the bot. 
Inside the `bot.yml` file, you will have to set the `token`. 

For more information on Discord Bot Tokens check [here](https://discord.com/developers/docs/topics/oauth2#bot-application-oauth2-url-generator).

Additionally to the `bot.yml` file, MoBot will create a `modules` folder. 
This folder is where you will place your modules.

Enjoy.

## Creating a Module
To create a module for MoBot, follow these steps:

1. **Create a Java Project** (Maven/Gradle):
- **Maven:**
  Add the MoBot dependency in `pom.xml`:

```xml
<repository>
  <id>pixel-services-releases</id>
  <name>Pixel Services</name>
  <url>https://maven.pixel-services.com/releases</url>
</repository>

<dependency> 
    <groupId>com.pixelservices</groupId>  
    <artifactId>mobot-api</artifactId>
    <version>VERSION</version> <!-- Replace VERSION -->
</dependency>
```

- **Gradle:**
  Add the dependency in `build.gradle`:

```gradle
maven {
    name "pixelServicesReleases"
    url "https://maven.pixel-services.com/releases"
}

dependencies {
  implementation "com.pixelservices:mobot-api:VERSION"  // Replace VERSION
}
```

_Find the latest version [here](https://maven.pixel-services.com/#/releases/com/pixelservices/MoBot)._

2. **Create Main Class**: Extend the `MbModule` class and override the `onEnable` and `onDisable` methods:

```java
public class WelcomeModule extends MbModule {
    @Override
    public void onEnable() {
        //Do something
    }

    @Override
    public void onDisable() {
        //Do something
    }
}
```

3. **Create `module.yml`**: Add a `module.yml` file in the `resources` folder and define the module details:

```yaml
name: WelcomeModule
version: 1.0.0
main: com.example.WelcomeModule
authors: ["author1", "author2"]
license: "MIT"
description: "A welcome module for MoBot"
dependencies: []
```
