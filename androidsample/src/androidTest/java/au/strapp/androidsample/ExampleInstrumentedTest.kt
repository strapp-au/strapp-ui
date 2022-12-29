package au.strapp.androidsample

import androidx.compose.material3.Text
import androidx.test.ext.junit.runners.AndroidJUnit4
import au.strapp.strappui.android.StrappComponent

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @get:Rule
    val component = StrappComponent(name = "Compose Text", group = "Compose Components")

    @Test
    fun composeTextDefault() {
        component.snapshot {
            Text(text = "Hello Strapp!")
        }
    }

}