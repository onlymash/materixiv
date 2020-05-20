package onlymash.materixiv.ui.base

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

abstract class KodeinActivity : BaseActivity(), KodeinAware {
    override val kodein: Kodein by kodein()
}