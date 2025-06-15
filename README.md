WearOS Metronome is a minimalist and functional metronome app designed specifically for smartwatches running Wear OS. It provides accurate tempo keeping directly on the user's wrist, making it ideal for musicians during practice or live sessions without relying on a smartphone.

This project is part of a Final Thesis Project for a Double Degree in Computer Engineering and Business Administration. It includes not only the design and development of the smartwatch application but also the creation of technical and business documentation for the simulation of a real innovation funding process.

Features
  - Set tempo between 30 and 240 BPM
  - Continuous and precise beat ticking
  - Simple and intuitive user interface optimized for circular Wear OS displays
  - Supports both dark and light modes
  - Native development using Jetpack Compose for Wear OS

Technologies Used
  - Kotlin
  - Jetpack Compose for Wear OS
  - Android Studio
  - Samsung One UI Watch design guidelines
  - Figma (UI/UX design)

Figma Designs Evolution Screenshots
![Metronome App](https://github.com/user-attachments/assets/e4a1d123-c36f-4eca-a355-501def3275b5)


Designed following official wearable design principles for consistency and usability.

Getting Started
Prerequisites
  - Android Studio Hedgehog or later
  - Wear OS Emulator or compatible smartwatch (e.g., Samsung Galaxy Watch with Wear OS 3)
  - Minimum SDK: 30

Installation
1. Clone this repository:
  git clone https://github.com/dvazquezteba/WearOS-Metronome.git
  cd WearOS-Metronome
  
2. Open the project in Android Studio.

3. Connect a physical Wear OS device or start an emulator.

4. Build and run the project.

Project Structure
├── MainActivity.kt         // Entry point and navigation
├── MetronomeScreen.kt      // UI for tempo control
├── SettingsScreen.kt       // User settings and preferences
├── components/             // Reusable composable UI elements

Future Improvements
  - Haptic feedback for beats
  - Optional sound click on each beat
  - AI-powered tempo correction (in progress)
  - Custom time signatures
  - Visual feedback with animated pulses
