package com.vgmkeymaster.nativeapp

data class PianoNote(
    val pitch: Int,       // MIDI pitch 21 to 108
    val time: Float,      // Seconds
    val duration: Float,  // Seconds
    val hand: String,     // "lh" or "rh"
    var sounded: Boolean = false,
    var hit: Boolean = false
)

object SongRepository {
    // Great Fairy Fountain (BOTW) with unified 4-note LH chords and cascading RH arpeggios
    val greatFairyFountain = listOf(
        // Measure 1 - Db Major 9 / F chord in LH + Db5/F5/Ab5 arpeggios in RH
        PianoNote(49, 0.0f, 2.4f, "lh"), // Db3
        PianoNote(56, 0.0f, 2.4f, "lh"), // Ab3
        PianoNote(65, 0.0f, 2.4f, "lh"), // F4 (cohesive LH chord)
        PianoNote(68, 0.0f, 2.4f, "lh"), // Ab4 (cohesive LH chord)

        PianoNote(73, 0.0f, 0.15f, "rh"), // Db5
        PianoNote(77, 0.15f, 0.15f, "rh"), // F5
        PianoNote(80, 0.30f, 0.15f, "rh"), // Ab5
        PianoNote(85, 0.45f, 0.15f, "rh"), // Db6
        PianoNote(80, 0.60f, 0.15f, "rh"), // Ab5
        PianoNote(77, 0.75f, 0.15f, "rh"), // F5
        PianoNote(73, 0.90f, 0.15f, "rh"), // Db5
        PianoNote(68, 1.05f, 0.15f, "rh"), // Ab4
        PianoNote(73, 1.20f, 0.15f, "rh"), // Db5
        PianoNote(77, 1.35f, 0.15f, "rh"), // F5
        PianoNote(80, 1.50f, 0.15f, "rh"), // Ab5
        PianoNote(85, 1.65f, 0.15f, "rh"), // Db6
        PianoNote(80, 1.80f, 0.15f, "rh"), // Ab5
        PianoNote(77, 1.95f, 0.15f, "rh"), // F5
        PianoNote(73, 2.10f, 0.15f, "rh"), // Db5
        PianoNote(68, 2.25f, 0.15f, "rh"), // Ab4

        // Measure 2 - Bb Minor 9 / Db in LH + Bb/Db/F arpeggio in RH
        PianoNote(46, 2.4f, 2.4f, "lh"), // Bb2
        PianoNote(53, 2.4f, 2.4f, "lh"), // F3
        PianoNote(65, 2.4f, 2.4f, "lh"), // F4
        PianoNote(68, 2.4f, 2.4f, "lh"), // Ab4

        PianoNote(70, 2.40f, 0.15f, "rh"), // Bb4
        PianoNote(73, 2.55f, 0.15f, "rh"), // Db5
        PianoNote(77, 2.70f, 0.15f, "rh"), // F5
        PianoNote(82, 2.85f, 0.15f, "rh"), // Bb5
        PianoNote(77, 3.00f, 0.15f, "rh"), // F5
        PianoNote(73, 3.15f, 0.15f, "rh"), // Db5
        PianoNote(70, 3.30f, 0.15f, "rh"), // Bb4
        PianoNote(65, 3.45f, 0.15f, "rh"), // F4
        PianoNote(70, 3.60f, 0.15f, "rh"), // Bb4
        PianoNote(73, 3.75f, 0.15f, "rh"), // Db5
        PianoNote(77, 3.90f, 0.15f, "rh"), // F5
        PianoNote(82, 4.05f, 0.15f, "rh"), // Bb5
        PianoNote(77, 4.20f, 0.15f, "rh"), // F5
        PianoNote(73, 4.35f, 0.15f, "rh"), // Db5
        PianoNote(70, 4.50f, 0.15f, "rh"), // Bb4
        PianoNote(65, 4.65f, 0.15f, "rh")  // F4
    )
}
