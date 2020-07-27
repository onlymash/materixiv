package onlymash.materixiv.ui.base

import org.kodein.di.DIAware
import org.kodein.di.android.di

abstract class KodeinActivity : BaseActivity(), DIAware {
    final override val di by di()
}