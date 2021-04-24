plugins {
    kotlin("jvm")
    id("com.utopia-rise.godot-kotlin-jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":shared"))
}

godot {
    isAndroidExportEnabled.set(false)
    dxToolPath.set("dx")
}
