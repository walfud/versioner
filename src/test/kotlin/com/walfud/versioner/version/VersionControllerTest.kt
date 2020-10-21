package com.walfud.versioner.version

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(VersionController::class)
class VersionControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var versionService: VersionService

    @Test
    fun version() {
        val id = "com.walfud.versioner"
        val version = "1.2.3"
        val req = VersionRequest(
                id,
                version,
                "patch",
                "major.minor.patch"
        )

        val partAdapter = PatchPart(version)
        `when`(versionService.inc(id, partAdapter)).thenReturn(3)

        mockMvc.perform(
                post("/version")
                        .contentType("application/json")
                        .content(jacksonObjectMapper().writeValueAsString(req))
        )
                .andExpect(status().isOk)
                .andExpect(content().string("1.2.3"))
    }

    @Test
    fun versionLength3() {
        val id = "com.walfud.versioner"
        val version = "1.2.3"
        val req = VersionRequest(
                id,
                version,
                "patch",
                "major.minor.patch-3"
        )

        val partAdapter = PatchPart(version)
        `when`(versionService.inc(id, partAdapter)).thenReturn(3)

        mockMvc.perform(
                post("/version")
                        .contentType("application/json")
                        .content(jacksonObjectMapper().writeValueAsString(req))
        )
                .andExpect(status().isOk)
                .andExpect(content().string("1.2.003"))
    }
}