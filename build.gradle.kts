import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("com.avast.gradle.docker-compose") version "0.17.11"
    application
}

group = "com.github.fpiechowski"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("com.github.fpiechowski.kontaktio.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.arrow-kt:arrow-stack:1.2.4"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:suspendapp:0.4.0")

    implementation("io.github.oshai:kotlin-logging:7.0.0")
    implementation("ch.qos.logback:logback-classic:1.5.12")

    implementation("com.sksamuel.hoplite:hoplite-core:2.9.0")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.9.0")
    implementation("com.sksamuel.hoplite:hoplite-arrow:2.9.0")

    val ktorVersion = "3.0.1"
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    val kotestVersion = "5.9.1"
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest.extensions:kotest-assertions-ktor:2.0.0")

    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
}

tasks {
    test {
        useJUnitPlatform()
    }

    val integrationTest by registering(Test::class) {
        description = "Runs the integration tests."
        group = "verification"
        testClassesDirs = sourceSets["integrationTest"].output.classesDirs
        classpath = sourceSets["integrationTest"].runtimeClasspath
        useJUnitPlatform()
        shouldRunAfter("test")
    }

    check {
        dependsOn(integrationTest)
    }

    dockerCompose.isRequiredBy(integrationTest)

    composeBuild.get().dependsOn(shadowJar.get())
}

tasks.withType<ShadowJar> {
    archiveFileName.set("kontaktio-all.jar")
    mergeServiceFiles()
}

sourceSets {
    val domain by creating

    create("integrationTest") {
        compileClasspath += sourceSets.main.get().output + sourceSets.test.get().output
        runtimeClasspath += sourceSets.main.get().output + sourceSets.test.get().output
    }

    main {
        compileClasspath += domain.output
        runtimeClasspath += domain.output
    }

    tasks.jar {
        from(domain.output)
    }

    tasks.shadowJar {
        from(domain.output)
    }
}

configurations["integrationTestImplementation"].extendsFrom(
    configurations.implementation.get(),
    configurations.testImplementation.get()
)

configurations["integrationTestRuntimeOnly"].extendsFrom(
    configurations.runtimeOnly.get(),
    configurations.testRuntimeOnly.get()
)

configurations["domainImplementation"].extendsFrom(configurations.implementation.get())
configurations["domainRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

dockerCompose {
    setProjectName("kontaktio")
    useComposeFiles.set(listOf("src/integrationTest/resources/docker-compose.yaml"))
}
