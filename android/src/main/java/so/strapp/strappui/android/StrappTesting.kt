package so.strapp.strappui.android

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.decapitalize
import androidx.compose.ui.unit.dp
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.android.resources.*
import com.google.gson.Gson
import org.junit.Rule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File
import java.util.*
import kotlin.reflect.KFunction

class StrappTesting(
    private val componentName: String
): TestRule {

    private val paparazzi = Paparazzi(
//        deviceConfig = DeviceConfig.PIXEL_5,
        deviceConfig = app.cash.paparazzi.DeviceConfig(
            screenHeight = 2560,
            screenWidth = 1440,
            xdpi = 534,
            ydpi = 534,
            orientation = com.android.resources.ScreenOrientation.PORTRAIT,
            density = com.android.resources.Density.DPI_560,
            ratio = com.android.resources.ScreenRatio.NOTLONG,
            size = com.android.resources.ScreenSize.NORMAL,
            keyboard = com.android.resources.Keyboard.NOKEY,
            touchScreen = com.android.resources.TouchScreen.FINGER,
            keyboardState = com.android.resources.KeyboardState.SOFT,
            softButtons = false,
            navigation = com.android.resources.Navigation.NONAV,
            released = "October 15, 2020"
        ),
        theme = "android:Theme.Material.Light.NoActionBar.Fullscreen",
        renderingMode = SessionParams.RenderingMode.SHRINK,
        snapshotRootDirectory = File(BuildConfig.PROJECT_DIR, ".strapp/snaps/android")
    )

    @Composable
    fun TestView(view: @Composable (() -> Unit)?) {
        view?.invoke()
    }

    fun snap(
        label: String = "Default",
        view: @Composable () -> Unit
    ) {
        paparazzi.inflate<SnapshotHostView>(R.layout.frame).let { root ->
            root.findViewById<ComposeView>(R.id.compose_frame).apply {
                setContent {
                    TestView {
                        view()
                    }
                }
            }
            val fileName = "${componentName}_${label}".lowercase(Locale.US).replace("\\s".toRegex(), "_")
            paparazzi.snapshot(
                view = root,
                name = fileName
            )
            val c = config
            updateConfig(StrappConfig(
                components = StrappComponents(
                    ios = c.components.ios,
                    android = arrayListOf<StrappComponent>().apply {
                        this.addAll(c.components.android)
                        val component = this.find {
                            it.name == componentName
                        }
                        this.removeIf { it.name == componentName }
                        this.add(StrappComponent(
                            name = componentName,
                            snaps = arrayListOf<StrappSnap>().apply {
                                component?.snaps?.let { this.addAll(it) }
                                this.removeIf { it.label == label }
                                this.add(
                                    StrappSnap(
                                        label = label,
                                        snap = "$snapDir/$fileName.png"
                                    )
                                )
                            }
                        ))
                        this.sortBy { it.name }
                    }
                )
            ))
        }
    }

    data class StrappConfig(
        val components: StrappComponents
    )

    data class StrappComponents(
        val android: List<StrappComponent>,
        val ios: List<StrappComponent>
    )

    data class StrappComponent(
        val name: String,
        val snaps: List<StrappSnap>
    )

    data class StrappSnap(
        val label: String,
        val snap: String
    )

    private val gson = Gson()

    private val projectDir = BuildConfig.PROJECT_DIR
    private val snapDir = "${projectDir}/.strapp/snaps/android"
    private val configPath = "$projectDir/.strapp/config.json"

    private val config: StrappConfig get() =
        gson.fromJson(File(configPath).let {
            it.parentFile?.mkdirs()
            it.createNewFile()
            it.readText()
        }, StrappConfig::class.java)?: StrappConfig(
            components = StrappComponents(
                android = listOf(),
                ios = listOf()
            )
        )
    private fun updateConfig(config: StrappConfig) {
        File(configPath).apply {
            this.parentFile?.mkdirs()
            this.createNewFile()
            this.writeText(gson.toJson(config))
        }
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    paparazzi.prepare(description)
                    base.evaluate()
                } finally {
                    paparazzi.close()
                }
            }
        }
    }
}