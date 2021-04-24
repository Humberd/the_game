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
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")
}

godot {
    isAndroidExportEnabled.set(false)
    dxToolPath.set("dx")
}
