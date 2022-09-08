package so.strapp.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import org.junit.Rule
import org.junit.Test
import au.strapp.strappui.android.StrappTesting

class KeyTest {

    @get:Rule
    val strapp = StrappTesting(
        componentName = "Key"
    )

    @Composable
    fun Key() {
        Text("Key")
    }

    @Test
    fun default() {
        strapp.snap("Default") {
            Key()
        }
    }

}