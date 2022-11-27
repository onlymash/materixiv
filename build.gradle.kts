
plugins {
    val kotlinVersion = "1.7.20"
    id("com.github.ben-manes.versions") version("0.44.0")
    id("com.android.application") version("7.3.1") apply(false)
    id("com.android.library") version("7.3.1") apply(false)
    kotlin("android") version(kotlinVersion) apply(false)
    kotlin("plugin.serialization") version(kotlinVersion) apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}