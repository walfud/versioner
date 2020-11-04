package com.walfud.versioner.misc

import com.walfud.versioner.version.VersionController
import com.walfud.versioner.version.VersionService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class MiscController @Autowired constructor(
        val versionService: VersionService
) {
    private val logger = LoggerFactory.getLogger(VersionController::class.java)

    @GetMapping(
            value = ["/health"],
            consumes = ["*/*"],
            produces = ["application/text;charset=utf-8"]
    )
    fun health(): String {
        logger.debug("health")
        versionService.versionMapper.queryAnyOne()
        return "ok"
    }
}