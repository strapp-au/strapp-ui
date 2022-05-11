package so.strapp.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import so.strapp.strappui.android.StrappTesting

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