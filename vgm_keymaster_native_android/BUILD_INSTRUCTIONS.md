# VGM KeyMaster Native - Android APK Compilation Guide

*(Pure Ground-Up Native Android Edition — 100% Kotlin & Hardware SurfaceView)*

This guide explains how to compile and run **VGM KeyMaster Native**, built from the ground up as a native Android application (with **zero browser, zero WebView, and zero web layers**), matching the architecture used by applications like Synthesia.

---

## Why This Native Version Does Not Lag

* **Hardware SurfaceView Rendering**: Instead of rendering on an HTML5 2D Canvas in a browser, this app uses a native Android `SurfaceView` running on a dedicated background thread (`Thread.MAX_PRIORITY`). It draws directly into Android's hardware surface buffers using `lockHardwareCanvas()`, completely bypassing the UI thread.
* **Native PCM Audio (`AudioTrack`)**: Acoustic piano notes are generated directly as raw 16-bit PCM waveform audio buffers at 44.1 kHz on a native audio thread, with sub-5ms latency and zero garbage collection pauses.
* **Direct USB-OTG MIDI**: Uses Android's native `android.media.midi` API to read note-on and note-off events directly from USB kernel packets.

---

## Method 1: Compile with Android Studio (Recommended)

1. **Extract the Project**:
   * Unzip `vgm_keymaster_native_android.zip` on your computer.

2. **Open in Android Studio**:
   * Open Android Studio.
   * Click **File** > **Open** and select the unzipped `vgm_keymaster_native_android` folder.
   * Android Studio will sync the Gradle project automatically (`compileSdk 34`, `minSdk 26`).

3. **Connect Your Tablet & Run**:
   * Enable **Developer Options** and **USB Debugging** on your Samsung Galaxy Tab S9 FE+.
   * Connect your tablet to your computer via USB.
   * Select your Samsung Galaxy Tab S9 FE+ from the device dropdown at the top of Android Studio.
   * Click the green **Run (▶)** button (or press `Shift + F10`).
   * The app will compile and launch natively on your tablet in fullscreen landscape mode.

4. **Generate Standalone APK**:
   * Click **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
   * Once finished, click **locate** in the notification popup.
   * Your installable `app-debug.apk` will be located at:
     `app/build/outputs/apk/debug/app-debug.apk`
   * Transfer this `.apk` to your tablet (via USB, Google Drive, or messaging) and tap to install.

---

## Method 2: Command-Line Build (Fastest with Terminal)

If you have the Android SDK and command-line tools installed:

1. Open your terminal in the extracted `vgm_keymaster_native_android` directory:
   ```bash
   cd vgm_keymaster_native_android
   ```

2. Compile the debug APK:
   * **Linux / macOS**:
     ```bash
     ./gradlew assembleDebug
     ```
   * **Windows (PowerShell / Command Prompt)**:
     ```bash
     .\gradlew.bat assembleDebug
     ```

3. Install directly to your connected tablet:
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

---

## Method 3: Automated Cloud Compilation (GitHub Actions — No Laptop Needed)

If you want an APK built in the cloud:

1. Create a free repository on [github.com](https://github.com).
2. Upload the files from `vgm_keymaster_native_android.zip`.
3. Add a GitHub Actions workflow `.github/workflows/build-native-apk.yml`:
   ```yaml
   name: Build Native Android APK
   on: [push, workflow_dispatch]
   jobs:
     build:
       runs-on: ubuntu-latest
       steps:
         - uses: actions/checkout@v4
         - name: Set up JDK 17
           uses: actions/setup-java@v4
           with:
             java-version: '17'
             distribution: 'temurin'
         - name: Build Debug APK
           run: |
             chmod +x gradlew
             ./gradlew assembleDebug
         - name: Upload APK Artifact
           uses: actions/upload-artifact@v4
           with:
             name: VGM-KeyMaster-Native-APK
             path: app/build/outputs/apk/debug/app-debug.apk
   ```
4. Download the generated `app-debug.apk` from the **Actions** tab on your phone or tablet.