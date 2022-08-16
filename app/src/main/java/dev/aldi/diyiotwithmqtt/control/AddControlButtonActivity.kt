package dev.aldi.diyiotwithmqtt.control

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import dev.aldi.diyiotwithmqtt.MainActivity
import dev.aldi.diyiotwithmqtt.MyApplication
import dev.aldi.diyiotwithmqtt.dao.ButtonDao
import dev.aldi.diyiotwithmqtt.dao.ControlDao
import dev.aldi.diyiotwithmqtt.databinding.ActivityAddControlButtonBinding
import dev.aldi.diyiotwithmqtt.entity.Button
import dev.aldi.diyiotwithmqtt.entity.Control
import kotlinx.coroutines.launch

class AddControlButtonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddControlButtonBinding
    private lateinit var controlDao: ControlDao
    private lateinit var buttonDao: ButtonDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddControlButtonBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        controlDao = (this.application as MyApplication).database.controlDao()
        buttonDao = (this.application as MyApplication).database.buttonDao()

        binding.addControl.setOnClickListener {
            val controlName = binding.controlName.text.toString()
            val payload = binding.payload.text.toString()
            val subscribeTopic = binding.subscribeTopic.text.toString()
            val publishTopic = binding.publishTopic.text.toString()
            val isRetain = binding.isRetain.isChecked

            val control = Control(
                id = 0,
                name = controlName,
                type = "Button",
                subscribeTopic = subscribeTopic,
                publishTopic = publishTopic,
                is_retain = isRetain
            )

            lifecycleScope.launch {
                val insertedControl = controlDao.insert(control)
                val button = Button(0, insertedControl.toInt(), payload)
                buttonDao.save(button)
                startActivity(Intent(this@AddControlButtonActivity, MainActivity::class.java))
                Toast.makeText(this@AddControlButtonActivity, "Success add control", Toast.LENGTH_LONG).show()
            }
        }
    }
}