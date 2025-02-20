import com.jetbrains.plugin.structure.base.utils.contentBuilder.buildDirectory
import com.jetbrains.plugin.structure.base.utils.contentBuilder.buildZipFile
import org.gradle.kotlin.dsl.intellijPlatform
import java.util.*
import org.jetbrains.intellij.platform.gradle.*
import org.jetbrains.intellij.platform.gradle.models.*
import org.jetbrains.intellij.platform.gradle.tasks.*

plugins {
    id("org.jetbrains.intellij.platform") version "2.0.1"
    kotlin("jvm") version "2.0.0"
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.2")
        instrumentationTools()
    }

    testImplementation("com.jetbrains.intellij.tools:ide-starter-squashed:243.24978.46")
    testImplementation("com.jetbrains.intellij.tools:ide-starter-junit5:243.24978.46")
    testImplementation("com.jetbrains.intellij.tools:ide-starter-driver:243.24978.46")
    testImplementation("com.jetbrains.intellij.driver:driver-client:243.24978.46")
    testImplementation("com.jetbrains.intellij.driver:driver-sdk:243.24978.46")
    testImplementation("com.jetbrains.intellij.driver:driver-model:243.24978.46")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.kodein.di:kodein-di-jvm:7.20.2")
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild.set("242")
            untilBuild.set("243.*")
        }
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    dependsOn("buildPlugin")
    systemProperty("path.to.build.plugin", tasks.buildPlugin.get().archiveFile.get().asFile.absolutePath)
    useJUnitPlatform()

    jvmArgs(*(jvmArgs.orEmpty().toTypedArray() + "-Djava.rmi.server.useCodebaseOnly=false"))
}
