package dev.bluesheep.xeiexporter.exporter.resources

import dev.bluesheep.xeiexporter.Config
import dev.bluesheep.xeiexporter.XEIExporter.EXPORT_ASSETS_DIR
import dev.bluesheep.xeiexporter.exporter.ExportUtil
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.resources.language.ClientLanguage
import net.minecraft.network.chat.Component

object LanguageExporter {
    private val EXPORT_LANG_DIR = EXPORT_ASSETS_DIR.resolve("lang")

    fun export() {
        ExportUtil.mkdir(EXPORT_LANG_DIR)

        val minecraft = Minecraft.getInstance()
        Config.EXPORT_LANGUAGES.get().forEach { langName ->
            if (minecraft.languageManager.getLanguage(langName) == null) {
                minecraft.player?.sendSystemMessage(
                    Component.literal("⚠Language not found: $langName").withStyle(ChatFormatting.YELLOW)
                )
                return@forEach
            }

            val lang = ClientLanguage.loadFrom(
                minecraft.resourceManager,
                listOf(langName),
                false
            ).languageData.mapValues(::escape).toSortedMap()

            ExportUtil.saveExportFile(lang, EXPORT_LANG_DIR.resolve("$langName.json"))
        }
    }

    private fun escape(entry: Map.Entry<String, String>): String {
        return entry.value
            .replace("\\", "\\\\")
            .replace("{", "\\{")
            .replace("}", "\\}")
            .replace("@", "\\@")
            .replace("|", "\\|")
    }
}