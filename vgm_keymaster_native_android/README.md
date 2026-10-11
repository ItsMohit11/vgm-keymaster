# VGM KeyMaster - Version 3.2 Update Notes

## Polyphonic Chord Cohesion & Full Left Hand Chord Preservation

This release resolves the issue where only a single note of a Left Hand chord was recognized as the left hand (while the remaining chord notes were mistakenly split into the right hand), specifically addressing pieces like *The Great Fairy Fountain* from *The Legend of Zelda: Breath of the Wild*.

---

### 1. The Issue: Why Chords Were Being Split
In piano arrangements like *The Great Fairy Fountain*:
- The Left Hand plays rich, sustained 3- or 4-note chords (e.g., $D\flat_3$, $A\flat_3$, $F_4$, $A\flat_4$), where upper chord tones extend into the fourth octave (pitches $65\text{–}70$).
- The Right Hand simultaneously plays rapid cascading 16th-note arpeggios in the high register ($D\flat_5\text{–}F_6$).
- When notes were evaluated individually by pitch threshold, the lower root was categorized as Left Hand (`lh`), but the upper notes of the exact same chord exceeded the threshold and were torn into the Right Hand (`rh`). On the screen, only 1 note appeared in blue on the Bass staff, while the rest of the chord was colored red and thrown onto the Treble staff.

---

### 2. The Solution: Polyphonic Chord Cohesion Algorithm

* **Time-Slice Harmonic Clustering**:
  - Notes starting within a $40\text{ms}$ window are grouped into simultaneous sonority clusters.
  - If a cluster begins with a bass/accompaniment note (lowest note $< 64$ or tagged LH), the algorithm identifies all harmonic notes voiced within a standard two-octave left-hand stretch ($\le 24$ semitones) and sustained duration ($\ge 0.7\text{s}$).
* **Register Gap Detection**:
  - Automatically identifies the natural register gap (typically $5\text{–}12$ semitones) separating the top of the Left Hand chord from the bottom of the Right Hand cascading arpeggios.
  - Every note within the lower harmonic chord cluster is assigned to the **Left Hand (`lh`)** in **Royal Ocean Azure (`#2563eb`)** and rendered on the **Bass staff** with proper ledger lines.
  - The upper arpeggios are preserved as **Right Hand (`rh`)** in **Radiant Ruby (`#e11d48`)**.
* **Automatic Re-evaluation in `loadSong`**:
  - The chord cohesion pass runs automatically whenever any song is loaded. If you previously uploaded a custom *Great Fairy Fountain* MIDI file that was saved in your browser, it is automatically corrected upon opening without needing to re-upload.
* **Built-in "Great Fairy Fountain (Zelda: BOTW)"**:
  - Added to the built-in library with full authentic 4-note Left Hand chords ($D\flat$ Maj, $E\flat$ Maj, $C$ Min, $F$ Min) and cascading Right Hand arpeggios ready for immediate practice.

---

### 3. Deliverables

* `vgm_keymaster_vintage.html`: Standalone web application with polyphonic chord cohesion, full Left Hand chord preservation, and *Great Fairy Fountain* built-in.
* `midi_hand_splitter.py`: Standalone Python utility updated with polyphonic chord cohesion.
* `vgm_keymaster_native_android.zip`: Pure Native Android Studio project updated with Great Fairy Fountain and chord preservation.
* `vgm_keymaster_android_project.zip`: Updated Capacitor project with synchronized assets.
* `apk_generation_instructions.md`: Guide for compiling the APKs.