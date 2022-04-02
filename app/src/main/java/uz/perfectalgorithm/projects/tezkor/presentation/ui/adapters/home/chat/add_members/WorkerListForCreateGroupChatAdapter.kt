package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.add_members

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.workers.AllWorkersShortDataResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemTeamWorkersGroupChatBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlUniversal

/**
 * Created by Raximjanov Davronbek on 07.10.2021 16:44
 **/

class WorkerListForCreateGroupChatAdapter :
    RecyclerView.Adapter<WorkerListForCreateGroupChatAdapter.VH>(
    ) {
    private var myList: ArrayList<AllWorkersShortDataResponse.WorkerShortDataItem> =
        ArrayList()

    private var listenClick: SingleBlock<AllWorkersShortDataResponse.WorkerShortDataItem>? = null
    private var listenCheck: SingleBlock<AllWorkersShortDataResponse.WorkerShortDataItem>? = null

    inner class VH(val binding: ItemTeamWorkersGroupChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() = bindItem {

            binding.apply {
                val data = getItem(absoluteAdapterPosition)
                data?.let { data ->
                    txtFullName.text = data.fullName
                    if (data.image?.isNotEmpty() == true) {
                        imgPerson.loadImageUrlUniversal(data.image, R.drawable.ic_user)
                    } else {
                        imgPerson.setImageResource(R.drawable.ic_user)
                    }
                    if (data.isChecked) {
                        imgIsChecked.setImageResource(R.drawable.ic_check_ellipse)
                    } else {
                        imgIsChecked.setImageResource(R.drawable.ic_no_check_ellipse)
                    }
                }

                root.setOnClickListener {
                    data.isChecked = !data.isChecked
                    if (data.isChecked) {
                        imgIsChecked.setImageResource(R.drawable.ic_check_ellipse)
                    } else {
                        imgIsChecked.setImageResource(R.drawable.ic_no_check_ellipse)
                    }
                    listenCheck?.invoke(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemTeamWorkersGroupChatBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun setOnClickListener(block: SingleBlock<AllWorkersShortDataResponse.WorkerShortDataItem>) {
        listenClick = block
    }

    fun setOnCheckListener(block: SingleBlock<AllWorkersShortDataResponse.WorkerShortDataItem>) {
        listenCheck = block
    }

    override fun getItemCount() = myList.size

    fun getItem(position: Int) = myList[position]

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<AllWorkersShortDataResponse.WorkerShortDataItem>) {
        myList.clear()
        myList.addAll(list)
        notifyDataSetChanged()
    }

}