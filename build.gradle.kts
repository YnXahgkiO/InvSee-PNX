plugins {
    `java-library`
    id("io.github.goooler.shadow") version "8.1.7"
}

group = "back"
version = "1.0.0"

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(files("../PowerNukkitX/build/powernukkitx.jar"))
    compileOnly(files("../InvMenuPNX/build/libs/InvMenuPNX-1.0.0.jar"))
    compileOnly("org.jetbrains:annotations:24.1.0")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveFileName.set("InvSee-PNX-${project.version}.jar")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
