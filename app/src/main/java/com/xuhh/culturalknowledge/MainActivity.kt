package com.xuhh.culturalknowledge

import com.xuhh.culturalknowledge.databinding.ActivityMainBinding
import com.xuhh.culturalknowledge.ui.base.BaseActivity

class MainActivity : BaseActivity<ActivityMainBinding>(){

    override fun initBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

}