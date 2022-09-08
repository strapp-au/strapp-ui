package au.strapp.strappui.android

//import android.content.Context
//import android.util.AttributeSet
//import android.view.View
//import android.widget.FrameLayout
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.LifecycleEventObserver
//import androidx.lifecycle.LifecycleObserver
//import androidx.lifecycle.ViewTreeLifecycleOwner
//import androidx.savedstate.SavedStateRegistry
//import androidx.savedstate.SavedStateRegistryController
//import androidx.savedstate.SavedStateRegistryOwner
//import androidx.savedstate.ViewTreeSavedStateRegistryOwner

// View Hacks
//class SnapshotHostView @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null
//) : FrameLayout(context, attrs) {
//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        val parentView = parent as? View
//        parentView?.let {
//            val treeLifecycle = SimulatedLifecycle(state = Lifecycle.State.CREATED, simulateObserver = true)
//            ViewTreeLifecycleOwner.set(it) { treeLifecycle }
//            ViewTreeSavedStateRegistryOwner.set(it, SimulatedSavedStateOwner(SimulatedLifecycle()))
//        }
//    }
//}


//internal class SimulatedSavedStateOwner(private val lifecycle: Lifecycle) : SavedStateRegistryOwner {
//    override fun getLifecycle() = lifecycle
//
//    override fun getSavedStateRegistry(): SavedStateRegistry =
//        SavedStateRegistryController.create(this)
//            .apply { performRestore(null) }
//            .savedStateRegistry
//}
//
//internal class SimulatedLifecycle(
//    private val state: State = State.INITIALIZED,
//    private val simulateObserver: Boolean = false
//) : Lifecycle() {
//
//    override fun addObserver(observer: LifecycleObserver) {
//        if (simulateObserver) {
//            val eventObserver = observer as? LifecycleEventObserver
//            eventObserver?.onStateChanged({ this }, Event.ON_CREATE)
//        }
//    }
//
//    override fun removeObserver(observer: LifecycleObserver) = Unit
//
//    override fun getCurrentState() = state
//}