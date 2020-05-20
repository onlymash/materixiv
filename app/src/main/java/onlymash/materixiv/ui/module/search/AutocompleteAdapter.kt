package onlymash.materixiv.ui.module.search

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import onlymash.materixiv.app.Values
import onlymash.materixiv.data.model.common.Tag
import onlymash.materixiv.databinding.ItemNameBinding
import onlymash.materixiv.databinding.ItemTagBinding
import onlymash.materixiv.ui.viewbinding.viewBinding

class AutocompleteAdapter(
    private val type: Int,
    private val clickItemCallback: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val tags: MutableList<Tag> = mutableListOf()
    private val names: MutableList<String> = mutableListOf()

    fun updateTags(tags: List<Tag>) {
        this.tags.clear()
        this.tags.addAll(tags)
        notifyDataSetChanged()
    }

    fun updateNames(names: List<String>) {
        this.names.clear()
        this.names.addAll(names)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int): RecyclerView.ViewHolder {
        return if (type == Values.SEARCH_TYPE_USER) {
            NameViewHolder(parent)
        } else {
            TagViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TagViewHolder -> holder.bind(tags[position])
            is NameViewHolder -> holder.bind(names[position])
        }
    }

    override fun getItemCount(): Int = if (type == Values.SEARCH_TYPE_USER) names.size else tags.size

    inner class TagViewHolder(binding: ItemTagBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup): this(parent.viewBinding(ItemTagBinding::inflate))

        private val nameJp = binding.nameJp
        private val nameTranslation = binding.nameTranslation

        init {
            itemView.setOnClickListener {
                clickItemCallback.invoke(nameJp.text.toString())
            }
        }

        fun bind(tag: Tag) {
            nameJp.text = tag.name
            nameTranslation.text = tag.translatedName
        }
    }

    inner class NameViewHolder(binding: ItemNameBinding) : RecyclerView.ViewHolder(binding.root) {

        constructor(parent: ViewGroup): this(parent.viewBinding(ItemNameBinding::inflate))

        private val name = binding.name

        init {
            itemView.setOnClickListener {
                clickItemCallback.invoke(name.text.toString())
            }
        }

        fun bind(name: String) {
            this.name.text = name
        }
    }
}