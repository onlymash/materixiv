package onlymash.materixiv.ui.module.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.takisoft.preferencex.SimpleMenuPreference
import onlymash.materixiv.R
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.app.getValue
import onlymash.materixiv.app.setValue
import onlymash.materixiv.extensions.getPersistedUri
import onlymash.materixiv.extensions.toDecodedString
import onlymash.materixiv.ui.module.common.StorageFolderLifecycleObserver

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val sp: SharedPreferences? get() = preferenceManager.sharedPreferences

    private val folderSummary: String
        get() {
            val defaultSummary = getString(R.string.settings_category_storage_folder_summary_default)
            val context = context ?: return defaultSummary
            val uri = context.contentResolver.getPersistedUri() ?: return defaultSummary
            return uri.toDecodedString()
        }
    private var storageFolderPreference: Preference? = null
    private var dohPreference: SwitchPreference? = null
    private var dohProviderPreference: SimpleMenuPreference? = null
    private var isDohEnabled: Boolean
        get() = sp.getValue(Keys.NETWORK_DOH, true)
        set(value) {
            sp?.setValue(Keys.NETWORK_DOH, value)
        }

    private lateinit var observer: StorageFolderLifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observer = StorageFolderLifecycleObserver(requireActivity().activityResultRegistry)
        lifecycle.addObserver(observer)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.apply {
            sharedPreferencesName = Values.PREFERENCE_NAME_SETTINGS
            sharedPreferencesMode = Context.MODE_PRIVATE
        }
        setPreferencesFromResource(R.xml.settings, rootKey)
        storageFolderPreference = findPreference(Keys.STORAGE_FOLDER)
        storageFolderPreference?.apply {
            summary = folderSummary
            setOnPreferenceClickListener {
                pickDir()
                true
            }
        }
        dohPreference = findPreference(Keys.NETWORK_DOH)
        dohProviderPreference = findPreference(Keys.NETWORK_DOH_PROVIDER)
        setupDohPreferenceVisible()
        sp?.registerOnSharedPreferenceChangeListener(this)
    }

    private fun setupDohPreferenceVisible(dohChanged: Boolean = false) {
        dohProviderPreference?.isVisible = isDohEnabled
        if (dohChanged) {
            dohPreference?.summary = getString(R.string.settings_summary_restart)
        }
    }

    private fun pickDir() {
        val context = context ?: return
        observer.openDocumentTree(context)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            Keys.STORAGE_FOLDER -> storageFolderPreference?.summary = folderSummary
            Keys.NETWORK_DOH, Keys.NETWORK_DOH_PROVIDER -> setupDohPreferenceVisible(true)
        }
    }

    override fun onDestroyView() {
        sp?.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroyView()
    }
}