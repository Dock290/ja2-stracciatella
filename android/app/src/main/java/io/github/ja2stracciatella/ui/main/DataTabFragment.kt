package io.github.ja2stracciatella.ui.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.codekidlabs.storagechooser.StorageChooser
import io.github.ja2stracciatella.ConfigurationModel
import io.github.ja2stracciatella.R
import io.github.ja2stracciatella.util.Util
import kotlinx.android.synthetic.main.fragment_launcher_data_tab.view.*


/**
 * A placeholder fragment containing a simple view.
 */
class DataTabFragment : Fragment() {
    private val requestPermissionsCode = 1001

    private lateinit var configurationModel: ConfigurationModel
    private lateinit var chooser: StorageChooser

    override fun onCreate(savedInstanceState: Bundle?) {
        configurationModel = ViewModelProvider(requireActivity()).get(ConfigurationModel::class.java)
        // We suppress deprecation for .fragmentManager here as we need the android.app.FragmentManager
        // and not androidx.fragment.app.FragmentManager for the Storage Chooser
        @Suppress("DEPRECATION")
        chooser = StorageChooser.Builder()
            .withActivity(activity)
            .withFragmentManager(activity?.fragmentManager)
            .allowCustomPath(true)
            .setType(StorageChooser.DIRECTORY_CHOOSER)
            .build()
        chooser.setOnSelectListener { path ->
            configurationModel.apply {
                setVanillaGameDir(path)
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val tab = inflater.inflate(R.layout.fragment_launcher_data_tab, container, false)

        update(tab)

        configurationModel.vanillaGameDir.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                tab.gameDirValueText.text = it
            }
        }
        tab.gameDirChooseButton?.setOnClickListener {
            showGameDirChooser()
        }

        tab.resversionValueSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                configurationModel.setResversion(ConfigurationModel.resversionList[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        tab.resTextInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                val resolution = editable.toString()
                context?.let {
                    if (Util.validateResolution(resolution)) {
                        tab.resTextInput.setTextColor(ContextCompat.getColor(it, R.color.textPrimaryColor))
                    } else {
                        tab.resTextInput.setTextColor(ContextCompat.getColor(it, R.color.textColorRed))
                    }
                }
                configurationModel.setRes(resolution)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        tab.scalingValueSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                configurationModel.scaling.value?.let {
                    configurationModel.setScaling(ConfigurationModel.scalingList[position])
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        tab.soundCheckBox.setOnCheckedChangeListener { _, isChecked ->
            configurationModel.setNosound(!isChecked)
        }

        return tab
    }

    fun update(view: View) {
        view.resversionValueSpinner
            ?.setSelection(ConfigurationModel.resversionList.indexOf(configurationModel.resversion.value))
        view.resTextInput?.setText(configurationModel.res.value)
        view.scalingValueSpinner
            ?.setSelection(ConfigurationModel.scalingList.indexOf(configurationModel.scaling.value))
        view.soundCheckBox?.isChecked = configurationModel.nosound.value == false
    }

    private fun showGameDirChooser() {
        getPermissionsIfNecessaryForAction {
            chooser.show()
        }
    }

    private fun getPermissionsIfNecessaryForAction(action: () -> Unit) {
        val permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val hasAllPermissions = permissions.all {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
        if (hasAllPermissions) {
            action()
        } else {
            requestPermissions(permissions, requestPermissionsCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == requestPermissionsCode) {
            if (grantResults.all { r -> r == PackageManager.PERMISSION_GRANTED }) {
                showGameDirChooser()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Cannot select game directory without proper permissions",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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
        fun newInstance(sectionNumber: Int): DataTabFragment {
            return DataTabFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}