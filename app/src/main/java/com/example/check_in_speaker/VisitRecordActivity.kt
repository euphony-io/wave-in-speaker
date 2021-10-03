package com.example.check_in_speaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.check_in_speaker.adapter.VisitRecordAdapter
import com.example.check_in_speaker.databinding.ActivityVisitRecordBinding
import com.example.check_in_speaker.db.User
import com.example.check_in_speaker.viewmodel.MainViewModel
import com.example.check_in_speaker.viewmodel.UserViewModelFactory

class VisitRecordActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels {
        UserViewModelFactory((application as MainApplication).repository)
    }
    private lateinit var binding: ActivityVisitRecordBinding
    private lateinit var visitRecordAdapter: VisitRecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisitRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        initViewModel()
    }

    private fun initToolbar(){
        setSupportActionBar(binding.tbVisitRecord)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
    }

    private fun initRecyclerView(){
        binding.rvVisitRecordList.apply {
            adapter = visitRecordAdapter
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(context, 1))
        }
    }

    private fun initViewModel(){
        viewModel.allUser.observe(this, {
            if(it.isNotEmpty()){
                visitRecordAdapter = VisitRecordAdapter(it as ArrayList<User>)
                initRecyclerView()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}