package com.vgmkeymaster.nativeapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var waterfallView: WaterfallSurfaceView
    private lateinit var btnPlayPause: Button
    private lateinit var btnHandMode: Button
    private lateinit var btnLoop: Button
    private lateinit var tvTitle: TextView
    private var midiHardwareManager: MidiHardwareManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        waterfallView = findViewById(R.id.waterfallView)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        btnHandMode = findViewById(R.id.btnHandMode)
        btnLoop = findViewById(R.id.btnLoop)
        tvTitle = findViewById(R.id.tvTitle)

        btnPlayPause.setOnClickListener {
            waterfallView.isPlaying = !waterfallView.isPlaying
            btnPlayPause.text = if (waterfallView.isPlaying) "pause" else "play"
        }

        btnHandMode.setOnClickListener {
            waterfallView.handMode = when (waterfallView.handMode) {
                "both" -> "right"
                "right" -> "left"
                else -> "both"
            }
            btnHandMode.text = when (waterfallView.handMode) {
                "right" -> "rh (app plays LH)"
                "left" -> "lh (app plays RH)"
                else -> "both hands"
            }
        }

        btnLoop.setOnClickListener {
            waterfallView.loopEnabled = !waterfallView.loopEnabled
            btnLoop.text = if (waterfallView.loopEnabled) "loop: on" else "loop: off"
        }

        // Initialize Native USB-OTG MIDI Driver
        midiHardwareManager = MidiHardwareManager(this) { pitch, isNoteOn, velocity ->
            if (isNoteOn) {
                // Interactive feedback on Yamaha YDP-143R key press
            }
        }
        midiHardwareManager?.initialize()
    }

    override fun onDestroy() {
        super.onDestroy()
        midiHardwareManager?.close()
    }
}
