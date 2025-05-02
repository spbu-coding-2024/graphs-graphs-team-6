import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
	kotlin("jvm")
	id("org.jetbrains.compose")
	id("org.jetbrains.kotlin.plugin.compose")
	id("com.github.jk1.dependency-license-report") version "2.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
	maven("https://repo.kotlin.link")
	maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
	google()
	maven("https://jitpack.io")
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
	detekt("io.gitlab.arturbosch.detekt:detekt-cli:1.23.8")
	testImplementation(kotlin("test"))
	testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
	implementation("space.kscience:kmath-core:0.4.2")
	implementation("com.github.JetBrains-Research:louvain:main-SNAPSHOT")
	testImplementation("io.mockk:mockk:1.14.2")
	implementation("org.neo4j:neo4j-ogm-core:4.0.17")
	implementation("org.neo4j:neo4j-ogm-bolt-driver:4.0.17")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.0")
}

tasks.test {
	useJUnitPlatform()
}

tasks.check {
	dependsOn(detektTask)
}

val runtimeClasspath: Configuration by configurations

licenseReport {
	outputDir = "$buildDir/reports/licenses"

	copySpec {
		val jars = runtimeClasspath.filter { it.name.endsWith(".jar") }

		jars
			.map { zipTree(it) }
			.forEach { from(it) }

		include(
			"META-INF/LICENSE",  "META-INF/LICENSE.*",
			"META-INF/NOTICE",   "META-INF/NOTICE.*"
		)
		into("$buildDir/licenses")
		rename("^META-INF/(.*)", "$1")
	}
}

tasks.named<Jar>("jar") {
	dependsOn("generateLicenseReport")
	from("$buildDir/licenses") {
		into("META-INF")
	}
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


