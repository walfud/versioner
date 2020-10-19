package com.walfud.versioner.version

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.max

@Service
class VersionService {
    @Autowired
    lateinit var versionMapper: VersionMapper

    /**
     * @return new value
     */
    @Transactional
    fun inc(id: String, partAdapter: PartAdapter): Int {
        val base = partAdapter.getBase()
        val sig = idBase2Sig(id, base)
        val value = partAdapter.getValue()
        val dbVersion = versionMapper.queryById(sig)
        return if (dbVersion == null) {
            val newValue = max(value, partAdapter.getMinValue())
            versionMapper.insert(sig, newValue)
            newValue
        } else {
            val newValue = max(value, dbVersion.value + 1)
            versionMapper.update(sig, newValue)
            newValue
        }
    }

    private fun idBase2Sig(id: String, base: String) = "$id:$base"
}

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

    open fun getMinValue(): Int = 0
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

    override fun getMinValue(): Int = 1
}