
plugins {
    id("com.github.ben-manes.versions") version("0.39.0")
    kotlin("plugin.serialization") version("1.5.31")
}

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", "1.5.31"))
        classpath("com.android.tools.build:gradle:7.1.0-alpha13")
//        classpath("com.google.gms:google-services:4.3.10")
//        classpath("com.google.firebase:firebase-crashlytics-gradle:2.7.1")
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