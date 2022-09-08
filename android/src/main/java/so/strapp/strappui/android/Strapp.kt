package so.strapp.strappui.android

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class Strapp (
    val name: String,
    val themes: List<@Composable (content: @Composable () -> Unit) -> Unit>
) {

    fun <T> composable(
        componentName: String,
        group: String,
        view: @Composable (props: T) -> Unit
    ) = ComposableStrappExecutor(
        strapp = this,
        componentName = componentName,
        group = group,
        view = view
    )

    fun <T> view(
        componentName: String,
        group: String,
        view: (props: T) -> View
    ) = ViewStrappExecutor(
        strapp = this,
        componentName = componentName,
        group = group,
        view = view
    )
}

class ComposableStrappExecutor <T> (
    val strapp: Strapp,
    val componentName: String,
    val group: String,
    val view: @Composable (props: T) -> Unit,
): TestRule {
    private val s = StrappComponent(
        root = strapp,
        componentName = componentName,
        group = group
    )

    fun snap(label: String, props: T) {
        s.snapshot(label) {
            view(props)
        }
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
    val group: String,
    val view: (props: T) -> View,
): TestRule {
    private val s = StrappComponent(
        root = strapp,
        componentName = componentName,
        group = group
    )

    fun snap(label: String, props: T) {
        s.snapshot(label, view(props))
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