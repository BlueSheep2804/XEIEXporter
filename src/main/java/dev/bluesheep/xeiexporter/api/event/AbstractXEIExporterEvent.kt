package dev.bluesheep.xeiexporter.api.event

import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.fml.event.IModBusEvent

abstract class AbstractXEIExporterEvent : Event(), IModBusEvent {
    override fun isCancelable(): Boolean {
        return false
    }

    override fun hasResult(): Boolean {
        return false
    }
}