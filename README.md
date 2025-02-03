# MythLibs

MythLibs is a powerful Minecraft plugin library designed to enhance your Minecraft server experience. This plugin is built using Kotlin and Gradle, and is compatible with Minecraft version 1.21.

## Features

- Easy to integrate with your Minecraft server
- Built with the latest Kotlin and Gradle versions
- Utilizes the Shadow plugin for building JAR files

## Requirements

- Java 21
- Minecraft 1.21
- Spigot API 1.21-R0.1-SNAPSHOT

## Getting Started

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Artaphy/MythLibs.git
   ```

2. Navigate to the project directory:
   ```bash
   cd MythLibs
   ```

3. Build the project using Gradle:
   ```bash
   ./gradlew build
   ```

4. The plugin JAR file will be located in the `build/libs` directory. Copy this file to your Minecraft server's `plugins` directory.

### Usage

- Start your Minecraft server. The plugin will automatically be loaded.
- Check the server console for messages indicating that the plugin has been enabled.

## Development

### Prerequisites

- Ensure you have Java 21 installed.
- Install Gradle 8.12.1 or use the provided Gradle wrapper.

### Building the Project

- Use the following command to build the project:
  ```bash
  ./gradlew build
  ```

- To create a shadow JAR, use:
  ```bash
  ./gradlew shadowJar
  ```

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request.

## License

This project is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for details.

## Contact

For any questions or suggestions, please contact [Artaphy](mailto:artaphy@163.com). 