package com.example.challenge.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.challenge.api.crimes.dto.CrimeModel
import com.example.challenge.databinding.ActivityCrimeDataListBinding
import com.example.challenge.ui.adapters.CrimeListAdapter
import java.util.ArrayList


class CrimeDataListActivity : BaseActivity() {

    lateinit var mBinding : ActivityCrimeDataListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCrimeDataListBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val crimeDataList = intent.getParcelableArrayListExtra<CrimeModel.CrimeData>("crime_list")
        supportActionBar?.title = "Crimes (${crimeDataList.size})"
        setUpRecyclerView(crimeDataList)
    }

    private fun setUpRecyclerView(crimeDataList: ArrayList<CrimeModel.CrimeData>?) {
        mBinding.rcvCrimeList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        crimeDataList?.let {
            val adapter = CrimeListAdapter(this, crimeDataList)
            mBinding.rcvCrimeList.adapter = adapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> super.onBackPressed()
        }
        return true
    }

}