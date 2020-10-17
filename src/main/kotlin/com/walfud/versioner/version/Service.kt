package com.walfud.versioner.version

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.math.max

@Service
class VersionService {
    @Autowired
    lateinit var versionMapper: VersionMapper

    /**
     * @return new value
     */
    @Transaction
    fun inc(id: String, base: String, fromValue: Int): Int {
        val sig = idBase2Sig(id, base)
        val dbVersion = versionMapper.queryById(sig)
        return if (dbVersion == null) {
            versionMapper.insert(sig, fromValue)
            fromValue
        } else {
            val newValue = max(fromValue, dbVersion.value + 1)
            versionMapper.update(sig, newValue)
            newValue
        }
    }

    private fun idBase2Sig(id: String, base: String) = "$id:$base"
}