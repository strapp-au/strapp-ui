package au.strapp.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class StrappConfigBuilder {

    fun addSnapshot(
        componentName: String,
        componentGroup: String,
        snapshotLabel: String,
        snapshotPath: String,
        configString: String
    ): String {

        val c = if (configString.isNotEmpty()) Json.decodeFromString<StrappConfig>(configString).components else listOf()
        val components = arrayListOf<StrappComponent>().apply {
            this.addAll(c)
            val component = this.find {
                it.name == componentName
            }
            this.removeAll { it.name == component?.name }
            this.add(StrappComponent(
                name = componentName,
                group = componentGroup,
                snapshots = arrayListOf<StrappSnapshot>().apply {
                    component?.snapshots?.let { this.addAll(it) }
                    this.removeAll { it.label == snapshotLabel }
                    this.add(0,
                        StrappSnapshot(
                            label = snapshotLabel,
                            type = "png",
                            src = snapshotPath
                        )
                    )
                }
            ))
            this.sortBy { it.name }
        }
        return Json.encodeToString(
            StrappConfig(
            components = components
        )
        ).replace("\\","")
    }

    @Serializable
    data class StrappConfig(
        val components: List<StrappComponent>
    )

    @Serializable
    data class StrappComponent(
        val name: String,
        val group: String = "",
        val snapshots: List<StrappSnapshot>
    )

    @Serializable
    data class StrappSnapshot(
        val label: String,
        val type: String,
        val src: String
    )
}