@file:OptIn(ExperimentalComposeLibrary::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
	kotlin("jvm")
	id("org.jetbrains.compose")
	id("jacoco")
	id("org.jetbrains.kotlin.plugin.compose")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
	maven("https://repo.kotlin.link")
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	google()
}

val detekt by configurations.creating

val detektTask = tasks.register<JavaExec>("detekt") {
	mainClass.set("io.gitlab.arturbosch.detekt.cli.Main")
	classpath = detekt

	val input = projectDir
	val config = "$projectDir/detekt.yml"
	val exclude = ".*/build/.*,.*/resources/.*"
	val params = listOf("-i", input, "-c", config, "-ex", exclude)

	args(params)
}

kotlin {
	jvmToolchain(21)
}


java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
	}
}

dependencies {
	// Note, if yoºu develop a library, you should use compose.desktop.common.
	// compose.desktop.currentOs should be used in launcher-sourceSet
	// (in a separate module for demo project and in testMain).
	// With compose.desktop.common you will also lose @Preview functionality
	implementation(compose.desktop.currentOs)
	implementation(compose.uiTest)
	implementation(compose.desktop.uiTestJUnit4)
	detekt("io.gitlab.arturbosch.detekt:detekt-cli:1.23.8")
	testImplementation(kotlin("test"))
	testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
	implementation("space.kscience:kmath-core:0.4.2")
}

tasks.test {
	useJUnitPlatform()
}

tasks.check {
	dependsOn(detektTask)
}

compose.desktop {
	application {
		mainClass = "MainKt"

		nativeDistributions {
			targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
			packageName = "graphs"
			packageVersion = "1.0.0"
		}
	}
}
