package dev.aldi.diyiotwithmqtt

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import dev.aldi.diyiotwithmqtt.dao.BrokerDao
import dev.aldi.diyiotwithmqtt.databinding.FragmentBrokerSettingBinding
import dev.aldi.diyiotwithmqtt.databinding.FragmentPubSubDebugBinding
import dev.aldi.diyiotwithmqtt.entity.Broker
import kotlinx.coroutines.launch
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import java.time.Instant
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PubSubDebugFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PubSubDebugFragment : MqttCallback, Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentPubSubDebugBinding? = null
    private val binding get() = _binding!!

    private lateinit var mqttClient: MqttAndroidClient
    private lateinit var brokerDao: BrokerDao

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
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPubSubDebugBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.messageLog.movementMethod = ScrollingMovementMethod()

        brokerDao = (requireActivity().application as MyApplication).database.brokerDao()
        viewLifecycleOwner.lifecycleScope.launch {
            val setting = brokerDao.get()
            if (setting != null) {
                val serverUri = "tcp://${setting.host}:${setting.port}"
                binding.brokerStatus.text = "Client Status: DISCONNECTED"
                connectMqtt(serverUri, setting.username!!, setting.password!!)
            } else {
                val broker = Broker(1, "broker.emqx.io", 1883, "", "")
                brokerDao.save(broker)
                val serverUri = "tcp://${broker.host}:${broker.port}"
                binding.brokerStatus.text = "Client Status: DISCONNECTED"
                connectMqtt(serverUri)
            }
        }

        binding.subscribeBtn.setOnClickListener {
            val topic = binding.subscribeTopic.text
            subscribeTopic(topic.toString())
        }

        binding.unsubscribeBtn.setOnClickListener {
            val topic = binding.subscribeTopic.text
            unsubscribeTopic(topic.toString())
        }

        binding.publishButton.setOnClickListener {
            val topic = binding.publishTopic.text.toString()
            val payload = binding.publishPayload.text.toString()
            val isRetained = binding.isRetain.isChecked
            var qos = binding.publishQos.text.toString().toInt()

            if (qos < 0) qos = 0
            if (qos > 2) qos = 2

            publishMessage(topic, payload, qos, isRetained)
        }
    }

    private fun publishMessage (topic: String, payload: String, qos: Int = 2, isRetained: Boolean = false) {
        val message = MqttMessage()
        message.payload = payload.toByteArray()
        message.qos = qos
        message.isRetained = isRetained
        mqttClient.publish(topic, message, null, object: IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Toast.makeText(requireContext(), "Message published to ${topic.toString()} with message: ${payload.toString()}", Toast.LENGTH_LONG).show()
             }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Toast.makeText(requireContext(), "Failed to publish ${payload.toString()} to topic ${topic.toString()}", Toast.LENGTH_LONG).show()            }
        })
    }

    private fun unsubscribeTopic (topic: String) {
        mqttClient.unsubscribe(topic, null, object: IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Toast.makeText(requireContext(), "Success to unsubscribe ${topic.toString()}", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Toast.makeText(requireContext(), "Failed to unsubscribe ${topic.toString()}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun subscribeTopic (topic: String) {
        mqttClient.subscribe(topic, 2, null, object: IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Toast.makeText(requireContext(), "Success to subscribe ${topic.toString()}", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Toast.makeText(requireContext(), "Failed to subscribe ${topic.toString()}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun connectMqtt (serverUri: String, username: String = "", password: String = "") {
        val options = MqttConnectOptions()
        options.userName = username
        options.password = password.toCharArray()

        mqttClient = MqttAndroidClient(requireContext(), serverUri, java.util.UUID.randomUUID().toString())
        mqttClient.setCallback(this)
        mqttClient.connect(options, null, object: IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                binding.brokerStatus.text = "Client Status: CONNECTED (${serverUri})"
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                binding.brokerStatus.text = "Client Status: DISCONNECTED (${serverUri})"
            }
        })
    }

    override fun connectionLost(cause: Throwable?) {
        Log.d(this.javaClass.name, "Connection lost ${cause.toString()}")
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        val message = "[${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}] (${topic}) ${message.toString()}\n"
        val text = message + binding.messageLog.text.toString()
        binding.messageLog.text = text
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        Log.d(this.javaClass.name, "Delivery completed")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mqttClient.isConnected) {
            mqttClient.disconnect()
            mqttClient.unregisterResources()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PubSubDebugFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PubSubDebugFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}