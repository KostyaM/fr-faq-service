val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

val postgres_version: String by project
val ktorm_version: String by project
val jackson_version: String by project
val koin_version: String by project


plugins {
    kotlin("jvm") version "1.9.10"
    id("io.ktor.plugin") version "2.3.3"
}


group = "faq.fastreport.ru"
version = "0.0.1"

application {
    mainClass.set("faq.fastreport.ru.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Server setup
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")

    // Serialization
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jackson_version")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jackson_version")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")

    // DI
    implementation( "io.insert-koin:koin-ktor:$koin_version")
    implementation( "io.insert-koin:koin-logger-slf4j:$koin_version")

    // database
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("org.ktorm:ktorm-core:$ktorm_version")
    implementation("org.ktorm:ktorm-support-postgresql:$ktorm_version")

    // Auth
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt:$ktor_version")

    // Logging
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")


    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks.register<JavaExec>("migrate") {
    group = "Execution"
    description = "Migrates the database to the latest version"
    classpath = sourceSets.getByName("main").runtimeClasspath
    mainClass.set("faq.fastreport.ru.db.RunMigration")
}
