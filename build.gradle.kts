// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion: String by extra { "1.4.30" }
    repositories {
        google()
        jcenter()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        maven(url = "https://jitpack.io")


    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.gms:oss-licenses:0.9.2")
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.4.1")
        val navVersion = "2.3.1"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    val kotlinVersion: String by extra { "1.4.30" }
    repositories {
        google()
        jcenter()
        maven(url = "https://jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
  configurations.all {
    resolutionStrategy {
      force("org.objenesis:objenesis:2.6")
    }
  }
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}
