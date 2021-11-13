package io.github.ja2stracciatella.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.github.ja2stracciatella.ConfigurationModel
import io.github.ja2stracciatella.R

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

    private lateinit var configurationModel: ConfigurationModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_launcher, container, false)
        val gameDirTextView: TextView = root.findViewById(R.id.gameDirTextView)
        configurationModel = ViewModelProvider(requireActivity()).get(ConfigurationModel::class.java)
        configurationModel.vanillaGameDir.observe(viewLifecycleOwner) {
            gameDirTextView.text = it
        }
        val resversionTextView: TextView = root.findViewById(R.id.resversionTextView)
        configurationModel.resversion.observe(viewLifecycleOwner) {
            resversionTextView.text = it
        }
        val resTextView: TextView = root.findViewById(R.id.resTextView)
        configurationModel.res.observe(viewLifecycleOwner) {
            resTextView.text = it
        }
        val scalingTextView: TextView = root.findViewById(R.id.scalingTextView)
        configurationModel.scaling.observe(viewLifecycleOwner) {
            scalingTextView.text = it
        }
        val nosoundTextView: TextView = root.findViewById(R.id.nosoundTextView)
        configurationModel.nosound.observe(viewLifecycleOwner) {
            nosoundTextView.text = (!it).toString()
        }
        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): PlaceholderFragment {
            return PlaceholderFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}