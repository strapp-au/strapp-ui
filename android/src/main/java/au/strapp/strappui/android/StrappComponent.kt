package au.strapp.strappui.android

import android.content.ContentProviderClient
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.os.RemoteException
import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.*
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.viewinterop.AndroidView
import androidx.test.annotation.ExperimentalTestApi
import androidx.test.core.graphics.writeToTestStorage
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.services.storage.TestStorage
import androidx.test.services.storage.TestStorageException
import au.strapp.strappui.shared.StrappConfigBuilder
import org.junit.rules.RunRules
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.io.BufferedInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.OutputStream
import java.util.*


@ExperimentalTestApi class StrappComponent(
    private val name: String,
    private val group: String
): TestRule {

    private val composeTestRule = createComposeRule()
    lateinit var testName: TestNames

    private val snapshotDir = "snapshots/android/images"


    private val contentResolver = InstrumentationRegistry.getInstrumentation().targetContext.contentResolver
    private val configUri = TestStorage.getOutputFileUri("strapp-output/config.json")

    fun snapshot(
        label: String = "Default",
        composable: @Composable () -> Unit
    ) {

        val fileName = toFileName(label, testName)

        composeTestRule.setContent {
            Box(Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .testTag("CaptureNode")
                ) {
                    composable()
                }
            }
        }
        composeTestRule.onNodeWithTag("CaptureNode")
            .captureToImage()
            .asAndroidBitmap()
            .writeToTestStorage("strapp-output/$snapshotDir/$fileName")

        val output = getOutputStream(configUri, contentResolver, true)?.close()
        val configInput = getInputStream(configUri, contentResolver)?.let {
            val contents = it.reader().readText()
            it.close()
            contents
        }

        updateConfig(StrappConfigBuilder().addSnapshot(
            name,
            group,
            label,
            "$snapshotDir/$fileName.png",
            configInput?: ""
        ))
    }

    fun <T: View> snapshot(label: String, viewFactory: (Context) -> T) {
        snapshot(label = label, composable = {
            AndroidView(
                modifier = Modifier.wrapContentSize(),
                factory = viewFactory,
                update = {}
            )
        })
    }

    fun snapshot(label: String, layout: Int, bind: (View) -> Unit = {}) {
        snapshot(label = label, composable = {
            AndroidView(
                factory = { context ->
                    LayoutInflater.from(context)
                        .inflate(layout, null)
                        .apply {
                            bind(this)
                        }
                },
                update = {}
            )
        })
    }

    private fun updateConfig(config: String) {
        getOutputStream(configUri, contentResolver)?.let {
            it.write(config.toByteArray())
            it.close()
        }
    }

    private fun toFileName(
        name: String,
        testName: TestNames,
        delimiter: String = "_"
    ): String {
        val formattedLabel = "$delimiter${name.lowercase(Locale.US).replace("\\s".toRegex(), delimiter)}"
        return "${testName.packageName}${delimiter}${testName.className}${delimiter}${testName.methodName}$formattedLabel"
    }

    private fun Description.toTestName(): TestNames {
        val fullQualifiedName = className
        val packageName = fullQualifiedName.substringBeforeLast('.', missingDelimiterValue = "")
        val className = fullQualifiedName.substringAfterLast('.')
        return TestNames(packageName, className, methodName)
    }

    data class TestNames (
        val packageName: String,
        val className: String,
        val methodName: String,
    )

    override fun apply(base: Statement, description: Description): Statement {
        testName = description.toTestName()
        return RunRules(base, listOf(composeTestRule), description)
    }

    /**
     * Gets the input stream for a given Uri.
     *
     * @param uri The Uri for which the InputStream is required.
     */
    @Throws(FileNotFoundException::class)
    fun getInputStream(uri: Uri, contentResolver: ContentResolver?): InputStream? {
        var providerClient: ContentProviderClient? = null
        return try {
            providerClient = contentResolver?.let { makeContentProviderClient(it, uri) }
            // Assignment to a variable is required. Do not inline.
            val pfd = providerClient?.openFile(uri, "r")
            // Buffered to improve performance.
            BufferedInputStream(ParcelFileDescriptor.AutoCloseInputStream(pfd))
        } catch (re: RemoteException) {
            throw TestStorageException("Unable to access content provider: $uri", re)
        } finally {
            providerClient?.release()
        }
    }

    /**
     * Gets the output stream for a given Uri.
     *
     *
     * The returned OutputStream is essentially a [java.io.FileOutputStream] which likely
     * should be buffered to avoid `UnbufferedIoViolation` when running under strict mode.
     *
     * @param uri The Uri for which the OutputStream is required.
     */
    @Throws(FileNotFoundException::class)
    fun getOutputStream(uri: Uri, contentResolver: ContentResolver?): OutputStream? {
        return getOutputStream(uri, contentResolver, false)
    }

    /**
     * Gets the output stream for a given Uri.
     *
     *
     * The returned OutputStream is essentially a [java.io.FileOutputStream] which likely
     * should be buffered to avoid `UnbufferedIoViolation` when running under strict mode.
     *
     * @param uri The Uri for which the OutputStream is required.
     * @param append if true, then the lines will be added to the end of the file rather than
     * overwriting.
     */
    @Throws(FileNotFoundException::class)
    fun getOutputStream(
        uri: Uri, contentResolver: ContentResolver?, append: Boolean
    ): OutputStream? {
        var providerClient: ContentProviderClient? = null
        return try {
            providerClient = contentResolver?.let { makeContentProviderClient(it, uri) }
            val mode = if (append) "wa" else "w"
            ParcelFileDescriptor.AutoCloseOutputStream(providerClient?.openFile(uri, mode))
        } catch (re: RemoteException) {
            throw TestStorageException("Unable to access content provider: $uri", re)
        } finally {
            providerClient?.release()
        }
    }

    private fun makeContentProviderClient(
        resolver: ContentResolver, uri: Uri
    ): ContentProviderClient {
        return resolver.acquireContentProviderClient(uri)
            ?: throw TestStorageException(
                String.format(
                    "No content provider registered for: %s. Are all test services apks installed?",
                    uri
                )
            )
    }
}