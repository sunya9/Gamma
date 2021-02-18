import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.io.FileInputStream
import java.util.*


plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("android.extensions")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.oss.licenses.plugin")
    id("com.google.gms.google-services") apply false
    id("com.google.firebase.crashlytics")
    id("jacoco")
    id("androidx.navigation.safeargs.kotlin")
}

jacoco {
    toolVersion = "0.8.5"
}

task("jacocoTestReport", JacocoReport::class) {
    dependsOn("testDebugUnitTest", "connectedDebugAndroidTest", "createDebugCoverageReport")
    reports {
        xml.isEnabled = true
        csv.isEnabled = false
        html.isEnabled = true
    }
    sourceDirectories.setFrom("${projectDir}/src/main/java")
    classDirectories.setFrom("${buildDir}/tmp/kotlin-classes/debug")
    executionData.setFrom(fileTree(buildDir) {
        include(
            "jacoco/testDebugUnitTest.exec",
            "outputs/code_coverage/debugAndroidTest/connected/*coverage.ec"
        )
    })
}

android {
    ndkVersion = "18.1.5063045"
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "net.unsweets.gamma"
        minSdkVersion(23)
        targetSdkVersion(29)
        versionCode = 6
        versionName = "0.4.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        renderscriptTargetApi = 23
        renderscriptSupportModeEnabled = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isTestCoverageEnabled = true
        }
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    lintOptions {
        isAbortOnError = false
    }
    testOptions {
        unitTests.isIncludeAndroidResources = true
        unitTests.isReturnDefaultValues = true
    }
    packagingOptions {
        // https://github.com/mockito/mockito/issues/1376#issuecomment-391192483
        pickFirst("mockito-extensions/org.mockito.plugins.MockMaker")
    }

    sourceSets {
        val sharedTestDir = "src/sharedTest/java"
        getByName("test") {
            java.srcDir(sharedTestDir)
        }
        getByName("androidTest") {
            java.srcDir(sharedTestDir)
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
    implementation("androidx.wear:wear:1.0.0")
    compileOnly("com.google.android.wearable:wearable:2.6.0")

    val lifecycleVersion = "2.2.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-extensions:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlinVersion")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0-alpha2")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")

    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("com.squareup.retrofit2:retrofit:2.5.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.5.0")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("com.otaliastudios:nestedscrollcoordinatorlayout:1.0.3")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.30")


    val moshiVersion = "1.9.2"
    implementation("com.squareup.moshi:moshi:$moshiVersion")
    implementation("com.squareup.moshi:moshi-adapters:$moshiVersion")
    implementation("com.squareup.moshi:moshi-kotlin:$moshiVersion")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")

    val glideVersion = "4.11.0"
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    kapt("com.github.bumptech.glide:compiler:$glideVersion")

    kapt("com.android.databinding:compiler:3.1.4")

    val coroutinesVersion = "1.3.0"
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")

    val supportVersion = "28.0.0"
    implementation("com.android.support:palette-v7:$supportVersion")
    implementation("com.android.support:customtabs:$supportVersion")
    implementation("com.android.support:support-emoji:$supportVersion")
    implementation("com.android.support:appcompat-v7:$supportVersion")
    implementation("com.android.support:support-emoji-bundled:$supportVersion")

    implementation("com.squareup.okhttp3:logging-interceptor:3.12.1")

    val daggerVersion = "2.23.2"
    implementation("com.google.dagger:dagger:$daggerVersion")
    implementation("com.google.dagger:dagger-android:$daggerVersion")
    implementation("com.google.dagger:dagger-android-support:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    kapt("com.google.dagger:dagger-android-processor:$daggerVersion")
    kaptAndroidTest("com.google.dagger:dagger-compiler:$daggerVersion")
    kaptAndroidTest("com.google.dagger:dagger-android-processor:$daggerVersion")

    implementation("com.theartofdev.edmodo:android-image-cropper:2.8.0")
    implementation("com.github.thefuntasty.hauler:library:2.0.0")

    implementation("com.google.android.gms:play-services-oss-licenses:17.0.0")

    val firebaseVersion = "17.2.2"
    implementation("com.google.firebase:firebase-core:$firebaseVersion")
    implementation("com.google.firebase:firebase-analytics:$firebaseVersion")
    implementation("com.google.firebase:firebase-crashlytics:17.3.1")
    implementation("com.google.firebase:firebase-analytics:18.0.2")

    implementation("jp.wasabeef:glide-transformations:4.1.0")
    implementation("me.zhanghai.android.materialprogressbar:library:1.6.1")

    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-core:2.28.2")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    androidTestImplementation("org.mockito:mockito-core:2.28.2")
    androidTestImplementation("org.mockito:mockito-android:2.28.2")
    testImplementation("org.robolectric:robolectric:4.3")
    val espressoVersion = "3.3.0"
    androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
    androidTestImplementation("androidx.test.espresso:espresso-intents:$espressoVersion")
    val testVersion = "1.3.0"
    androidTestImplementation("androidx.test:core:$testVersion")
    androidTestImplementation("androidx.test:rules:$testVersion")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.ext:truth:1.3.0")
    androidTestImplementation("com.google.truth:truth:1.0")
    testImplementation("org.powermock:powermock-module-junit4:2.0.2")
    testImplementation("org.powermock:powermock-api-mockito2:2.0.2")
    androidTestImplementation("org.powermock:powermock-module-junit4:2.0.2")
    androidTestImplementation("org.powermock:powermock-api-mockito2:2.0.2")
    implementation("com.github.Chrisvin:EasyReveal:1.2")
    // Kotlin
    val navVersion = "2.3.1"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    testImplementation("org.robolectric:robolectric:4.5.1")

}

androidExtensions {
    isExperimental = true
}

apply(plugin = "com.google.gms.google-services")
