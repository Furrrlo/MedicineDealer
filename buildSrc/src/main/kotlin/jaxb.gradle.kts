import com.intershop.gradle.jaxb.extension.SchemaToJava
import com.intershop.gradle.jaxb.task.SchemaToJavaTask
import java.util.*

plugins {
    java
    id("com.intershop.gradle.jaxb")
}

dependencies {
    val jaxbVer = "2.3.2"
    val jaxb2Basics = "0.12.0"

    compile("org.jvnet.jaxb2_commons:jaxb2-basics-runtime:$jaxb2Basics")

    jaxb("org.glassfish.jaxb:jaxb-xjc:$jaxbVer")
    jaxb("org.glassfish.jaxb:jaxb-runtime:$jaxbVer")

    jaxb("org.jvnet.jaxb2_commons:jaxb2-basics-ant:$jaxb2Basics")
    jaxb("org.slf4j:slf4j-simple:1.7.30")

    // Additional plugins

    jaxb("org.jvnet.jaxb2_commons:jaxb2-basics:$jaxb2Basics")
    jaxb("com.github.jaxb-xew-plugin:jaxb-xew-plugin:1.1")
    jaxb("org.jvnet.jaxb2_commons:jaxb2-value-constructor:3.0")
    jaxb("org.jvnet.jaxb2_commons:jaxb2-fluent-api:3.0")
}

val jaxbDir = "src/generated/jaxb"
sourceSets["main"].java.srcDir(jaxbDir)

configurations.jaxb.get().setTransitive(true)
tasks.named("compileJava").configure { dependsOn(tasks.jaxb) }

jaxb.javaGen {
    val base: SchemaToJava.() -> Unit = {
        antTaskClassName = "org.jvnet.jaxb2_commons.xjc.XJC2Task"
        packageName = "$group.jaxb"
        setOutputDir(file(jaxbDir))
        encoding = "UTF-8"
        extension = true
        strictValidation = true
        header = true
        args = listOf(
                "-Xxew", "-Xxew:instantiate lazy",
                "-Xvalue-constructor",
                "-XenumValue",
                "-Xfluent-api",
                "-XtoString", /*"-XtoString-toStringStrategyClass=$group.jaxb.ToStringStrategy",*/
                "-XsimpleEquals", "-XsimpleHashCode"
        )
    }

    create("web-service") {
        base(this)
        schemas = fileTree(mapOf(
                Pair("dir", "src/main/resources/schema/web-service"),
                Pair("include", "*.xsd")))
    }
}

tasks.withType<SchemaToJavaTask>().configureEach {
    var previousLocale: Locale? = null

    doFirst {
        // Set appropriate locale
        previousLocale = Locale.getDefault()
        Locale.setDefault(Locale.ENGLISH)
        // Clean directory before rewriting
        outputs.files.forEach { outputDir ->
            delete(outputDir)
            outputDir.mkdir()
        }
    }

    doLast {
        if(previousLocale != null)
            Locale.setDefault(previousLocale)
    }
}