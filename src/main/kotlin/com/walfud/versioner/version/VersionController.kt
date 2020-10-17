package com.walfud.versioner.version

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class VersionController {

    @Autowired
    lateinit var versionService: VersionService

    @PostMapping("/version")
    fun gen(@RequestBody body: VersionRequest): String {
        val partAdapter: PartAdapter = when (body.part) {
            "major" -> MajorPart(body.current, body.part)
            "minor" -> MinorPart(body.current, body.part)
            "patch" -> PatchPart(body.current, body.part)
            "build" -> BuildPart(body.current, body.part)
            else -> throw RuntimeException("`inc` must be [major|minor|patch|build]")
        }

        val base = partAdapter.getBase()
        val value = partAdapter.getValue()
        val newValue = versionService.inc(body.id, base, value)

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

abstract class PartAdapter {
    val major: Int
    val minor: Int
    val patch: Int
    val build: Int

    constructor(ver: String, part: String) {
        val parts = ver.split(".").map { it.toInt() }
        major = if (parts.size > 0) parts[0] else 0
        minor = if (parts.size > 1) parts[1] else 0
        patch = if (parts.size > 2) parts[2] else 0
        build = if (parts.size > 3) parts[3] else 0
    }

    protected constructor(major: Int, minor: Int, patch: Int, build: Int) {
        this.major = major
        this.minor = minor
        this.patch = patch
        this.build = build
    }

    abstract fun getBase(): String
    abstract fun getValue(): Int
    abstract fun fork(newValue: Int): PartAdapter
}

class MajorPart : PartAdapter {
    constructor(ver: String, part: String) : super(ver, part)
    private constructor(major: Int, minor: Int, patch: Int, build: Int) : super(major, minor, patch, build)

    override fun getBase() = ".$minor.$patch.$build"
    override fun getValue() = major
    override fun fork(newValue: Int): PartAdapter = MajorPart(newValue, minor, patch, build)
}

class MinorPart : PartAdapter {
    constructor(ver: String, part: String) : super(ver, part)
    private constructor(major: Int, minor: Int, patch: Int, build: Int) : super(major, minor, patch, build)

    override fun getBase() = "$major..$patch.$build"
    override fun getValue() = minor
    override fun fork(newValue: Int): PartAdapter = MinorPart(major, newValue, patch, build)
}

class PatchPart : PartAdapter {
    constructor(ver: String, part: String) : super(ver, part)
    private constructor(major: Int, minor: Int, patch: Int, build: Int) : super(major, minor, patch, build)

    override fun getBase() = "$major.$minor..$build"
    override fun getValue() = patch
    override fun fork(newValue: Int): PartAdapter = PatchPart(major, minor, newValue, build)
}

class BuildPart : PartAdapter {
    constructor(ver: String, part: String) : super(ver, part)
    private constructor(major: Int, minor: Int, patch: Int, build: Int) : super(major, minor, patch, build)

    override fun getBase() = "$major.$minor.$patch."
    override fun getValue() = build
    override fun fork(newValue: Int): PartAdapter = BuildPart(major, minor, patch, newValue)
}