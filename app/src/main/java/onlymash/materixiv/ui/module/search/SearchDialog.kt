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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import onlymash.materixiv.databinding.DialogSearchBinding
import onlymash.materixiv.R
import onlymash.materixiv.app.Keys
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.model.common.Tag

class SearchDialog : BaseSearchDialog<DialogSearchBinding>() {

    companion object {
        fun create(type: Int, word: String? = null): SearchDialog {
            return SearchDialog().apply {
                arguments = Bundle().apply {
                    putInt(Keys.SEARCH_TYPE, type)
                    if (word != null) {
                        putString(Keys.SEARCH_WORD, word)
                    }
                }
            }
        }
    }
    private var type = Values.SEARCH_TYPE_ILLUST
    private var word: String? = null
    private lateinit var autocompleteAdapter: AutocompleteAdapter
    private lateinit var textInputEdit: TextInputEditText
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
        arguments?.apply {
            type = getInt(Keys.SEARCH_TYPE)
            word = getString(Keys.SEARCH_WORD)
        }
    }

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogSearchBinding {
        return DialogSearchBinding.inflate(inflater, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.let {
                WindowCompat.setDecorFitsSystemWindows(it, false)
            }
        }
    }

    override fun onBaseViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onBaseViewCreated(view, savedInstanceState)
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                left = systemBarsInsets.left,
                right = systemBarsInsets.right,
                top = systemBarsInsets.top,
                bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom)
            insets
        }
        textInputEdit = binding.textInputEdit
        textInputEdit.setHint(when (type) {
            Values.SEARCH_TYPE_ILLUST -> R.string.search_hint_illust
            Values.SEARCH_TYPE_NOVEL -> R.string.search_hint_novel
            Values.SEARCH_TYPE_USER -> R.string.search_hint_user
            else -> R.string.search_hint
        })
        if (word != null) {
            setQuery(word)
        }
        textInputEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL) {
                val keyword = textInputEdit.text ?: ""
                search(keyword.toString())
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
        autocompleteAdapter = AutocompleteAdapter(type) { word -> handleWord(word) }
        binding.list.adapter = autocompleteAdapter
        textInputEdit.addTextChangedListener { text: Editable? ->
            if (type == Values.SEARCH_TYPE_USER) {
                val query = text?.trim()
                if (!query.isNullOrEmpty()) {
                    fetchNames(query.toString())
                }
            } else {
                val query = text?.toString()
                if (query != null) {
                    val queryList = query.replace(" +".toRegex(), " ").split(" ")
                    if (queryList.isNotEmpty()) {
                        val word = queryList.last()
                        if (word.isNotEmpty()) {
                            fetchTags(word)
                        }
                    }
                }
            }
        }
    }

    private fun search(word: String) {
        val keyword = word.trim()
        if (keyword.isEmpty()) {
            return
        }
        context?.let { context ->
            SearchActivity.startSearch(context, type, keyword)
            dismiss()
        }
    }

    private fun handleWord(word: String) {
        if (type == Values.SEARCH_TYPE_USER) {
            search(word)
        } else {
            val queryList = (textInputEdit.text ?: "").toString()
                .replace(" +".toRegex(), " ")
                .split(" ")
                .toMutableList()
            if (queryList.isEmpty()) {
                setQuery("$word ")
                return
            }
            if (tag == queryList.last()) {
                return
            }
            queryList.removeLast()
            queryList.add(word)
            var query = ""
            queryList.forEach {
                query = "$query $it"
            }
            query = query.trim()
            setQuery("$query ")
        }
    }

    private fun setQuery(query: String?) {
        textInputEdit.setText(query)
        if (query != null) {
            textInputEdit.setSelection(query.length)
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
        textInputEdit.postDelayed({
            textInputEdit.requestFocus()
            inputMethodManager?.showSoftInput(textInputEdit, InputMethodManager.SHOW_IMPLICIT)
        }, 300L)
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

    override fun onSaveInstanceState(outState: Bundle) {
        arguments?.putString(Keys.SEARCH_WORD, word)
        super.onSaveInstanceState(outState)
    }
}