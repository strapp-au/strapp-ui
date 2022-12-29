package au.strapp.strappplugin

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class StrappPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        project.tasks.create("strappManagedDeviceRecord") {
            it.dependsOn("strappDeviceDebugAndroidTest")
            it.doLast {
                val strappBuild = project.rootDir.resolve(".strapp/")
                if (!strappBuild.exists()) strappBuild.mkdirs()
                val output = project.projectDir
                    .resolve("build")
                    .resolve("outputs")
                    .resolve("managed_device_android_test_additional_output")
                    .resolve("strappDevice")
                    .resolve("strapp-output")
                output.copyRecursively(strappBuild, overwrite = true)
            }
        }

        project.tasks.create("strappConnectedDeviceRecord") {
            it.dependsOn("connectedDebugAndroidTest")
            it.doLast {
                val strappBuild = project.rootDir.resolve(".strapp/")
                if (!strappBuild.exists()) strappBuild.mkdirs()
                project.projectDir
                    .resolve("build")
                    .resolve("outputs")
                    .resolve("connected_android_test_additional_output")
                    .resolve("debugAndroidTest")
                    .resolve("connected")
                    .listFiles()?.forEach { deviceFolder ->
                        deviceFolder.listFiles()?.firstOrNull { outputFile ->
                            outputFile.name == "strapp-output"
                        }?.copyRecursively(strappBuild, overwrite = true)
                    }
            }
        }

        project.configurations.getByName("androidTestImplementation").dependencies.add(
            project.dependencies.create("com.github.strapp-au:strapp-ui:22.9.3")
        )

    }
}