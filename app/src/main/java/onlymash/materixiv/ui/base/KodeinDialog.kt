package onlymash.materixiv.ui.base

import androidx.fragment.app.DialogFragment
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein

abstract class KodeinDialog : DialogFragment(), KodeinAware {
    override val kodein: Kodein by kodein()
}