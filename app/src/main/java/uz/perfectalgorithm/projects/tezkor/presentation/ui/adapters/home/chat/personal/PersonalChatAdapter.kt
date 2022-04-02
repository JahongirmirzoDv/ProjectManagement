package uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.chat.personal

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.SwipeRevealLayout
import uz.perfectalgorithm.projects.tezkor.R
import uz.perfectalgorithm.projects.tezkor.app.App
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.all_chat.personal.PersonalChatListResponse
import uz.perfectalgorithm.projects.tezkor.databinding.ItemPersonalChatUserBinding
import uz.perfectalgorithm.projects.tezkor.utils.DateUtil.getTimeForChat
import uz.perfectalgorithm.projects.tezkor.utils.`typealias`.SingleBlock
import uz.perfectalgorithm.projects.tezkor.utils.extensions.bindItem
import uz.perfectalgorithm.projects.tezkor.utils.extensions.loadImageUrlUniversal
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.log_messages.myLogD
import uz.perfectalgorithm.projects.tezkor.utils.visible

/**
 * Created by Davronbek Raximjanov on 27.07.2021 10:22
 **/

class PersonalChatAdapter(private val userId: Int) :
    RecyclerView.Adapter<PersonalChatAdapter.VH>() {

    private val mList = ArrayList<PersonalChatListResponse.PersonalChatDataItem>()


    private var listenerCallback: SingleBlock<PersonalChatListResponse.PersonalChatDataItem>? = null
    private var listenDeleteClick: SingleBlock<PersonalChatListResponse.PersonalChatDataItem>? =
        null

    private val swViewList: ArrayList<SwipeRevealLayout> = ArrayList()
    private var positionSw = -1

    inner class VH(private val binding: ItemPersonalChatUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                swViewList.add(rootSwRl)
                rootSwRl.close(false)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind() = bindItem {
            binding.apply {

                val data = mList[absoluteAdapterPosition]

                if (data.isOpen) {
                    rootSwRl.open(true)
                } else {
                    rootSwRl.close(true)
                }

                txtUsername.text = data.receiver?.fullName

                data.receiver?.image?.let {
                    imgPerson.loadImageUrlUniversal(
                        it,
                        R.drawable.ic_user
                    )
                }

                if (data.receiver?.image != null) {
                    if (data.receiver?.image == "") {
                        txtDefaultImg.visible()
                        txtDefaultImg.text = if (data.receiver.fullName.isNullOrEmpty()) "" else
                            data.receiver?.fullName.toString().trim()?.get(0).toString()
                        imgPerson.gone()
                    } else {
                        txtDefaultImg.gone()
                        imgPerson.visible()
                        data.receiver?.image?.let {
                            imgPerson.loadImageUrlUniversal(
                                it,
                                R.drawable.ic_user
                            )
                        }
                    }
                } else {
                    txtDefaultImg.visible()
                    txtDefaultImg.text =
                        data.receiver?.fullName.toString().trim()?.get(0).toString()
                    imgPerson.gone()
                }


                if (data.lastMessage?.creator?.id != userId) {
                    if (data.unreadMessagesCount!! > 0) {
                        txtUnreadCount.visible()
                        txtUnreadCount.text = data.unreadMessagesCount.toString()
                    } else {
                        txtUnreadCount.gone()
                    }
                    imgReadUnread.gone()

                } else {
                    imgReadUnread.visible()
                    txtUnreadCount.gone()

                    if (data.lastMessage.isRead == true)
                        imgReadUnread.setImageResource(R.drawable.ic_read_message_s)
                    else {
                        imgReadUnread.setImageResource(R.drawable.ic_unread_message)
                    }
                }

                data.lastMessage?.timeCreated?.let {
                    txtDate.text = getTimeForChat(it)
                }


                txtLastMessage.text = data.lastMessage?.message
                if (data.lastMessage?.files?.isNotEmpty() == true) {
                    txtLastMessage.text = App.instance.getString(R.string.picture_msg_las_title)
                }

                frontLayout.setOnClickListener {
                    if (rootSwRl.isOpened) {
                        rootSwRl.close(true)
                    } else {
                        listenerCallback?.invoke(data)
                    }
                }

                foregroundLayout.setOnClickListener {
                    listenDeleteClick?.invoke(data)
                    rootSwRl.close(false)
                }

                rootSwRl.setSwipeListener(object : SwipeRevealLayout.SwipeListener {
                    override fun onClosed(view: SwipeRevealLayout?) {
                        myLogD("closed => ", "TAG189")
                        positionSw = -1
                    }

                    override fun onOpened(view: SwipeRevealLayout?) {
                        myLogD("opened => ", "TAG189")
                        if (positionSw > -1) {
                            swViewList[positionSw].close(true)
                        }
                        positionSw = absoluteAdapterPosition
                    }

                    override fun onSlide(view: SwipeRevealLayout?, slideOffset: Float) {
                        myLogD("slide => ", "TAG189")

                    }
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VH(
        ItemPersonalChatUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind()
    }

    fun setOnClickListener(block: SingleBlock<PersonalChatListResponse.PersonalChatDataItem>) {
        listenerCallback = block
    }

    fun setOnDeleteClickListener(block: SingleBlock<PersonalChatListResponse.PersonalChatDataItem>) {
        listenDeleteClick = block
    }


    fun submitList(list: List<PersonalChatListResponse.PersonalChatDataItem>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount() = mList.size

}