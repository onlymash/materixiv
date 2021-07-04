package onlymash.materixiv.ui.base

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.kodein.di.DIAware
import org.kodein.di.android.x.closestDI

abstract class KodeinBottomSheetDialog : BottomSheetDialogFragment(), DIAware {
    final override val di by closestDI()
}