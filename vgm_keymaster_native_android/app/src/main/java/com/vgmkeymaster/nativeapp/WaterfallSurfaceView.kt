package com.vgmkeymaster.nativeapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class WaterfallSurfaceView(context: Context, attrs: AttributeSet? = null) :
    SurfaceView(context, attrs), SurfaceHolder.Callback, Runnable {

    private var renderThread: Thread? = null
    private var isRunning = false
    private val audioEngine = PianoAudioEngine()

    var songNotes: List<PianoNote> = SongRepository.greatFairyFountain
    var songTime = 0.0f
    var isPlaying = false
    var loopEnabled = true
    var handMode = "both" // "both", "right", "left"

    // 1920x1200 hardware surface resolution lock (eliminates 3.07MP fill rate lag on Mali-G68 GPU)
    private val targetWidth = 1920
    private val targetHeight = 1200
    private val keyHeight = 180f
    private val noteTravelTime = 2.4f

    private val bgPaint = Paint().apply { color = Color.parseColor("#050814") }
    private val whiteKeyPaint = Paint().apply { color = Color.parseColor("#F8FAFC") }
    private val blackKeyPaint = Paint().apply { color = Color.parseColor("#1E293B") }
    private val keyBorderPaint = Paint().apply {
        color = Color.parseColor("#94A3B8")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val rhPaint = Paint().apply { color = Color.parseColor("#E11D48") } // Radiant Ruby
    private val lhPaint = Paint().apply { color = Color.parseColor("#2563EB") } // Royal Ocean Azure
    private val hitLinePaint = Paint().apply {
        color = Color.parseColor("#F59E0B")
        strokeWidth = 4f
    }

    init {
        holder.addCallback(this)
        // Hardware DPU scaling: render at 1920x1200, scaled to 2560x1600 hardware by Display Processor
        holder.setFixedSize(targetWidth, targetHeight)
        audioEngine.start()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isRunning = true
        renderThread = Thread(this).apply {
            priority = Thread.MAX_PRIORITY
            start()
        }
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isRunning = false
        renderThread?.join(200)
        audioEngine.stop()
    }

    override fun run() {
        var lastTime = System.nanoTime()
        val hitY = targetHeight - keyHeight

        while (isRunning) {
            val now = System.nanoTime()
            val dt = (now - lastTime) / 1_000_000_000.0f
            lastTime = now

            if (isPlaying) {
                songTime += dt
                // Audio sounding logic
                for (note in songNotes) {
                    if (songTime >= note.time && !note.sounded) {
                        note.sounded = true
                        val shouldAppPlay = when (handMode) {
                            "right" -> note.hand == "lh" // App plays LH
                            "left" -> note.hand == "rh"  // App plays RH
                            else -> true                 // App plays both
                        }
                        if (shouldAppPlay) {
                            audioEngine.playNote(note.pitch)
                        }
                    }
                    if (songTime >= note.time + note.duration && note.sounded && !note.hit) {
                        note.hit = true
                        audioEngine.stopNote(note.pitch)
                    }
                }

                // Loop handling
                if (songTime > 5.5f) {
                    if (loopEnabled) {
                        songTime = 0.0f
                        songNotes.forEach {
                            it.sounded = false
                            it.hit = false
                        }
                    } else {
                        isPlaying = false
                    }
                }
            }

            var canvas: Canvas? = null
            try {
                canvas = holder.lockHardwareCanvas()
                if (canvas != null) {
                    drawFrame(canvas, hitY)
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    private fun isBlackKey(pitch: Int): Boolean {
        val n = pitch % 12
        return n == 1 || n == 3 || n == 6 || n == 8 || n == 10
    }

    private fun getWhiteKeyIndex(pitch: Int): Int {
        var count = 0
        for (p in 21 until pitch) {
            if (!isBlackKey(p)) count++
        }
        return count
    }

    private fun drawFrame(canvas: Canvas, hitY: Float) {
        canvas.drawRect(0f, 0f, targetWidth.toFloat(), targetHeight.toFloat(), bgPaint)

        val totalWhiteKeys = 52
        val whiteKeyWidth = targetWidth.toFloat() / totalWhiteKeys

        // 1. Draw Hit Line
        canvas.drawLine(0f, hitY, targetWidth.toFloat(), hitY, hitLinePaint)

        // 2. Draw Falling Notes
        val speed = hitY / noteTravelTime
        for (note in songNotes) {
            val noteBottomY = hitY - (note.time - songTime) * speed
            val noteTopY = noteBottomY - (note.duration * speed)

            if (noteBottomY < 0f || noteTopY > targetHeight) continue

            val isBlack = isBlackKey(note.pitch)
            val whiteIdx = getWhiteKeyIndex(note.pitch)
            val noteX = if (!isBlack) {
                whiteIdx * whiteKeyWidth
            } else {
                whiteIdx * whiteKeyWidth + (whiteKeyWidth * 0.65f)
            }
            val noteW = if (!isBlack) whiteKeyWidth * 0.9f else whiteKeyWidth * 0.6f

            val paint = if (note.hand == "rh") rhPaint else lhPaint
            val rect = RectF(noteX, noteTopY.coerceAtLeast(0f), noteX + noteW, noteBottomY.coerceAtMost(hitY))
            canvas.drawRoundRect(rect, 8f, 8f, paint)
        }

        // 3. Draw Piano Keyboard (Unpressed during playback)
        for (i in 0 until totalWhiteKeys) {
            val x = i * whiteKeyWidth
            canvas.drawRect(x, hitY, x + whiteKeyWidth, targetHeight.toFloat(), whiteKeyPaint)
            canvas.drawRect(x, hitY, x + whiteKeyWidth, targetHeight.toFloat(), keyBorderPaint)
        }

        val blackKeyHeight = keyHeight * 0.62f
        val blackKeyWidth = whiteKeyWidth * 0.65f
        for (p in 21..108) {
            if (isBlackKey(p)) {
                val whiteIdx = getWhiteKeyIndex(p)
                val x = whiteIdx * whiteKeyWidth + (whiteKeyWidth - blackKeyWidth / 2f)
                canvas.drawRect(x, hitY, x + blackKeyWidth, hitY + blackKeyHeight, blackKeyPaint)
            }
        }
    }
}
