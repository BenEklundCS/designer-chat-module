plugins {
    `java-library`
    checkstyle
    id("com.diffplug.spotless") version "6.25.0"
}

checkstyle {
    toolVersion = "10.12.5"
}

spotless {
    java {
        palantirJavaFormat()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    api(projects.common)
    compileOnly(libs.ignition.common)
    compileOnly(libs.ignition.designer.api)
}
