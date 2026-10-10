package com.vgmkeymaster.nativeapp

import android.content.Context
import android.media.midi.MidiDevice
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager
import android.media.midi.MidiOutputPort
import android.media.midi.MidiReceiver
import android.os.Handler
import android.os.Looper
import android.util.Log

class MidiHardwareManager(
    private val context: Context,
    private val onNoteEvent: (pitch: Int, isNoteOn: Boolean, velocity: Int) -> Unit
) {
    private val midiManager = context.getSystemService(Context.MIDI_SERVICE) as? MidiManager
    private var openDevice: MidiDevice? = null
    private var outputPort: MidiOutputPort? = null

    fun initialize() {
        val devices = midiManager?.devices ?: return
        for (info in devices) {
            val name = info.properties.getString(MidiDeviceInfo.PROPERTY_NAME) ?: ""
            Log.d("VGM_MIDI", "Discovered MIDI Device: ")
            openDevice(info)
            break
        }
    }

    private fun openDevice(info: MidiDeviceInfo) {
        midiManager?.openDevice(info, { device ->
            openDevice = device
            val portCount = info.outputPortCount
            if (portCount > 0) {
                outputPort = device.openOutputPort(0)
                outputPort?.connect(object : MidiReceiver() {
                    override fun onSend(msg: ByteArray, offset: Int, count: Int, timestamp: Long) {
                        for (i in offset until (offset + count)) {
                            val status = msg[i].toInt() and 0xFF
                            val cmd = status and 0xF0
                            if (cmd == 0x90 || cmd == 0x80) {
                                if (i + 2 < offset + count) {
                                    val pitch = msg[i + 1].toInt() and 0x7F
                                    val vel = msg[i + 2].toInt() and 0x7F
                                    val isNoteOn = (cmd == 0x90) && (vel > 0)
                                    Handler(Looper.getMainLooper()).post {
                                        onNoteEvent(pitch, isNoteOn, vel)
                                    }
                                }
                            }
                        }
                    }
                })
            }
        }, Handler(Looper.getMainLooper()))
    }

    fun close() {
        outputPort?.close()
        openDevice?.close()
    }
}
