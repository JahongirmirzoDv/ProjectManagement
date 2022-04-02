package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.others.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import uz.perfectalgorithm.projects.tezkor.data.sources.local_models.setting.LanguageClass
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.DirectionResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemLanguageSpinnerBinding
import uz.perfectalgorithm.projects.tezkor.databinding.ItemSpinnerBinding

class LanguageSpinnerAdapter(var list: List<LanguageClass>): BaseAdapter() {
    override fun getCount(): Int  = list.size

    override fun getItem(position: Int): LanguageClass = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var binding: ItemLanguageSpinnerBinding
        if (convertView == null) {
            binding = ItemLanguageSpinnerBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        } else {
            binding = ItemLanguageSpinnerBinding.bind(convertView)
        }
        val item = getItem(position)

        binding.apply {
            countryImg.setImageResource(item.img)
            countryName.text = item.name
        }

        return  binding.root
    }
}