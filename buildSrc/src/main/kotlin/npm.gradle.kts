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

tasks.named("compileJava").configure { dependsOn(tasks.yarn) }