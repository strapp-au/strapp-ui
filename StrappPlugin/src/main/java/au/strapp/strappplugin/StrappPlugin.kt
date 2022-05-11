package au.strapp.strappplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

@Suppress("unused")
class StrappPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.afterEvaluate {
            require(project.plugins.hasPlugin("com.android.library")) {
                "The Android Gradle library plugin must be applied for Paparazzi to be configured."
            }
        }

        project.plugins.withId("com.android.library") {
            project.pluginManager.apply("app.cash.paparazzi")
        }

//        project.tasks.all { test ->
//            if (test is Test)
//                test.systemProperties["paparazzi.test.rootDirectory"] = project.rootProject.rootDir.absolutePath + "/.strapp/snapshots/android"
//        }

    }
}