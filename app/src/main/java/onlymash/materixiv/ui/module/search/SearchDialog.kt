package onlymash.materixiv.ui.module.search

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import onlymash.materixiv.databinding.DialogSearchBinding
import onlymash.materixiv.R
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.model.common.Tag

class SearchDialog : BaseSearchDialog<DialogSearchBinding>() {

    companion object {
        fun create(type: Int): SearchDialog {
            return SearchDialog().apply {
                arguments = Bundle().apply {
                    putInt(Keys.SEARCH_TYPE, type)
                }
            }
        }
    }
    private var type = Values.SEARCH_TYPE_ILLUST
    private lateinit var autocompleteAdapter: AutocompleteAdapter
    private val textInputEdit get() =  binding.textInputEdit
    private val inputMethodManager by lazy {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        } else {
            context?.getSystemService(InputMethodManager::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogThemeFullScreen)
        type = arguments?.getInt(Keys.SEARCH_TYPE) ?: Values.SEARCH_TYPE_ILLUST
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogSearchBinding {
        return DialogSearchBinding.inflate(inflater, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
        textInputEdit.setHint(when (type) {
            Values.SEARCH_TYPE_ILLUST -> R.string.search_hint_illust
            Values.SEARCH_TYPE_NOVEL -> R.string.search_hint_novel
            Values.SEARCH_TYPE_USER -> R.string.search_hint_user
            else -> R.string.search_hint
        })
        textInputEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL) {
                search(textInputEdit.text?.trim().toString())
            }
            true
        }
        binding.leftButton.setOnClickListener { dismiss() }
        binding.rightButton.setOnClickListener {
            if (textInputEdit.text.isNullOrEmpty()) {
                dismiss()
            } else {
                textInputEdit.text = null
            }
        }
        autocompleteAdapter = AutocompleteAdapter(type) { word -> search(word) }
        binding.list.adapter = autocompleteAdapter
        textInputEdit.addTextChangedListener { text: Editable? ->
            if (type == Values.SEARCH_TYPE_USER) {
                fetchNames(text?.trim()?.toString())
            } else {
                fetchTags(text?.trim()?.toString())
            }
        }
    }

    private fun search(word: String) {
        if (word.isNotEmpty()) {
            context?.let { context ->
                SearchActivity.startSearch(context, type, word)
                dismiss()
            }
        }
    }

    override fun onTagsUpdated(tags: List<Tag>) {
        if (type != Values.SEARCH_TYPE_USER) {
            autocompleteAdapter.updateTags(tags)
        }
    }

    override fun onNamesUpdated(names: List<String>) {
        if (type == Values.SEARCH_TYPE_USER) {
            autocompleteAdapter.updateNames(names)
        }
    }

    override fun onResume() {
        super.onResume()
        textInputEdit.post {
            textInputEdit.requestFocus()
            inputMethodManager?.showSoftInput(textInputEdit, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onDismiss(dialog: DialogInterface) {
        inputMethodManager?.apply {
            if (isActive) {
                toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
        super.onDismiss(dialog)
    }
}