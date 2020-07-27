package onlymash.materixiv.ui.base

import androidx.appcompat.app.AppCompatDialogFragment
import org.kodein.di.DIAware
import org.kodein.di.android.x.di

abstract class KodeinDialog : AppCompatDialogFragment(), DIAware {
    override val di by di()
}