# designer-chat-module

An Ignition Designer docked window module backed by a local [OpenWebUI](https://openwebui.com/) instance, or an [OpenRouter](https://openrouter.ai/) API key.

Extend `IChatAPI` to swap to a different API provider.

### Overview

1. Select AI Model in the Dropdown
2. Connected status displayed on • indicator to the left of the dropdown
3. Clear chat by clicking "Clear"
4. Type into the text area and press "Send" to send a message to the model 
<img width="1485" height="604" alt="image" src="https://github.com/user-attachments/assets/33a0c622-915f-4355-a03f-e67794c1c78d" />


Access from **View > Panels > Chat**

<img width="377" height="144" alt="image" src="https://github.com/user-attachments/assets/e4dfa489-42fc-4303-ae4f-06e35509f168" />


## Setup

### Prerequisites

- Java 17+
- Ignition 8.3.0+

### Configuration

Copy the example properties file and fill in your values:

```bash
cp gradle.properties.example gradle.properties
```

Edit `gradle.properties` with your configuration:

```properties
# Ignition gateway API key (for deploy tasks)
gatewayApiKey=your-gateway-api-key

# Open WebUI / Ollama backend
openWebuiHost=http://localhost:3000
openWebuiKey=your-openwebui-key

# OpenRouter backend
openRouterHost=https://openrouter.ai
openRouterKey=your-openrouter-key
```

> `gradle.properties` is gitignored — credentials stay local.

To switch between backends, change the `IChatAPI` implementation in `ChatDesignerHook.java`:

```java
// OpenRouter (cloud, free tier available)
new ChatFrame(new OpenRouterChatAPI());

// Open WebUI (local Ollama)
new ChatFrame(new OllamaChatAPI());
```

## Building

```bash
# Linux / macOS
./gradlew build

# Windows
gradlew.bat build
```

Output: `build/DesignerChatModule.modl` — install via the Ignition Gateway module management page.

> Signing is disabled (`skipModlSigning = true`).

## Deploy

If your gateway API key is configured, you can build, upload, install, and restart in one step:

```bash
./gradlew deploy
```

Or run the steps individually:

```bash
./gradlew uploadModule    # upload .modl to gateway
./gradlew installModule   # install the uploaded module
./gradlew restartGateway  # restart the gateway
```

## Project Structure

```
common/     # shared interfaces and data classes (IChatAPI, ChatHistoryRecord)
designer/   # designer-scoped UI and API implementations
gateway/    # gateway hook (module lifecycle)
```
