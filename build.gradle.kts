// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlinVersion: String by extra { "1.3.50" }
    repositories {
        google()
        jcenter()
        maven(url = "https://maven.fabric.io/public")
    }
    dependencies {
      classpath("com.android.tools.build:gradle:3.6.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("com.google.gms:oss-licenses:0.9.2")
      classpath("com.google.gms:google-services:4.3.3")
        classpath("io.fabric.tools:gradle:1.31.1")
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
}

task("clean", Delete::class) {
    delete = setOf(rootProject.buildDir)
}
