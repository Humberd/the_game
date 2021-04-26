plugins {
    kotlin("jvm")
    id("com.utopia-rise.godot-kotlin-jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    // Logging
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.2")
    implementation("org.slf4j:slf4j-api:1.7.26")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("ch.qos.logback:logback-core:1.2.3")

    implementation(project(":shared"))

}

godot {
    isAndroidExportEnabled.set(false)
    dxToolPath.set("dx")
}
