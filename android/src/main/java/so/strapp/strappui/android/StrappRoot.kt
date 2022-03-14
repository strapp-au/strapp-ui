package so.strapp.strappui.android

import androidx.compose.runtime.Composable

class StrappRoot(
    val name: String,
    val themes: List<@Composable (content: @Composable () -> Unit) -> Unit>
) {

}