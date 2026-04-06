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

    fun export(): Int {
        ExportUtil.mkdir(EXPORT_LANG_DIR)
        val minecraft = Minecraft.getInstance()

        val fallbackLanguage = Config.EXPORT_LANGUAGE_FALLBACK.get()
        val isFallbackAvailable = minecraft.languageManager.getLanguage(fallbackLanguage) != null
        if (!isFallbackAvailable) {
            minecraft.player?.sendSystemMessage(languageNotFoundComponent(fallbackLanguage))
        }

        val exportedLanguage = mutableListOf<String>()
        Config.EXPORT_LANGUAGES.get().forEach { langName ->
            if (minecraft.languageManager.getLanguage(langName) == null) {
                minecraft.player?.sendSystemMessage(languageNotFoundComponent(langName))
                return@forEach
            }

            val languageList = if (isFallbackAvailable && fallbackLanguage != langName) {
                listOf(fallbackLanguage, langName)
            } else {
                listOf(langName)
            }
            val lang = ClientLanguage.loadFrom(
                minecraft.resourceManager,
                languageList,
                false
            ).languageData.mapValues(::escape).toSortedMap()

            ExportUtil.saveExportFile(lang, EXPORT_LANG_DIR.resolve("$langName.json"))
            exportedLanguage.add(langName)
        }

        ExportUtil.saveExportFile(exportedLanguage, EXPORT_LANG_DIR.resolve("available.json"))
        return exportedLanguage.size
    }

    private fun escape(entry: Map.Entry<String, String>): String {
        return entry.value
            .replace("\\", "\\\\")
            .replace("{", "\\{")
            .replace("}", "\\}")
            .replace("@", "\\@")
            .replace("|", "\\|")
    }

    private fun languageNotFoundComponent(langName: String): Component {
        return Component.translatable("xeiexporter.language.not_found", langName)
            .withStyle(ChatFormatting.YELLOW)
    }
}