import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "me.npkol"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation ("org.jetbrains.lets-plot:lets-plot-batik:2.4.0")
    implementation ("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.0.0")
    implementation ("org.slf4j:slf4j-log4j12:2.0.0")
}