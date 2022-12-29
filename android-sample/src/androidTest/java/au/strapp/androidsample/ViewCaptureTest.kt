package au.strapp.androidsample

import android.widget.Button
import androidx.test.ext.junit.runners.AndroidJUnit4
import au.strapp.strappui.android.StrappComponent
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ViewCaptureTest {

    @get:Rule
    val component = StrappComponent(name = "Android View Button", group = "Android View")

    @Test
    fun viewButtonDefault() {
        component.snapshot(label = "Default", layout = R.layout.button)
    }

    @Test
    fun viewButtonOtherText() {
        component.snapshot(label = "Another variant", layout = R.layout.button) {
            it.findViewById<Button>(R.id.button).text = "Some other"
        }
    }
}