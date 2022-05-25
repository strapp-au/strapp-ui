package au.strapp.strappplugin

import app.cash.paparazzi.gradle.PaparazziPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.ArtifactRepository
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
            addTestDependencies(project)
            PaparazziPlugin().apply(project)
//            project.pluginManager.apply("app.cash.paparazzi")
        }

//        project.tasks.all { test ->
//            if (test is Test)
//                test.systemProperties["paparazzi.test.rootDirectory"] = project.rootProject.rootDir.absolutePath + "/.strapp/snapshots/android"
//        }

    }

    private fun addTestDependencies(project: Project) {
        project.repositories.maven { repo ->
            repo.setUrl("https://jitpack.io")
//            repo.metadataSources { sources ->
//                sources.artifact()
//            }
        }
        project.configurations.getByName("testImplementation").dependencies.add(
            project.dependencies.create("com.github.strapp-au:strapp-ui:0.1.1")
        )
    }
}