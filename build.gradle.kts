plugins {
    java
    application
    id("com.google.protobuf") version "0.8.10" apply false
}

version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testCompile("junit", "junit", "4.12")
    implementation(project(":run"))
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

application {
    mainClassName = "ru.ifmo.java.run.RunServers"
}

