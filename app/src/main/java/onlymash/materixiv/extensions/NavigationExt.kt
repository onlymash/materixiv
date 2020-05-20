package onlymash.materixiv.extensions

import android.annotation.SuppressLint
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun BottomNavigationView.setup(navController: NavController) {

    setOnNavigationItemSelectedListener { menuItem ->
        if (menuItem.itemId == navController.currentDestination?.id) {
            true
        } else {
            onNavDestinationSelected(menuItem.itemId, navController)
        }
    }
}

@SuppressLint("RestrictedApi")
private fun onNavDestinationSelected(
    @IdRes itemId: Int,
    navController: NavController): Boolean {

    val options = NavOptions.Builder()
        .setLaunchSingleTop(true)
        .setEnterAnim(R.anim.nav_default_enter_anim)
        .setExitAnim(R.anim.nav_default_exit_anim)
        .setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
        .setPopExitAnim(R.anim.nav_default_pop_exit_anim)
        .build()

    return try {
        val index = navController.backStack.indexOfFirst { it.destination.id == itemId }
        if (index >= 0) {
            navController.popBackStack(itemId, false)
        } else {
            navController.navigate(itemId, null, options)
        }
        true
    } catch (_: IllegalArgumentException) {
        false
    } catch (_: IllegalStateException) {
        false
    }
}

fun FragmentActivity.findNavController(@IdRes viewId: Int) =
    (supportFragmentManager.findFragmentById(viewId) as? NavHostFragment)?.navController
        ?: Navigation.findNavController(this, viewId)