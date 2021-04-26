plugins {
    kotlin("jvm") version "1.5.0-RC"
    id("com.utopia-rise.godot-kotlin-jvm") version "0.1.4-3.2.3" apply false
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
}
