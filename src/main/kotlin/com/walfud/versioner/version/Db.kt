package com.walfud.versioner.version

import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Component
import java.sql.Timestamp

@Component
@Mapper
interface VersionMapper {

    @Select("""
        SELECT *
        FROM `version`
        WHERE id = #{id}
    """)
    fun queryById(id: String): DbApp?

    @Insert("""
        INSERT INTO `version`
        (id, value) 
        VALUES 
        (#{id}, #{value})
    """)
    fun insert(id: String, value: Int)

    @Update("""
        UPDATE `version`
        SET value = #{value}
        WHERE id = #{id}
    """)
    fun update(id: String, value: Int)

}

data class DbApp(
        var id: String,

        var value: Int,

        var createTime: Timestamp? = null,
        var updateTime: Timestamp? = null,
)