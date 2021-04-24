import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
    implementation("org.slf4j:slf4j-api:1.7.26")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("ch.qos.logback:logback-core:1.2.3")

    // Json
    implementation("com.beust:klaxon:5.5")

    // physics
    implementation("com.badlogicgames.gdx:gdx-box2d:1.9.14")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:1.9.14:natives-desktop")

    // physics debug
    implementation("com.badlogicgames.gdx:gdx:1.9.14")
    implementation("com.badlogicgames.gdx:gdx-platform:1.9.14:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:1.9.14")

    implementation(project(":shared"))
}

kotlin {
    sourceSets.all {
        languageSettings.enableLanguageFeature("InlineClasses")
    }
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xopt-in=kotlin.ExperimentalUnsignedTypes"
    )
}
