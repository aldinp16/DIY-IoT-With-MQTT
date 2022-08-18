package dev.aldi.diyiotwithmqtt.control

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import dev.aldi.diyiotwithmqtt.MainActivity
import dev.aldi.diyiotwithmqtt.MyApplication
import dev.aldi.diyiotwithmqtt.dao.ControlDao
import dev.aldi.diyiotwithmqtt.dao.SwitchDao
import dev.aldi.diyiotwithmqtt.databinding.ActivityAddControlSwitchBinding
import dev.aldi.diyiotwithmqtt.entity.Switch
import dev.aldi.diyiotwithmqtt.entity.Control
import kotlinx.coroutines.launch

class AddControlSwitchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddControlSwitchBinding
    private lateinit var controlDao: ControlDao
    private lateinit var switchDao: SwitchDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddControlSwitchBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        controlDao = (this.application as MyApplication).database.controlDao()
        switchDao = (this.application as MyApplication).database.switchDao()

        binding.addControl.setOnClickListener {
            val controlName = binding.controlName.text.toString()
            val onPayload = binding.onPayload.text.toString()
            val offPayload = binding.offPayload.text.toString()
            val subscribeTopic = binding.subscribeTopic.text.toString()
            val publishTopic = binding.publishTopic.text.toString()
            val isRetain = binding.isRetain.isChecked

            val control = Control(
                id = 0,
                name = controlName,
                type = "Switch",
                subscribeTopic = subscribeTopic,
                publishTopic = publishTopic,
                is_retain = isRetain
            )

            lifecycleScope.launch {
                val insertedControl = controlDao.insert(control)
                val switch = Switch(0, insertedControl.toInt(), onPayload, offPayload)
                switchDao.save(switch)
                startActivity(Intent(this@AddControlSwitchActivity, MainActivity::class.java))
                Toast.makeText(this@AddControlSwitchActivity, "Success add control", Toast.LENGTH_LONG).show()
            }
        }
    }
}