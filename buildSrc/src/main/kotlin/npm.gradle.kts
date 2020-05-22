import com.moowork.gradle.node.npm.NpmTask

plugins {
    id("com.github.node-gradle.node")
}

node {
    download = true
    workDir = file("${project.buildDir}/nodejs")
    npmWorkDir = file("${project.buildDir}/npm")
    yarnWorkDir = file("${project.buildDir}/yarn")
    nodeModulesDir = file("${project.projectDir}/src/main/webapp")
}

val buildCss = tasks.register<NpmTask>("buildCss") {
    group = "node"
    dependsOn(tasks.yarn)
    setArgs(listOf("run", "css-build"))
}

tasks.named("compileJava").configure { dependsOn(tasks.yarn, buildCss) }