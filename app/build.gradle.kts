import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.*


plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("com.google.gms.oss.licenses.plugin")
    id("io.fabric")
    id("com.google.gms.google-services") apply false

}

android {
    compileSdkVersion(28)
    defaultConfig {
        applicationId = "net.unsweets.gamma"
        minSdkVersion(23)
        targetSdkVersion(28)
        versionCode = 6
        versionName = "0.4.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments = mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
        renderscriptTargetApi = 23
        renderscriptSupportModeEnabled = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    dataBinding {
        isEnabled = true
    }
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    if (keystorePropertiesFile.exists()) {
        // Create a variable called keystorePropertiesFile, and initialize it to your
        // keystore.properties file, in the rootProject folder.

        // Initialize a new Properties() object called keystoreProperties.
        val keystoreProperties = Properties()

        // Load your keystore.properties file into the keystoreProperties object.
        keystoreProperties.load(FileInputStream(keystorePropertiesFile))
        signingConfigs {
            create("release") {
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
                storeFile = File(keystoreProperties.getProperty("storeFile"))
                storePassword = keystoreProperties.getProperty("storePassword")
            }
        }
    }

}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xallow-result-return-type")
        jvmTarget = "1.8"
    }
}
val kotlinVersion: String by extra

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))

    val lifecycleVersion = "2.2.0-alpha05"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-extensions:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("androidx.constraintlayout:constraintlayout:2.0.0-beta2")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    implementation("com.google.android.material:material:1.1.0-alpha10")
    implementation("androidx.appcompat:appcompat:1.1.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0-beta04")
    implementation("com.squareup.retrofit2:retrofit:2.5.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.5.0")
    implementation("androidx.preference:preference:1.1.0")
    implementation("com.otaliastudios:nestedscrollcoordinatorlayout:1.0.3")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.3.50")


    val moshiVersion = "1.9.0-SNAPSHOT"
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-adapters:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    val glideVersion = "4.9.0"
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    kapt("com.github.bumptech.glide:compiler:$glideVersion")

    kapt("com.android.databinding:compiler:3.1.4")
    kapt("android.arch.lifecycle:compiler:1.1.1")

    val coroutinesVersion = "1.3.0"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    val supportVersion = "28.0.0"
    implementation("com.android.support:palette-v7:$supportVersion")
    implementation("com.android.support:customtabs:$supportVersion")
    implementation("com.android.support:support-emoji:$supportVersion")
    implementation("com.android.support:appcompat-v7:$supportVersion")
    implementation("com.android.support:support-emoji-bundled:$supportVersion")

    val navVersion = "1.0.0"
    implementation("android.arch.navigation:navigation-fragment-ktx:$navVersion")
    implementation("android.arch.navigation:navigation-ui-ktx:$navVersion")

    val roomVersion = "2.1.0"

    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

    implementation("com.squareup.okhttp3:logging-interceptor:3.12.1")

    val daggerVersion = "2.25.4"
    implementation("com.google.dagger:dagger:$daggerVersion")
    implementation("com.google.dagger:dagger-android:$daggerVersion")
    implementation("com.google.dagger:dagger-android-support:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    kapt("com.google.dagger:dagger-android-processor:$daggerVersion")
    kaptTest("com.google.dagger:dagger-compiler:$daggerVersion")

    implementation("com.theartofdev.edmodo:android-image-cropper:2.8.0")
    implementation("com.github.thefuntasty.hauler:library:2.0.0")

    implementation("com.google.android.gms:play-services-oss-licenses:17.0.0")

    val firebaseVersion = "17.2.0"
    implementation("com.google.firebase:firebase-core:$firebaseVersion")
    implementation("com.google.firebase:firebase-analytics:$firebaseVersion")
    implementation("com.crashlytics.sdk.android:crashlytics:2.10.1")

    implementation("jp.wasabeef:glide-transformations:4.1.0")
    implementation("me.zhanghai.android.materialprogressbar:library:1.6.1")

    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}

androidExtensions {
    isExperimental = true
}

apply(plugin = "com.google.gms.google-services")
