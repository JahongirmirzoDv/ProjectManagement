package uz.perfectalgorithm.projects.tezkor.presentation.ui.screens.home_activity.quick_ideas_box

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import uz.perfectalgorithm.projects.tezkor.data.sources.local.LocalStorage
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment.CommentData
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.idea_comment.PostCommentBody
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.rating_idea.RatingIdea
import uz.perfectalgorithm.projects.tezkor.databinding.FragmentCommentBinding
import uz.perfectalgorithm.projects.tezkor.presentation.ui.adapters.home.quick_idea.CommentAdapter
import uz.perfectalgorithm.projects.tezkor.presentation.viewmodels.home_activity.idea_comment.IdeaCommentViewModel
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.handleException
import uz.perfectalgorithm.projects.tezkor.utils.error_handling.makeErrorSnack
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideAppBar
import uz.perfectalgorithm.projects.tezkor.utils.extensions.hideBottomMenu
import uz.perfectalgorithm.projects.tezkor.utils.extensions.show
import uz.perfectalgorithm.projects.tezkor.utils.gone
import uz.perfectalgorithm.projects.tezkor.utils.hideKeyboard
import uz.perfectalgorithm.projects.tezkor.utils.visible
import javax.inject.Inject


@AndroidEntryPoint
class CommentFragment : Fragment() {

    private var _binding: FragmentCommentBinding? = null
    private val binding: FragmentCommentBinding
        get() = _binding ?: throw NullPointerException("View wasn't created")

    private val args: CommentFragmentArgs by navArgs()

    private lateinit var mAdapter: CommentAdapter

    private val ideaCommentViewModel: IdeaCommentViewModel by viewModels()

    private var commentList = ArrayList<CommentData>()

    private var ideaData: RatingIdea? = null

    @Inject
    lateinit var storage: LocalStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        hideBottomMenu()
        hideAppBar()

        ideaData = args.ideaData

        initData()
        loadViews()
        loadObservers()
    }

    @SuppressLint("FragmentLiveDataObserve")
    private fun loadObservers() {
        ideaCommentViewModel.progressLiveData.observe(viewLifecycleOwner, progressObserver)
        ideaCommentViewModel.errorLiveData.observe(viewLifecycleOwner, errorObserver)
        ideaCommentViewModel.notConnectionLiveData.observe(
            viewLifecycleOwner,
            notConnectionObserver
        )
        ideaCommentViewModel.postIdeaCommentLiveData.observe(this, postIdeaCommentObserver)
        ideaCommentViewModel.getIdeaCommentList.observe(this, getIdeaCommentsListObserver)
    }

    private val getIdeaCommentsListObserver = Observer<List<CommentData>> { commentList ->
        this.commentList.clear()
        this.commentList.addAll(commentList)
        load()
        binding.rvComments.scrollToPosition(commentList.size - 1)

    }

    private val postIdeaCommentObserver = Observer<CommentData> {
        binding.apply {
            hideKeyboard()
            etMessage.text?.clear()
        }
        load()
        binding.rvComments.scrollToPosition(commentList.size - 1)

    }

    private val notConnectionObserver = Observer<Unit> {
        makeErrorSnack()
    }

    private val errorObserver = Observer<Throwable> {
        if (it is Exception) {
            handleException(it)
        } else {
            makeErrorSnack(it.message.toString())
        }
    }

    private val progressObserver = Observer<Boolean> {
        val progressView = binding.commentProgressBar
        if (it) progressView.show()
        else progressView.gone()
    }

    private fun initData() {
        binding.apply {
//            val idea = args.ideaData
            val time = ideaData?.createdAt?.replace("-", ".")
            val resultDate = time?.split("T")?.get(0)
            ideaCreateDate.text = resultDate
            ideaOwner.text = "${ideaData?.creator?.lastName}   ${ideaData?.creator?.firstName}:"


            if (ideaData?.description?.length!! > 100) {
                ideaDescriptionTextView.gone()
                ideaDescription.show()
                ideaDescription.text = ideaData?.description

            } else {
                ideaDescriptionTextView.visible()
                ideaDescription.gone()
                ideaDescriptionTextView.text = ideaData?.description
            }
        }
    }

    private fun loadViews() {

        storage.ideaId = args.ideaData?.id!!

        binding.apply {
            toolbar.setNavigationOnClickListener {
                findNavController().popBackStack()
                hideKeyboard()

            }


        }
        initRecyclerView()
        initForSendComment()

    }

    private fun initRecyclerView() {
        mAdapter = CommentAdapter(storage.userId/*args.ideaData?.creator?.id*/)
        binding.rvComments.adapter = mAdapter

        val mLayoutManager = LinearLayoutManager(requireContext())
        binding.rvComments.layoutManager = mLayoutManager

    }

    private fun initForSendComment() {
        with(binding) {
            imgSendMessage.setOnClickListener {
                sendComment()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        load()
    }

    private fun load() {
        lifecycleScope.launch {
            ideaCommentViewModel.getIdeaCommentList().collectLatest { pagedData ->
                mAdapter.submitData(pagedData)
            }
        }
    }


    private fun sendComment() {
        val commentData =
            PostCommentBody(
                storage.ideaId,
                binding.etMessage.text.toString().trim()
            )
        ideaCommentViewModel.postIdeaComment(commentData)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}