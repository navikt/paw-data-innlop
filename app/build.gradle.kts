import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val githubPassword: String by project

plugins {
    application
    kotlin("jvm") version "1.7.20"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.5.0"
    id("org.jmailen.kotlinter") version "3.13.0"
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://maven.pkg.github.com/navikt/*")
        credentials {
            username = "x-access-token"
            password = githubPassword
        }
    }
}

tasks {
    test {
        useJUnit()
    }
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
    implementation("org.apache.kafka:kafka-streams:7.3.1-ccs")
    implementation("org.apache.kafka:kafka-clients:7.3.1-ccs")
    implementation("io.confluent:kafka-streams-avro-serde:7.2.0")
    implementation("org.apache.avro:avro:1.11.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("net.logstash.logback:logstash-logback-encoder:7.2")
    implementation("no.nav.paw:pdl-client:0.1.0")

    testImplementation("org.apache.kafka:kafka-streams-test-utils:7.3.1-ccs")
    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:kafka:1.17.6")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("io.mockk:mockk:1.13.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("no.nav.paw.data.innlop.AppKt")
}
