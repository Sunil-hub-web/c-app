package com.net.capp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomDialogFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_dialog, container, false)

        val generalNews = view.findViewById<TextView>(R.id.general_news)
        generalNews.setOnClickListener {
            val intent = Intent(requireContext(), PostActivity::class.java)
            intent.putExtra("groupid", "1")
            startActivity(intent)
        }
        val coronaUpdate = view.findViewById<TextView>(R.id.corona_update)
        coronaUpdate.setOnClickListener {
            val intent = Intent(requireContext(), PostActivity::class.java)
            intent.putExtra("groupid", "2")
            startActivity(intent)
        }
        val politics = view.findViewById<TextView>(R.id.politics)
        politics.setOnClickListener {
            val intent = Intent(requireContext(), PostActivity::class.java)
            intent.putExtra("groupid", "4")
            startActivity(intent)
        }
        val police = view.findViewById<TextView>(R.id.police_station)
        police.setOnClickListener {
            val intent = Intent(requireContext(), PostActivity::class.java)
            intent.putExtra("groupid", "5")
            startActivity(intent)
        }
        val stateUpdate = view.findViewById<TextView>(R.id.state_update)
        stateUpdate.setOnClickListener {
            val intent = Intent(requireContext(), PostActivity::class.java)
            intent.putExtra("groupid", "6")
            startActivity(intent)
        }
        val localUpdate = view.findViewById<TextView>(R.id.local_update)
        localUpdate.setOnClickListener {
            val intent = Intent(requireContext(), PostActivity::class.java)
            intent.putExtra("groupid", "7")
            startActivity(intent)
        }
        val education = view.findViewById<TextView>(R.id.education)
        education.setOnClickListener {
            val intent = Intent(requireContext(), PostActivity::class.java)
            intent.putExtra("groupid", "3")
            startActivity(intent)
        }
        return view
    }
}