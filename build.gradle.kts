import com.tinify.Tinify
import java.io.FileNotFoundException

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

plugins {
    id("com.android.application") version "7.4.1" apply false
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.0" apply false
    id("com.diffplug.spotless") version "6.12.0" apply false
}

buildscript {
    dependencies {
        classpath(deps.google.services)
        classpath(deps.google.oss.license)
        classpath(deps.bundles.firebase.gradle)
        classpath(deps.tinify)
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

task<Task>("tinify") {
    description = "Tinify Compressing images"
    val images = fileTree("app/src/main") {
        include("ic_launcher-playstore.png", "res/mipmap-*/ic_launcher*.png")
    }
    doFirst {
        Tinify.setKey(project.property("tinify.key").toString())
        Tinify.setAppIdentifier("Gradle task")
        for (image in images) {
            if (!image.exists()) {
                throw FileNotFoundException(image.path)
            }
            Tinify.fromFile(image.path).toFile(image.path)
        }
    }
}
