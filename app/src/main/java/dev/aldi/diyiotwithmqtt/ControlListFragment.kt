package dev.aldi.diyiotwithmqtt

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import dev.aldi.diyiotwithmqtt.adapter.ControlListAdapter
import dev.aldi.diyiotwithmqtt.control.ControlButtonActivity
import dev.aldi.diyiotwithmqtt.control.ControlSwitchActivity
import dev.aldi.diyiotwithmqtt.databinding.FragmentControlListBinding
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ControlListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ControlListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentControlListBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentControlListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val database = (requireActivity().application as MyApplication).database

        viewLifecycleOwner.lifecycleScope.launch {
            val controls = database.controlDao().getAll()
            val adapter = ControlListAdapter(controls)
            binding.rvControlList.adapter = adapter
            binding.rvControlList.setOnItemClickListener { _, _, i, _ ->
                val control = adapter.getItem(i)

                var intent: Intent? = null
                when(control.type) {
                    "Button" -> intent = Intent(context, ControlButtonActivity::class.java)
                    "Switch" -> intent = Intent(context, ControlSwitchActivity::class.java)
                }

                intent?.putExtra("control_id", control.id)
                startActivity(intent)
            }
        }

        binding.addControl.setOnClickListener {
            AddControlDialogFragment().show(childFragmentManager, "Add Control Dialog")
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ControlListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ControlListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}