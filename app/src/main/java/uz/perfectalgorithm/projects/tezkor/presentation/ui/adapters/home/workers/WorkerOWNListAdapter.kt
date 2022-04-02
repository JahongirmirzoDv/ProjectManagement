package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.workers

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemWorkerListBinding
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrl

class WorkerOWNListAdapter(
    private val list: List<AllWorkersResponse.DataItem>
) : RecyclerView.Adapter<WorkerOWNListAdapter.WorkerListHolder>() {

    private var listenerClickItem: SingleBlock<AllWorkersResponse.DataItem>? = null
    private var listenerClickPerson: SingleBlock<AllWorkersResponse.DataItem>? = null
    private var listenerClickPhone: SingleBlock<AllWorkersResponse.DataItem>? = null
    private var listenerClickFavourite: SingleBlock<AllWorkersResponse.DataItem>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkerListHolder {
        val binding =
            ItemWorkerListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkerListHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkerListHolder, position: Int) {
        if (position > 0) {
            holder.bind(list[position - 1], list[position])
        } else {
            holder.bind(workerLast = AllWorkersResponse.DataItem(), list[position])
        }
    }

    override fun getItemCount(): Int = list.size

    inner class WorkerListHolder(private val binding: ItemWorkerListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(workerLast: AllWorkersResponse.DataItem, workerThis: AllWorkersResponse.DataItem) {
            binding.tvPersonName.text = "${workerThis.firstName} ${workerThis.lastName}"
            if (workerThis.lastName != null && workerLast.firstName != null)
                if (workerThis.lastName.isNotEmpty() && workerLast.firstName.isNotEmpty()) {

                    if (workerLast.firstName[0] != workerThis.lastName[0]) {
                        binding.tvTitle.visibility = View.VISIBLE
                        binding.tvTitle.text = workerThis.firstName!![0].toString()
                        if (workerThis.isFavourite!!) {
                            binding.imgSaved.setImageDrawable(
                                ContextCompat.getDrawable(
                                    binding.root.context,
                                    R.drawable.ic_saved_contact
                                )
                            )
                        }

                        workerThis.image?.let { binding.imgAvatar.loadImageUrl(it) }

                    } else {
                        binding.tvTitle.visibility = View.GONE
                    }
                }

            binding.cons1.setOnClickListener {
                dropDown()
            }
            binding.imgDropDown.setOnClickListener {
                dropDown()
            }

            binding.btnPersonal.setOnClickListener {
                listenerClickPerson?.invoke(workerLast)
            }

            binding.ll1.setOnClickListener {
                listenerClickPerson?.invoke(workerThis)
            }

            binding.btnPhone.setOnClickListener {
                Log.d("RDO", ":::::: => ::::::: => ${workerThis.phoneNumber}")
                listenerClickPhone?.invoke(workerThis)
            }

            binding.imgPhone.setOnClickListener {
                listenerClickPhone?.invoke(workerThis)
            }

            binding.btnFavourite.setOnClickListener {
                listenerClickFavourite?.invoke(workerThis)
            }
        }

        private fun dropDown() {
            if (binding.ll1.visibility == View.GONE) {
                binding.ll1.visibility = View.VISIBLE
                binding.imgDropDown.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_chevron_down
                    )
                )
            } else {
                binding.ll1.visibility = View.GONE
                binding.imgDropDown.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.root.context,
                        R.drawable.ic_chevron_up
                    )
                )
            }
        }
    }

    fun onClickPerson(f: SingleBlock<AllWorkersResponse.DataItem>) {
        listenerClickPerson = f
    }

    fun onClickPhone(f: SingleBlock<AllWorkersResponse.DataItem>) {
        listenerClickPhone = f
    }

    fun onClickFavourite(f: SingleBlock<AllWorkersResponse.DataItem>) {
        listenerClickFavourite = f
    }

}

