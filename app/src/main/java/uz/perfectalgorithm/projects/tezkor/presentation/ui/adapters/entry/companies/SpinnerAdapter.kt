package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.entry.companies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.auth.DirectionResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemSpinnerBinding

class SpinnerAdapter(var list: List<DirectionResponse.Direction>): BaseAdapter() {
    override fun getCount(): Int  = list.size

    override fun getItem(position: Int): DirectionResponse.Direction = list[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var binding: ItemSpinnerBinding

        if (convertView == null) {
            binding = ItemSpinnerBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        } else {
            binding = ItemSpinnerBinding.bind(convertView)
        }

        binding.txt.text = list[position].title

        return  binding.root
    }
}