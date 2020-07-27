import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.SigningConfig
import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    kotlin("plugin.serialization")
    id("kotlin-android")
    id("kotlin-android-extensions")
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

fun BuildType.setupSigning(signingConfigs: NamedDomainObjectContainer<SigningConfig>) {
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
    compileSdkVersion(29)
    buildToolsVersion = "29.0.3"
    defaultConfig {
        applicationId = "onlymash.materixiv"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "0.0.1_alpha01"
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=kotlin.ExperimentalStdlibApi",
            "-Xopt-in=kotlinx.serialization.UnstableDefault",
            "-Xopt-in=kotlinx.serialization.ImplicitReflectionSerializer",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.coroutines.ObsoleteCoroutinesApi"
        )
    }
    kapt {
        useBuildCache = true
        arguments {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }
    buildFeatures.viewBinding = true
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    val lifecycleVersion = "2.3.0-alpha06"
    val navVersion = "2.3.0"
    val coroutinesVersion = "1.3.8"
    val kodeinVersion = "7.0.0"
    val okhttpVersion = "4.8.0"
    val retrofitVersion = "2.9.0"
    val roomVersion = "2.3.0-alpha02"
    val glideVersion = "4.11.0"
    val markwonVersion = "4.4.0"

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.kodein.di:kodein-di-framework-android-core:$kodeinVersion")
    implementation("org.kodein.di:kodein-di-framework-android-x:$kodeinVersion")

    implementation("com.google.android.material:material:1.3.0-alpha02")
    implementation("androidx.annotation:annotation:1.2.0-alpha01")
    implementation("androidx.appcompat:appcompat:1.3.0-alpha01")
    implementation("androidx.activity:activity-ktx:1.2.0-alpha07")
    implementation("androidx.fragment:fragment-ktx:1.3.0-alpha07")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.core:core-ktx:1.5.0-alpha01")
    implementation("androidx.drawerlayout:drawerlayout:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.2.0-alpha05")
    implementation("androidx.viewpager2:viewpager2:1.1.0-alpha01")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta8")
    implementation("androidx.browser:browser:1.3.0-alpha04")
    implementation("androidx.documentfile:documentfile:1.0.1")

    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    implementation("androidx.paging:paging-runtime-ktx:2.1.2")
    implementation("androidx.work:work-runtime-ktx:2.4.0")

    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    implementation("com.squareup.okhttp3:okhttp-dnsoverhttps:$okhttpVersion")
    implementation("com.squareup.okio:okio:2.7.0")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.5.0")

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
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

    testImplementation("junit:junit:4.13")
    testImplementation("org.robolectric:robolectric:4.3.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}