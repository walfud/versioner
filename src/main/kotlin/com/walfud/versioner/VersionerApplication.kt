package com.walfud.versioner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VersionerApplication

fun main(args: Array<String>) {
    runApplication<VersionerApplication>(*args)
}
