package dev.aldi.diyiotwithmqtt

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import dev.aldi.diyiotwithmqtt.databinding.FragmentBrokerSettingBinding
import dev.aldi.diyiotwithmqtt.entity.Broker
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BrokerSettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BrokerSettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentBrokerSettingBinding? = null
    private val binding get() = _binding!!

    private lateinit var mqttHost: String
    private lateinit var mqttPort: Number
    private lateinit var mqttUsername: String
    private lateinit var mqttPassword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBrokerSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val database = (requireActivity().application as MyApplication).database
        val broker = database.brokerDao()

        viewLifecycleOwner.lifecycleScope.launch {
            val setting = broker.get()
            if (setting != null) {
                binding.mqttHost.setText(setting.host)
                binding.mqttPort.setText(setting.port.toString())
                binding.mqttUsername.setText(setting.username)
                binding.mqttPassword.setText(setting.password)
            } else {
                mqttHost = binding.mqttHost.text.toString()
                mqttPort = Integer.parseInt(binding.mqttPort.text.toString())
                mqttUsername = binding.mqttUsername.text.toString()
                mqttPassword = binding.mqttPassword.text.toString()
            }
        }

        binding.brokerSettingSave.setOnClickListener {
            val host = binding.mqttHost.text.toString()
            val port = Integer.parseInt(binding.mqttPort.text.toString())
            val username = binding.mqttUsername.text.toString()
            val password = binding.mqttPassword.text.toString()

            viewLifecycleOwner.lifecycleScope.launch {
                broker.save(Broker(1, host, port, username, password))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BrokerSettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BrokerSettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}