package com.cartrack.challenge.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.cartrack.challenge.R
import com.cartrack.challenge.api.crimes.dto.CrimeModel
import com.cartrack.challenge.ui.adapters.CrimeListAdapter
import kotlinx.android.synthetic.main.activity_crime_data_list.*

class CrimeDataListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crime_data_list)

        val crimeDataList = intent.getParcelableArrayListExtra<CrimeModel.CrimeData>("crime_list")

        rcv_crime_list.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        val adapter = CrimeListAdapter(this,crimeDataList)
        rcv_crime_list.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

}