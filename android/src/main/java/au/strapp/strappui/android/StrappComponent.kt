package au.strapp.strappui.android

import au.strapp.strappui.shared.StrappConfigBuilder
import android.view.LayoutInflater
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.*
import app.cash.paparazzi.*
import com.android.ide.common.rendering.api.SessionParams
import com.android.resources.*
import com.google.gson.Gson
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.File
import java.util.*

enum class ComponentLayout {
    WRAP_CONTENT,
    MATCH_PARENT
}

class StrappComponent(
    private val name: String,
    private val group: String,
    layout: ComponentLayout = ComponentLayout.WRAP_CONTENT
): TestRule {

    private val paparazzi = Paparazzi(
//        deviceConfig = DeviceConfig.PIXEL_5,
        deviceConfig = DeviceConfig(
            screenHeight = if (layout == ComponentLayout.WRAP_CONTENT) 1 else 2560,
            screenWidth = 1440,
            xdpi = 534,
            ydpi = 534,
            orientation = ScreenOrientation.PORTRAIT,
            density = Density.DPI_560,
            ratio = ScreenRatio.NOTLONG,
            size = ScreenSize.NORMAL,
            keyboard = Keyboard.NOKEY,
            touchScreen = TouchScreen.FINGER,
            keyboardState = KeyboardState.SOFT,
            softButtons = false,
            navigation = Navigation.NONAV,
            released = "October 15, 2020",
        ),
        theme = "android:Theme.Material.Light.NoActionBar.Fullscreen",
        renderingMode = if (layout == ComponentLayout.WRAP_CONTENT) SessionParams.RenderingMode.V_SCROLL else SessionParams.RenderingMode.SHRINK,
        appCompatEnabled = true
//        snapshotRootDirectory = File(BuildConfig.PROJECT_DIR, ".strapp/snaps/android")
    )

    lateinit var testName: TestName

    fun snapshot(
        label: String = "Default",
        layout: Int,
        bind: (view: View) -> Unit = {}
    ) {
        snapView(label, layout, bind)
    }

    private fun getInflater(): LayoutInflater {
        return paparazzi.layoutInflater
    }

    private val context get() = paparazzi.context

    private fun snapView(label: String, layout: Int, bind: (view: View) -> Unit = {}) {
        paparazzi.inflate<View>(layout).let { root ->
            bind(root)
            snapshot(label, root)
        }
    }

    fun snapshot(
        label: String = "Default",
        composable: @Composable () -> Unit
    ) {
//        if (root.themes.isNotEmpty()) {
//            root.themes.forEach {
//                snapView(label,
//                    { it(content = view) }
//                )
//            }
//        } else {
//            paparazzi.snapshot(label, composable)
//        }

//        val fileName =
//            "${componentName}_${label}".lowercase(Locale.US).replace("\\s".toRegex(), "_")
        paparazzi.snapshot(
            name = label,
            composable = composable
        )

        val fileName = toFileName(label, testName, extension = "png")
        val paparazziFile = File("${paparazziSnapshotDir}/${fileName}")
        val strappFile = File("${snapDir}/${fileName}")
        paparazziFile.copyTo(strappFile)
        paparazziFile.delete()

        updateConfig(StrappConfigBuilder().addSnapshot(
            name,
            group,
            label,
            strappFile.absolutePath,
            config
        ))
//        updateConfig(StrappConfig(
//            components = StrappComponents(
//                ios = c.components.ios,
//                android = arrayListOf<StrappComponent>().apply {
//                    this.addAll(c.components.android)
//                    val component = this.find {
//                        it.name == componentName
//                    }
//                    this.removeIf { it.name == componentName }
//                    this.add(StrappComponent(
//                        name = componentName,
//                        snaps = arrayListOf<StrappSnap>().apply {
//                            component?.snaps?.let { this.addAll(it) }
//                            this.removeIf { it.label == label }
//                            this.add(
//                                0,
//                                StrappSnap(
//                                    label = label,
//                                    snap = "$snapDir/$fileName"
//                                )
//                            )
//                        }
//                    ))
//                    this.sortBy { it.name }
//                }
//            )
//        ))
    }

    fun snapshot(label: String, view: View) {
//        val fileName =
//            "${componentName}_${label}".lowercase(Locale.US).replace("\\s".toRegex(), "_")

        paparazzi.snapshot(
            view = view,
            name = label
        )
        val fileName = toFileName(label, testName, extension = "png")
        val paparazziFile = File("${paparazziSnapshotDir}/${fileName}")
        val strappFile = File("${snapDir}/${fileName}")
        strappFile.delete()
        paparazziFile.copyTo(strappFile)
        paparazziFile.delete()

        updateConfig(StrappConfigBuilder().addSnapshot(
            name,
            group,
            label,
            strappFile.absolutePath,
            config
        ))
//        updateConfig(StrappConfig(
//            components = StrappComponents(
//                ios = c.components.ios,
//                android = arrayListOf<StrappComponent>().apply {
//                    this.addAll(c.components.android)
//                    val component = this.find {
//                        it.name == componentName
//                    }
//                    this.removeIf { it.name == componentName }
//                    this.add(StrappComponent(
//                        name = componentName,
//                        snaps = arrayListOf<StrappSnap>().apply {
//                            component?.snaps?.let { this.addAll(it) }
//                            this.removeIf { it.label == label }
//                            this.add(
//                                0,
//                                StrappSnap(
//                                    label = label,
//                                    snap =
//                                )
//                            )
//                        }
//                    ))
//                    this.sortBy { it.name }
//                }
//            )
//        ))
    }

    private data class StrappConfig(
        val components: StrappComponents
    )

    private data class StrappComponents(
        val android: List<StrappComponent>,
        val ios: List<StrappComponent>
    )

    private data class Component(
        val name: String,
        val snaps: List<StrappSnap>
    )

    private data class StrappSnap(
        val label: String,
        val snap: String
    )

    private val gson = Gson()

    private val projectDir = System.getProperty("strapp.test.project_root")
    private val moduleDir = System.getProperty("strapp.test.module_root")
    private val snapDir = "${projectDir}/.strapp/snapshots/android/images"
    private val configPath = File("$projectDir/.strapp/config.json")
    private val paparazziSnapshotDir = "${moduleDir}/src/test/snapshots/images"

    init {
        configPath.parentFile?.mkdirs()
        configPath.createNewFile()
    }

    private val config: String get() =
        configPath.let {
            it.parentFile?.mkdirs()
            it.createNewFile()
            it.readText()
        }
    private fun updateConfig(config: String) {
        configPath.apply {
            this.parentFile?.mkdirs()
            this.createNewFile()
            this.writeText(config)
        }
    }

    private fun toFileName(
        name: String,
        testName: TestName,
        delimiter: String = "_",
        extension: String
    ): String {
        val formattedLabel = if (name != null) {
            "$delimiter${name.toLowerCase(Locale.US).replace("\\s".toRegex(), delimiter)}"
        } else {
            ""
        }
        return "${testName.packageName}${delimiter}${testName.className}${delimiter}${testName.methodName}$formattedLabel.$extension"
    }

    private fun Description.toTestName(): TestName {
        val fullQualifiedName = className
        val packageName = fullQualifiedName.substringBeforeLast('.', missingDelimiterValue = "")
        val className = fullQualifiedName.substringAfterLast('.')
        return TestName(packageName, className, methodName)
    }

    fun prepare(description: Description) {
        paparazzi.prepare(description)
    }

    fun close() {
        paparazzi.close()
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    paparazzi.prepare(description)
                    testName = description.toTestName()
                    base.evaluate()
                } finally {
                    paparazzi.close()

                    val paparazziImagesDir = File(paparazziSnapshotDir)
                    if (paparazziImagesDir.listFiles()?.isEmpty() == true) {
                        paparazziImagesDir.parentFile?.deleteRecursively()
                    }
                }
            }
        }
    }
}