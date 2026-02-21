package dev.bluesheep.xeiexporter

import net.minecraftforge.common.ForgeConfigSpec

object Config {
    private val BUILDER = ForgeConfigSpec.Builder()

    val DATABASE_HOST: ForgeConfigSpec.ConfigValue<String> = BUILDER
        .comment("Database host")
        .define("databaseUrl", "localhost")

    val DATABASE_PORT: ForgeConfigSpec.IntValue = BUILDER
        .comment("Database port")
        .defineInRange("databasePort", 5432, 0, 65535)

    val DATABASE_NAME: ForgeConfigSpec.ConfigValue<String> = BUILDER
        .comment("Database name")
        .define("databaseName", "postgres")

    val DATABASE_USER: ForgeConfigSpec.ConfigValue<String> = BUILDER
        .comment("Database user")
        .define("databaseUser", "xeiex")

    val DATABASE_PASSWORD: ForgeConfigSpec.ConfigValue<String> = BUILDER
        .comment("Database password")
        .define("databasePassword", "postgres")

    val SPEC: ForgeConfigSpec = BUILDER.build()
}
