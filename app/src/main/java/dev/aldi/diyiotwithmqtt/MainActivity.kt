package dev.aldi.diyiotwithmqtt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import dev.aldi.diyiotwithmqtt.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, binding.drawer, R.string.nav_open, R.string.nav_close)
        binding.drawer.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.menu.setNavigationItemSelectedListener(this)

        replaceFragment(ControlListFragment())
        listenMQtt()
    }

    private fun replaceFragment (fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.menu_fragment, fragment)
        transaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.broker_setting_menu -> replaceFragment(BrokerSettingFragment())
            R.id.control_list_menu -> replaceFragment(ControlListFragment())
            R.id.pub_sub_debug -> replaceFragment(PubSubDebugFragment())
        }

        return true
    }
}

fun listenMQtt() {
    val client = MqttClient.builder()
        .useMqttVersion3()
        .serverHost("172.104.63.254")
        .identifier(UUID.randomUUID().toString())
        .buildAsync()

    client
        .connectWith()
        .simpleAuth()
        .username("admin")
        .password("public".toByteArray())
        .applySimpleAuth()
        .send()

    client.subscribeWith()
        .topicFilter("test-topic/device1")
        .qos(MqttQos.EXACTLY_ONCE)
        .callback { publish ->
            Log.d("MQTT", String(publish.payloadAsBytes))
        }
        .send()
}