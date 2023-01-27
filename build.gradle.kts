plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.26.0"
}

project.group = "com.engineal"
project.version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("com.engineal.scandatehelper")
    mainClass.set("com.engineal.scandatehelper.ScanDateHelperApplication")
}

javafx {
    version = "19.0.2.1"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.apache.commons:commons-imaging:1+")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5+")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5+")
}

dependencyLocking {
    lockAllConfigurations()
}

tasks.test {
    useJUnitPlatform()
}

jlink {
    imageZip.set(file("${buildDir}/distributions/app-${javafx.platform.classifier}.zip"))
    options.addAll("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    launcher {
        name = "app"
    }
}

tasks.jlinkZip {
    group = "distribution"
}
