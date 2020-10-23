package com.walfud.versioner.version

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class VersionController @Autowired constructor(
        val versionService: VersionService
) {

    @PostMapping(
            value = ["/version"],
            consumes = ["application/json"],
            produces = ["text/plain;charset=utf-8"]
    )
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

@Schema
data class VersionRequest(
        @Schema(required = true, example = "com.walfud.versioner")
        val id: String,
        @Schema(required = true, example = "1.3.4.0")
        val current: String,
        @Schema(required = true, example = "build", allowableValues = ["major", "minor", "patch", "build"])
        val part: String,
        @Schema(required = true, example = "major.minor.patch.build")
        val ret: String,
)