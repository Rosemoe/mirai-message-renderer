import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.6.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.11.1"
}

group = "io.github.rosemoe"
version = "1.0.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    val VERSION = "2.11.1"
    implementation("net.mamoe:mirai-core:$VERSION") // mirai-core 的 API
    implementation("net.mamoe:mirai-console:$VERSION") // 后端
    testImplementation(kotlin("test"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
