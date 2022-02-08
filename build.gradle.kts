
plugins {
    val kotlinVersion = "1.6.10"
    id("com.github.ben-manes.versions") version("0.42.0")
    id("com.android.application") version("7.1.1") apply(false)
    id("com.android.library") version("7.1.1") apply(false)
    kotlin("android") version(kotlinVersion) apply(false)
    kotlin("plugin.serialization") version(kotlinVersion) apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}