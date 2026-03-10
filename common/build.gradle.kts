plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val generateBuildConfig by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/sources/buildconfig")
    val host = providers.gradleProperty("openWebuiHost").getOrElse("http://localhost:3000")
    val key = providers.gradleProperty("openWebuiKey").getOrElse("")
    val openRouterHost = providers.gradleProperty("openRouterHost").getOrElse("https://openrouter.ai")
    val openRouterKey = providers.gradleProperty("openRouterKey").getOrElse("")

    outputs.dir(outputDir)
    doLast {
        val dir = outputDir.get().asFile.resolve("org/designerchat/common")
        dir.mkdirs()
        dir.resolve("BuildConfig.java").writeText("""
            package org.designerchat.common;

            public final class BuildConfig {
                public static final String OPENWEBUI_HOST = "${host}";
                public static final String OPENWEBUI_KEY = "${key}";
                public static final String OPENROUTER_HOST = "${openRouterHost}";
                public static final String OPENROUTER_KEY = "${openRouterKey}";

                private BuildConfig() {}
            }
        """.trimIndent())
    }
}

sourceSets["main"].java.srcDir(tasks.named("generateBuildConfig").map { it.outputs.files.singleFile })
tasks.named("compileJava") { dependsOn(generateBuildConfig) }

dependencies {
    compileOnly(libs.ignition.common)
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")
}

tasks.test {
    useJUnitPlatform()
}
