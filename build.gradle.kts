import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.parinherm"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs);
                implementation("net.java.dev.jna:jna:5.7.0");
                implementation("com.alphacephei:vosk:0.3.38");
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4");
                runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.4");
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "natter"
            packageVersion = "1.0.0"
        }
    }
}
