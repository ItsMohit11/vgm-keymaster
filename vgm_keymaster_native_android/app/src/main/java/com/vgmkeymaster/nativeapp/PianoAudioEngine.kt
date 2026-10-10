package com.vgmkeymaster.nativeapp

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class PianoAudioEngine {
    private val sampleRate = 44100
    private var isRunning = true
    private val activeNotes = ConcurrentHashMap<Int, NoteState>()
    private var audioTrack: AudioTrack? = null
    private var audioThread: Thread? = null

    data class NoteState(
        val freq: Double,
        var sampleIndex: Long = 0,
        var isReleased: Boolean = false,
        var releaseSample: Long = 0
    )

    fun start() {
        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize * 2)
            .setPerformanceMode(AudioTrack.PERFORMANCE_MODE_LOW_LATENCY)
            .build()

        audioTrack?.play()

        audioThread = Thread {
            val chunk = ShortArray(512)
            while (isRunning) {
                for (i in chunk.indices) {
                    var sampleVal = 0.0
                    val it = activeNotes.entries.iterator()
                    while (it.hasNext()) {
                        val entry = it.next()
                        val state = entry.value
                        val t = state.sampleIndex.toDouble() / sampleRate
                        val freq = state.freq
                        
                        // Harmonic synthesis (Fundamental + 2nd + 3rd harmonic)
                        val fundamental = sin(2.0 * PI * freq * t)
                        val h2 = 0.4 * sin(2.0 * PI * freq * 2.0 * t)
                        val h3 = 0.2 * sin(2.0 * PI * freq * 3.0 * t)
                        val wave = (fundamental + h2 + h3)

                        // Exponential decay envelope
                        val decay = exp(-3.0 * t)
                        var amp = decay
                        if (state.isReleased) {
                            val relT = (state.sampleIndex - state.releaseSample).toDouble() / sampleRate
                            amp *= exp(-20.0 * relT)
                            if (relT > 0.15) {
                                it.remove()
                                continue
                            }
                        } else if (t > 4.0) {
                            it.remove()
                            continue
                        }

                        sampleVal += wave * amp * 0.35
                        state.sampleIndex++
                    }

                    val clamped = (sampleVal.coerceIn(-1.0, 1.0) * 32767.0).toInt().toShort()
                    chunk[i] = clamped
                }
                audioTrack?.write(chunk, 0, chunk.size)
            }
        }.apply {
            priority = Thread.MAX_PRIORITY
            start()
        }
    }

    fun playNote(pitch: Int) {
        val freq = 440.0 * Math.pow(2.0, (pitch - 69).toDouble() / 12.0)
        activeNotes[pitch] = NoteState(freq)
    }

    fun stopNote(pitch: Int) {
        activeNotes[pitch]?.let {
            it.isReleased = true
            it.releaseSample = it.sampleIndex
        }
    }

    fun stop() {
        isRunning = false
        audioThread?.join(200)
        audioTrack?.stop()
        audioTrack?.release()
    }
}
