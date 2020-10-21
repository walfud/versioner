package com.walfud.versioner.version

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean

@WebMvcTest(VersionService::class)
class VersionServiceTest {

    @MockBean
    lateinit var versionMapper: VersionMapper

    /**
     * 1.2.3 -> 1.2.3
     */
    @Test
    fun incPatchNotExist() {
        val version = "1.2.3"
        val id = "com.walfud.versioner"
        val partAdapter = PatchPart(version)
        val sig = "$id:${partAdapter.getBase()}"

        `when`(versionMapper.queryById(sig)).thenReturn(null)

        val versionService = VersionService(versionMapper)
        val ret = versionService.inc(id, partAdapter)
        assertThat(ret).isEqualTo(3)
    }

    /**
     * 1.2.3 -> 1.2.4
     */
    @Test
    fun incPatchExist() {
        val version = "1.2.3"
        val id = "com.walfud.versioner"
        val partAdapter = PatchPart(version)
        val sig = "$id:${partAdapter.getBase()}"

        `when`(versionMapper.queryById(sig)).thenReturn(DbApp(sig, 3))

        val versionService = VersionService(versionMapper)
        val ret = versionService.inc(id, partAdapter)
        assertThat(ret).isEqualTo(4)
    }

    /**
     * 1.2.3 -> 1.2.3.1
     */
    @Test
    fun incBuild() {
        val version = "1.2.3"
        val id = "com.walfud.versioner"
        val partAdapter = BuildPart(version)
        val sig = "$id:${partAdapter.getBase()}"

        `when`(versionMapper.queryById(sig)).thenReturn(null)

        val versionService = VersionService(versionMapper)
        val ret = versionService.inc(id, partAdapter)
        assertThat(ret).isEqualTo(1)
    }

}