package au.strapp.strappplugin

import app.cash.paparazzi.gradle.PaparazziPlugin
//import app.cash.paparazzi.gradle.PaparazziPluginExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import java.io.File

@Suppress("unused")
class StrappPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        val rootDirectory = project.rootProject.rootDir.absolutePath
        val moduleDirectory = project.projectDir.absolutePath
        val snapshotDirectory = "$rootDirectory/.strapp/snapshots/android"

        project.afterEvaluate {
            require(project.plugins.hasPlugin("com.android.library")) {
                "The Android Gradle library plugin must be applied for Paparazzi to be configured."
            }
            project.tasks.withType(Test::class.java) { test ->
                test.systemProperties["strapp.test.project_root"] = rootDirectory
                test.systemProperties["strapp.test.module_root"] = moduleDirectory
            }
        }

        project.plugins.withId("com.android.library") {
            addTestDependencies(project)
            PaparazziPlugin().apply(project)
//            project.pluginManager.apply("app.cash.paparazzi")
//            (project.extensions.getByName("paparazzi") as PaparazziPluginExtension)
//                .snapshotRootDirectory.set(File(snapshotDirectory))
        }

    }

    private fun addTestDependencies(project: Project) {
//        project.repositories.maven { repo ->
//            repo.credentials {
//                it.username = "brenpearson"//findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
//                it.password = "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"//findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
//            }
//            repo.setUrl("https://maven.pkg.github.com/strapp-au/strapp-ui")
//            repo.name = "Strapp Github Packages"
////            repo.metadataSources { sources ->
////                sources.artifact()
////            }
//
//      }
        project.configurations.getByName("testImplementation").dependencies.add(
            project.dependencies.create("com.github.strapp-au:strapp-ui:22.9.3")
        )
    }
}