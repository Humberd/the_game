plugins {
    kotlin("jvm") version "1.4.32"
    id("com.utopia-rise.godot-kotlin-jvm") version "0.1.4-3.2.3" apply false
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}
