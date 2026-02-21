package dev.bluesheep.xeiexporter.sql

import net.minecraft.world.item.Rarity
import org.jetbrains.exposed.v1.core.Table

object ItemsTable : Table("items") {
    val id = integer("id").autoIncrement()
    val namespace = text("namespace")
    val name = text("name")
    val descriptionId = text("descriptionId")
    val maxDamage = integer("maxDamage")
    val rarity = text("rarity").transform(
        wrap = {
            Rarity.valueOf(it)
        },
        unwrap = {
            it.name
        }
    )
    val clazz = text("class")

    override val primaryKey = PrimaryKey(id)
}