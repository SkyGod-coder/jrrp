plugins {
    val kotlinVersion = "1.4.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.6.6"
}

group = "SkyGod"
version = "1.2"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public") // 阿里云国内代理仓库
    mavenCentral()
}
dependencies {
    implementation(fileTree(mapOf("dir" to "libs","include" to listOf("*.jar"))))
}