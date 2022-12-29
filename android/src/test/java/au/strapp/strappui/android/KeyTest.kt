package so.strapp.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import au.strapp.strappui.android.StrappComponent
import org.junit.Rule
import org.junit.Test

class KeyTest {

    @get:Rule
    val strapp = StrappComponent(
        name = "Key",
        group = "Component"
    )

    @Composable
    fun Key() {
        Text("Key")
    }

    @Test
    fun default() {
        strapp.snapshot("Default") {
            Key()
        }
    }

}