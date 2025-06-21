package com.speedDrawer.speed_drawer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.speedDrawer.speed_drawer.databinding.ActivityAppOptionsBinding

class AppOptionsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityAppOptionsBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize view binding
        binding = ActivityAppOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Setup click listeners using view binding
        binding.settingsButton.setOnClickListener {
            // Handle settings button click
            // TODO: Implement settings functionality
        }
        
        binding.aboutButton.setOnClickListener {
            // Handle about button click
            // TODO: Implement about functionality
        }
    }
} 