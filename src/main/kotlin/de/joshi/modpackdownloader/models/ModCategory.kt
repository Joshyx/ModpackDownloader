package de.joshi.modpackdownloader.models

enum class ModCategory {
    MOD,
    RESOURCE_PACK,
    SHADER_PACK,
    MOD_PACK;

    fun getFolderName(): String {
        return when (this) {
            MOD -> "mods"
            RESOURCE_PACK -> "resourcepacks"
            SHADER_PACK -> "shaderpacks"
            MOD_PACK -> "modpacks"
        }
    }

    fun getName(): String {
        return when (this) {
            MOD -> "mod"
            RESOURCE_PACK -> "resource pack"
            SHADER_PACK -> "shader pack"
            MOD_PACK -> "mod pack"
        }
    }
}