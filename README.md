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

- [Setup Project](#setup-project)
- [Installation](#installation)
- [Creating a Module](#creating-a-module)
- [Module Configuration](#module-configuration)
- [Loading and Managing Modules](#loading-and-managing-modules)
- [License](#license)

</details>

---

## Setup Project

To create a module for MoBot, follow these steps:

1. **Create a Java Project** (Maven/Gradle):
  - **Maven:**
    Add the MoBot dependency in `pom.xml`:

    ```xml
    <dependency>
        <groupId>com.pixelservices</groupId>
        <artifactId>MoBot</artifactId>
        <version>VERSION</version> <!-- Replace VERSION -->
    </dependency>
    ```

  - **Gradle:**
    Add the dependency in `build.gradle`:

    ```gradle
    dependencies {
        implementation "com.pixelservices:MoBot:VERSION"  // Replace VERSION
    }
    ```

   Find the latest version [here](https://maven.pixel-services.com/#/releases/net/vitacraft/MoBot).

2. **Create Main Module Class**: Extend the `MBModule` class:

    ```java
    public class WelcomeModule extends MBModule {
        @Override
        public void onEnable() {
            getLogger().info("WelcomeModule enabled!");
        }

        @Override
        public void onDisable() {
            getLogger().info("WelcomeModule disabled!");
        }
    }
    ```

3. **Create Service File**: Create `resources/META-INF/services/net.vitacraft.api.MBModule`, and to this file add **one line** to it: the fully qualified name of your main module class, for example:

    ```text
    com.example.WelcomeModule
    ```

---

## Installation

1. **Install MoBot** by adding it as a dependency in your Maven or Gradle project (see the Setup Project section).

2. **Create `bot.yml`** file inside the `resources` folder to configure your bot:

    ```yaml
    name: WelcomeModule
    version: '1.0.0'
    description: A Test Module
    dependencies: []
    authors: [Your Name]
    priority: DEFAULT
    ```

---

## Creating a Module

1. **Extend `MBModule`**:
   In your Java class, override `onEnable()` and `onDisable()`:

   ```java
   public class WelcomeModule extends MBModule {
       @Override
       public void onEnable() {
           getLogger().info("Module enabled!");
       }

       @Override
       public void onDisable() {
           getLogger().info("Module disabled!");
       }
   }
