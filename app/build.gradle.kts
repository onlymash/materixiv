import com.android.build.gradle.internal.dsl.BuildType
import com.android.build.gradle.internal.dsl.SigningConfig
import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import org.gradle.api.NamedDomainObjectContainer
import org.jetbrains.kotlin.config.KotlinCompilerVersion
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    id("com.android.application")
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
    compileSdkVersion(30)
    buildToolsVersion = "30.0.3"
    defaultConfig {
        applicationId = "onlymash.materixiv"
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 7
        versionName = "0.0.3_beta02"
        renderscriptTargetApi = 21
        renderscriptSupportModeEnabled = false
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    applicationVariants.all {
        outputs.map { it as BaseVariantOutputImpl }
            .forEach { output ->
                output.outputFileName = "materixiv_${defaultConfig.versionName}.apk"
            }
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
            "-Xopt-in=kotlinx.serialization.ExperimentalSerializationApi",
            "-Xopt-in=kotlinx.coroutines.DelicateCoroutinesApi",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlinx.coroutines.FlowPreview",
            "-Xopt-in=kotlinx.coroutines.ObsoleteCoroutinesApi",
            "-Xopt-in=androidx.paging.ExperimentalPagingApi"
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
    val lifecycleVersion = "2.4.0-alpha02"
    val navVersion = "2.4.0-alpha04"
    val coroutinesVersion = "1.5.1"
    val kodeinVersion = "7.6.0"
    val okhttpVersion = "5.0.0-alpha.2"
    val retrofitVersion = "2.9.0"
    val roomVersion = "2.4.0-alpha03"
    val glideVersion = "4.12.0"
    val markwonVersion = "4.6.2"

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(kotlin("stdlib-jdk8", KotlinCompilerVersion.VERSION))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.kodein.di:kodein-di-framework-android-core:$kodeinVersion")
    implementation("org.kodein.di:kodein-di-framework-android-x:$kodeinVersion")

    implementation("com.google.android.material:material:1.5.0-alpha01")
    implementation("androidx.annotation:annotation:1.3.0-alpha01")
    implementation("androidx.appcompat:appcompat:1.4.0-alpha03")
    implementation("androidx.activity:activity-ktx:1.3.0-rc02")
    implementation("androidx.fragment:fragment-ktx:1.4.0-alpha04")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.core:core-ktx:1.7.0-alpha01")
    implementation("androidx.drawerlayout:drawerlayout:1.1.1")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.viewpager2:viewpager2:1.1.0-alpha01")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0-beta02")
    implementation("androidx.browser:browser:1.3.0")
    implementation("androidx.documentfile:documentfile:1.0.1")

    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    implementation("androidx.paging:paging-runtime-ktx:3.1.0-alpha02")
    implementation("androidx.work:work-runtime-ktx:2.7.0-alpha04")

    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttpVersion")
    implementation("com.squareup.okhttp3:okhttp-dnsoverhttps:$okhttpVersion")
    implementation("com.squareup.okio:okio:3.0.0-alpha.8")
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

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

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.robolectric:robolectric:4.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}