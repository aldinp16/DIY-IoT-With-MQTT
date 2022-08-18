package dev.aldi.diyiotwithmqtt.control

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import dev.aldi.diyiotwithmqtt.MyApplication
import dev.aldi.diyiotwithmqtt.dao.BrokerDao
import dev.aldi.diyiotwithmqtt.dao.ButtonDao
import dev.aldi.diyiotwithmqtt.dao.ControlDao
import dev.aldi.diyiotwithmqtt.databinding.ActivityControlButtonBinding
import dev.aldi.diyiotwithmqtt.entity.Broker
import dev.aldi.diyiotwithmqtt.entity.Button
import dev.aldi.diyiotwithmqtt.entity.Control
import kotlinx.coroutines.launch
import info.mqtt.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.*
import java.time.Instant
import java.time.format.DateTimeFormatter

class ControlButtonActivity : AppCompatActivity(), IMqttActionListener, MqttCallback {

    private lateinit var binding: ActivityControlButtonBinding

    private lateinit var mqttClient: MqttAndroidClient
    private lateinit var brokerDao: BrokerDao
    private lateinit var controlDao: ControlDao
    private lateinit var buttonDao: ButtonDao

    private lateinit var broker: Broker
    private lateinit var control: Control
    private lateinit var button: Button
    private lateinit var serverUri: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityControlButtonBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.pubLog.movementMethod = ScrollingMovementMethod()
        binding.subLog.movementMethod = ScrollingMovementMethod()

        val controlId = intent.extras?.getInt("control_id")

        binding.publishTopic.keyListener = null
        binding.subscribeTopic.keyListener = null

        brokerDao = (this.application as MyApplication).database.brokerDao()
        controlDao = (this.application as MyApplication).database.controlDao()
        buttonDao = (this.application as MyApplication).database.buttonDao()

        lifecycleScope.launch {
            broker = brokerDao.get()!!
            control = controlDao.findByUid(controlId!!)
            button = buttonDao.get(controlId)

            binding.toolbar.title = control.name.toString()

            binding.subscribeTopic.setText(control.subscribeTopic)
            binding.publishTopic.setText(control.publishTopic)
            connectMqtt()
        }

        binding.triggerButton.setOnClickListener {
            val message = MqttMessage()
            message.payload = button.payload.toString().toByteArray()
            message.qos = 2
            message.isRetained = control.is_retain
            mqttClient.publish(control.publishTopic.toString(), message, null, object: IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    val message = "[${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}] (${control.publishTopic.toString()}) ${button.payload.toString()}\n"
                    val text = message + binding.pubLog.text.toString()
                    binding.pubLog.text = text
                    Toast.makeText(this@ControlButtonActivity, "Success", Toast.LENGTH_LONG).show()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Toast.makeText(this@ControlButtonActivity, "Failed", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun connectMqtt () {
        serverUri = "tcp://${broker.host.toString()}:${broker.port.toString()}"
        mqttClient = MqttAndroidClient(this, serverUri, java.util.UUID.randomUUID().toString())
        val options = MqttConnectOptions()
        options.userName = broker.username.toString()
        options.password = broker.password.toString().toCharArray()
        mqttClient.setCallback(this)
        mqttClient.connect(options, null, this)
    }

    override fun onSuccess(asyncActionToken: IMqttToken?) {
        binding.clientStatus.text = "Client Status: CONNECTED (${serverUri})"
        mqttClient.subscribe(control.subscribeTopic.toString(), 2)
    }

    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
        binding.clientStatus.text = "Client Status: DISCONNECTED (${serverUri})"
    }

    override fun connectionLost(cause: Throwable?) {
        binding.clientStatus.text = "Client Status: DISCONNECTED (${serverUri})"
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        val message = "[${DateTimeFormatter.ISO_INSTANT.format(Instant.now())}] (${topic}) ${message.toString()}\n"
        val text = message + binding.subLog.text.toString()
        binding.subLog.text = text
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mqttClient.isConnected) {
            mqttClient.disconnect()
            mqttClient.unregisterResources()
        }
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {}
}