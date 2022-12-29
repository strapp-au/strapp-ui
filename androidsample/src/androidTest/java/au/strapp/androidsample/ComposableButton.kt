package au.strapp.androidsample

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import au.strapp.strappui.android.StrappComponent
import org.junit.Rule
import org.junit.Test

class ComposableButton {

    @get:Rule
    val component = StrappComponent(name = "Compose Button", group = "Compose Components")

    @Test
    fun default() {
        component.snapshot {
            Button(onClick = { /*TODO*/ }) {
                Text("Compose Button")
            }
        }
    }
}