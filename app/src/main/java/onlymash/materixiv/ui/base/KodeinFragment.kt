package onlymash.materixiv.ui.base

import androidx.fragment.app.Fragment
import org.kodein.di.DIAware
import org.kodein.di.android.x.di

abstract class KodeinFragment : Fragment(), DIAware {
    override val di by di()
}