plugins {
    kotlin("jvm") version "1.5.0"
    id("com.utopia-rise.godot-kotlin-jvm") version "0.2.0-3.3.0" apply false
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
