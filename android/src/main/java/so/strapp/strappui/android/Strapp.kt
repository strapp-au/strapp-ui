package so.strapp.strappui.android

import android.view.View
import androidx.compose.runtime.Composable
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class Strapp (
    val name: String,
    val themes: List<@Composable (content: @Composable () -> Unit) -> Unit>
) {

    fun <T> composable(
        componentName: String,
        view: @Composable (props: T) -> Unit
    ) = ComposableStrappExecutor(
        strapp = this,
        componentName = componentName,
        view = view
    )

    fun <T> view(
        componentName: String,
        view: (props: T) -> View
    ) = ViewStrappExecutor(
        strapp = this,
        componentName = componentName,
        view = view
    )
}

class ComposableStrappExecutor <T> (
    val strapp: Strapp,
    val componentName: String,
    val view: @Composable (props: T) -> Unit,
): TestRule {
    private val s = StrappTesting(
        root = strapp,
        componentName = componentName
    )

    fun snap(label: String, props: T) {
//        s.snap(label) {
//            view(props)
//        }
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    s.prepare(description)
                    base.evaluate()
                } finally {
                    s.close()
                }
            }
        }
    }
}

class ViewStrappExecutor <T> (
    val strapp: Strapp,
    val componentName: String,
    val view: (props: T) -> View,
): TestRule {
    private val s = StrappTesting(
        root = strapp,
        componentName = componentName
    )

    fun snap(label: String, props: T) {
        s.snap(label, view(props))
    }

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    s.prepare(description)
                    base.evaluate()
                } finally {
                    s.close()
                }
            }
        }
    }
}