
plugins {
    id("com.github.ben-manes.versions") version("0.39.0")
    kotlin("plugin.serialization") version("1.5.20")
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", "1.5.20"))
        classpath("com.android.tools.build:gradle:4.2.2")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven(url = "https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}