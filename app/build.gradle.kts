import com.android.build.api.dsl.ApplicationBuildType
import com.android.build.gradle.internal.dsl.SigningConfig
import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
//    id("com.google.gms.google-services")
//    id("com.google.firebase.crashlytics")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization")
}

fun NamedDomainObjectContainer<SigningConfig>.setup() {
    val releaseStoreFile = file("../.gradle/materixiv.jks")
    val storePropertyFile = file("../.gradle/keystore.properties")
    if (storePropertyFile.exists() && releaseStoreFile.exists()) {
        val singingProperties = Properties()
        singingProperties.load(storePropertyFile.inputStream())
        create("release") {
            storeFile = releaseStoreFile
            keyAlias = singingProperties.getProperty("KEY_ALIAS")
            keyPassword = singingProperties.getProperty("KEY_PASS")
            storePassword = singingProperties.getProperty("STORE_PASS")
        }
    }
}

fun ApplicationBuildType.setupSigning(signingConfigs: NamedDomainObjectContainer<SigningConfig>) {
    val config = try {
        signingConfigs.getByName("release")
    } catch (_: UnknownDomainObjectException) {
        null
    }
    if (config != null) {
        signingConfig = config
    }
}

android {
    signingConfigs.setup()
    namespace = "onlymash.materixiv"
    compileSdk = 33
    buildToolsVersion = "33.0.0"
    defaultConfig {
        applicationId = "onlymash.materixiv"
        minSdk = 21
        targetSdk = 33
        versionCode = 11
        versionName = "0.0.4_beta04"
        renderscriptTargetApi = 21
        renderscriptSupportModeEnabled = false
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            setupSigning(signingConfigs)
            isMinifyEnabled = true
            isShrinkResources= true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug").applicationIdSuffix = ".debug"
    }
//    splits.abi {
//        isEnable = true
//        reset()
//        include("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
//        isUniversalApk = true
//    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.ExperimentalStdlibApi",
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-Xopt-in=kotlinx.coroutines.DelicateCoroutinesApi",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.coroutines.FlowPreview",
            "-Xopt-in=kotlinx.coroutines.ObsoleteCoroutinesApi",
            "-Xopt-in=androidx.paging.ExperimentalPagingApi",
            "-Xjvm-default=compatibility"
        )
    }
    kapt {
        useBuildCache = true
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
            arg("room.expandProjection", "true")
        }
    }
    buildFeatures.viewBinding = true
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    val lifecycleVersion = "2.6.0-alpha03"
    val navVersion = "2.6.0-alpha04"
    val coroutinesVersion = "1.6.4"
    val kodeinVersion = "7.16.0"
    val okhttpVersion = "5.0.0-alpha.10"
    val retrofitVersion = "2.9.0"
    val roomVersion = "2.5.0-beta02"
    val glideVersion = "4.14.2"
    val markwonVersion = "4.6.2"

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk8", "1.6.10"))
    implementation(project(":renderscript-toolkit"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.kodein.di:kodein-di-framework-android-core:$kodeinVersion")
    implementation("org.kodein.di:kodein-di-framework-android-x:$kodeinVersion")

    implementation("com.google.android.material:material:1.8.0-alpha02")
    implementation("androidx.annotation:annotation:1.5.0")
    implementation("androidx.appcompat:appcompat:1.7.0-alpha01")
    implementation("androidx.activity:activity-ktx:1.7.0-alpha02")
    implementation("androidx.fragment:fragment-ktx:1.6.0-alpha03")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.drawerlayout:drawerlayout:1.2.0-alpha01")
    implementation("androidx.recyclerview:recyclerview:1.3.0-rc01")
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta01")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0-alpha04")
    implementation("androidx.browser:browser:1.5.0-alpha01")
    implementation("androidx.documentfile:documentfile:1.1.0-alpha01")

    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    implementation("androidx.paging:paging-runtime-ktx:3.1.1")
    implementation("androidx.work:work-runtime-ktx:2.8.0-beta02")

    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    implementation("com.squareup.okhttp3:okhttp-dnsoverhttps:$okhttpVersion")
    implementation("com.squareup.okio:okio:3.2.0")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    implementation("androidx.room:room-ktx:$roomVersion")
    implementation("androidx.room:room-paging:$roomVersion")
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    implementation("com.github.bumptech.glide:glide:$glideVersion")
    implementation("com.github.bumptech.glide:okhttp3-integration:$glideVersion")
    kapt("com.github.bumptech.glide:compiler:$glideVersion")
    implementation("com.takisoft.preferencex:preferencex-simplemenu:1.1.0")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation("io.noties.markwon:core:$markwonVersion")
    implementation("io.noties.markwon:html:$markwonVersion")
    implementation("io.noties.markwon:ext-strikethrough:$markwonVersion")
    implementation("io.noties.markwon:linkify:$markwonVersion")
    implementation("me.saket:better-link-movement-method:2.2.0")

//    implementation("com.google.firebase:firebase-analytics-ktx:19.0.2")
//    implementation("com.google.firebase:firebase-crashlytics:18.2.3")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.9")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
}