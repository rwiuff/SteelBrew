/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    id("buildlogic.java-application-conventions")
}

dependencies {
    implementation("org.apache.commons:commons-text")
    implementation(project(":steelbrew"))
}

application {
    // Define the main class for the application.
    mainClass = "org.rwiuff.steelbrew.Driver"
}
