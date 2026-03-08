plugins {
    base
    id("io.ia.sdk.modl") version("0.1.1")
    id("org.barfuin.gradle.taskinfo") version "2.1.0"
}

allprojects {
    version = "1.0.0"
    group = "org.designerchat"
}

ignitionModule {
    fileName.set("DesignerChatModule")

    name.set("Designer Chat Module")
    id.set("org.designerchat.designerchat")
    moduleVersion.set("${project.version}")
    moduleDescription.set("An Ignition module that adds a chat panel to the Designer.")
    requiredIgnitionVersion.set("8.3.0")
    license.set("license.html")

    projectScopes.putAll(
        mapOf(
            ":gateway" to "G",
            ":designer" to "D",
            ":common" to "GD"
        )
    )

    hooks.putAll(
        mapOf(
            "org.designerchat.gateway.ChatGatewayHook" to "G",
            "org.designerchat.designer.ChatDesignerHook" to "D"
        )
    )

    skipModlSigning.set(true)
}

// ── Deploy configuration ────────────────────────────────────────────────────
val gatewayHost = "http://10.0.0.84:8088"
val gatewayApiKey = providers.gradleProperty("gatewayApiKey").getOrElse("")
val moduleId = "org.designerchat.designerchat"

fun httpClient() = java.net.http.HttpClient.newHttpClient()

fun authHeaders(builder: java.net.http.HttpRequest.Builder): java.net.http.HttpRequest.Builder =
    if (gatewayApiKey.isNotBlank()) builder.header("X-Ignition-API-Token", gatewayApiKey)
    else builder

tasks.named("build") { mustRunAfter(allprojects.map { "${it.path}:clean" }) }

val uploadModule by tasks.registering {
    description = "Uploads the .modl file to the gateway."
    dependsOn(allprojects.map { "${it.path}:clean" }, "build")
    doLast {
        val buildDir = layout.buildDirectory.get().asFile
        val file = listOf("DesignerChatModule.modl", "DesignerChatModule.unsigned.modl")
            .map { buildDir.resolve(it) }
            .first { it.exists() }
        val request = authHeaders(
            java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI("$gatewayHost/data/api/v1/modules/upload?fileName=${file.name}"))
                .header("Content-Type", "application/octet-stream")
                .POST(java.net.http.HttpRequest.BodyPublishers.ofFile(file.toPath()))
        ).build()

        val response = httpClient().send(request, java.net.http.HttpResponse.BodyHandlers.ofString())
        check(response.statusCode() == 200) { "Upload failed (${response.statusCode()}): ${response.body()}" }
        logger.lifecycle("Module uploaded.")
    }
}

val installModule by tasks.registering {
    description = "Installs the previously uploaded module on the gateway."
    dependsOn(uploadModule)
    doLast {
        val request = authHeaders(
            java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI("$gatewayHost/data/api/v1/modules/install?moduleId=$moduleId"))
                .POST(java.net.http.HttpRequest.BodyPublishers.noBody())
        ).build()

        val response = httpClient().send(request, java.net.http.HttpResponse.BodyHandlers.ofString())
        check(response.statusCode() == 200) { "Install failed (${response.statusCode()}): ${response.body()}" }
        logger.lifecycle("Module installed.")
    }
}

val restartGateway by tasks.registering {
    description = "Restarts the Ignition gateway."
    doLast {
        val request = authHeaders(
            java.net.http.HttpRequest.newBuilder()
                .uri(java.net.URI("$gatewayHost/data/api/v1/restart-tasks/restart?confirm=true"))
                .POST(java.net.http.HttpRequest.BodyPublishers.noBody())
        ).build()

        val response = httpClient().send(request, java.net.http.HttpResponse.BodyHandlers.ofString())
        check(response.statusCode() == 200) { "Restart failed (${response.statusCode()}): ${response.body()}" }
        logger.lifecycle("Gateway restarting.")
    }
}

val deploy by tasks.registering {
    description = "Builds, uploads, installs, and restarts the gateway."
    dependsOn(installModule, restartGateway)
    // restartGateway must run after installModule
    restartGateway.get().mustRunAfter(installModule)
}

// ────────────────────────────────────────────────────────────────────────────

val deepClean by tasks.registering {
    dependsOn(allprojects.map { "${it.path}:clean" })
    description = "Executes clean tasks and removes caches."
    doLast {
        delete(file(".gradle"))
    }
}
