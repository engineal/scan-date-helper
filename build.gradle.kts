plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.26.0"
}

project.group = "com.engineal"
project.version = "1.0.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(19))
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
    options.addAll("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")
    jpackage {
        installerOptions = listOf("--win-shortcut-prompt", "--win-menu", "--win-shortcut",
                "--license-file", "LICENSE",
                "--about-url", "https://github.com/engineal/scan-date-helper",
                "--win-update-url", "https://github.com/engineal/scan-date-helper/releases",
                "--win-upgrade-uuid", "19452a95-28e2-4f93-a1ee-d49b5a18865f"
        )
    }
}

tasks.jlinkZip {
    group = "distribution"
}
