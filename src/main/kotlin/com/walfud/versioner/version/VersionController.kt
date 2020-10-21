package com.walfud.versioner.version

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class VersionController @Autowired constructor(
        val versionService: VersionService
) {

    @PostMapping("/version")
    fun gen(@RequestBody body: VersionRequest): String {
        val partAdapter: PartAdapter = when (body.part) {
            "major" -> MajorPart(body.current)
            "minor" -> MinorPart(body.current)
            "patch" -> PatchPart(body.current)
            "build" -> BuildPart(body.current)
            else -> throw RuntimeException("`inc` must be [major|minor|patch|build]")
        }

        val newValue = versionService.inc(body.id, partAdapter)

        val newPartAdapter = partAdapter.fork(newValue)
        return body.ret
                .split(".")
                .map { it ->
                    val nameAndLen = it.split("-")
                    val name = nameAndLen[0]
                    val len = if (nameAndLen.size > 1) nameAndLen[1].toInt() else 0

                    return@map when (name) {
                        "major" -> newPartAdapter.major
                        "minor" -> newPartAdapter.minor
                        "patch" -> newPartAdapter.patch
                        "build" -> newPartAdapter.build
                        else -> throw RuntimeException("`ret` must be ([major|minor|patch|build]\\.+)")
                    }
                            .toString()
                            .padStart(len, '0')
                }.joinToString(".")
    }

}

data class VersionRequest(
        val id: String,         // xxxx
        val current: String,    // 1.3.4.0
        val part: String,       // major/minor/patch/build
        val ret: String         // major.minor.patch.build
)