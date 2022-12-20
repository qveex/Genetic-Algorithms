import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

dependencies {
    implementation ("org.jetbrains.lets-plot:lets-plot-batik:2.5.1")
    implementation ("org.jetbrains.lets-plot:lets-plot-kotlin-jvm:4.1.0")
    implementation ("org.slf4j:slf4j-log4j12:2.0.4")
}