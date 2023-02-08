import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm") version "1.7.20"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.5.0"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
}

tasks {
    named<Jar>("jar") {
        archiveBaseName.set("app")

        manifest {
            attributes["Main-Class"] = application.mainClass
            attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(separator = " ") {
                it.name
            }
        }

        doLast {
            configurations.runtimeClasspath.get().forEach {
                val file = File("$buildDir/libs/${it.name}")
                if (!file.exists()) {
                    it.copyTo(file)
                }
            }
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("no.nav.common:kafka:2.2023.01.02_13.51-1c6adeb1653b")
    implementation("org.apache.kafka:kafka-clients:3.3.2")
    implementation("org.apache.avro:avro:1.11.0")
    implementation("io.confluent:kafka-avro-serializer:7.2.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("no.nav.paw.data.innlop.AppKt")
}
