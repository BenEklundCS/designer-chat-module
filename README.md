# designer-chat-module

An Ignition Designer docked window module backed by a local [OpenWebUI](https://openwebui.com/) instance.

Extend IStatefulChatAPI to swap to a different API provider.

## Building

Requires Java 17+ and Ignition 8.3.0+.

```bash
# Linux / macOS
./gradlew build

# Windows
gradlew.bat build
```

Output: `build/DesignerChatModule.modl` — install via the Ignition Gateway module management page.

> Signing is disabled (`skipModlSigning = true`).
