package dev.bluesheep.xeiexporter.exporter

import dev.bluesheep.xeiexporter.sql.DatabaseUtil
import dev.bluesheep.xeiexporter.sql.FluidsTable
import net.minecraftforge.registries.ForgeRegistries
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object FluidExporter {
    fun export() {
        val fluid = ForgeRegistries.FLUIDS.entries

        transaction {
            DatabaseUtil.reset(FluidsTable)

            FluidsTable.batchInsert(fluid) { fluidEntry ->
                val fluidId = fluidEntry.key.location()
                val fluidType = fluidEntry.value.fluidType
                this[FluidsTable.namespace] = fluidId.namespace
                this[FluidsTable.name] = fluidId.path
                this[FluidsTable.descriptionId] = fluidType.descriptionId
                this[FluidsTable.temperature] = fluidType.temperature
            }
        }
    }
}