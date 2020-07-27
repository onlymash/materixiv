package onlymash.materixiv.ui.base

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.kodein.di.DIAware
import org.kodein.di.android.x.di

abstract class KodeinBottomSheetDialog : BottomSheetDialogFragment(), DIAware {
    final override val di by di()
}