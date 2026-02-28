package dev.bluesheep.xeiexporter.sql

import dev.bluesheep.xeiexporter.Config
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.exists
import org.postgresql.ds.PGSimpleDataSource

object DatabaseUtil {
    fun connect() {
        Database.connect(PGSimpleDataSource().apply {
            setUrl("jdbc:postgresql://${Config.DATABASE_HOST.get()}:${Config.DATABASE_PORT.get()}/${Config.DATABASE_NAME.get()}")
            user = Config.DATABASE_USER.get()
            password = Config.DATABASE_PASSWORD.get()
            reWriteBatchedInserts = true
        })
    }

    fun reset(table: Table) {
        if (table.exists()) {
            SchemaUtils.drop(table)
        }
        SchemaUtils.create(table)
    }
}