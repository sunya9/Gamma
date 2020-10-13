// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion: String by extra { "1.3.60" }
    repositories {
        google()
        jcenter()
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        maven(url = "https://jitpack.io")


    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.0.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.gms:oss-licenses:0.9.2")
        classpath("com.google.gms:google-services:4.3.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.3.0")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    val kotlinVersion: String by extra { "1.3.50" }
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
