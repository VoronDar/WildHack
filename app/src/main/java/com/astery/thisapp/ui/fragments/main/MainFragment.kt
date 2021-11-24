package com.astery.thisapp.ui.fragments.main

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MediaController
import androidx.fragment.app.viewModels
import com.astery.thisapp.databinding.FragmentMainBinding
import com.astery.thisapp.ui.fragments.TFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainFragment : TFragment(), MediaPlayer.OnPreparedListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.loadCams()
        viewModel.cameras.observe(viewLifecycleOwner) {
            val list: ArrayList<String> = ArrayList()
            for (i in it) {
                list.add("Camera " + i.id.toString() + ": ${i.organisation.name}")
            }

            val adapter = ArrayAdapter(
                requireContext(),
                com.astery.thisapp.R.layout.spinner_dropdown, list
            )

            binding.video.cameraDropdown.setAdapter(adapter)
            binding.video.cameraDropdown.setText(adapter.getItem(0).toString(), false)
            viewModel.getStreamUrl(it[0].id)


            binding.video.cameraDropdown.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ -> viewModel.getStreamUrl(it[position].id) }

        }
        viewModel.streamUrl.observe(viewLifecycleOwner){setupVideoView(it)}
    }
    private fun setupVideoView(url:String) {
        Timber.d("stream url $url")
        binding.video.stream.setOnPreparedListener(this)
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.video.stream)
        binding.video.stream.setVideoURI(Uri.parse(url))
        binding.video.stream.setMediaController(mediaController)
        Timber.i("videoview prepared")
    }

    override fun onPrepared(mp: MediaPlayer?) {
        binding.video.stream.start()
        Timber.i("started")
    }

    override fun onPause() {
        super.onPause()
        binding.video.stream.pause()
        Timber.i("paused")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}