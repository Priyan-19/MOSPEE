# MOSPEE 🚗💨 — Premium Automotive Tracking

**MOSPEE** is a high-performance, premium-tech Android GPS speedometer and trip tracker. Designed with a sophisticated "Premium Light" aesthetic, it combines precision GPS engine logic with stunning 3D visualizations and automotive-grade UI components.

![MOSPEE Branding](app/src/main/res/drawable/ic_launcher_foreground.xml)

---

## 💎 Premium Features

| Feature | Description |
|:---|:---|
| **Premium Light Tech UI** | A sophisticated design language using `MospeeCream` backgrounds and `MospeeTerracotta` accents. |
| **3D Speedometer** | Real-time 3D perspective gauge with kinetic needle physics and dynamic lighting. |
| **HUD Mode** | Head-Up Display mode flips the display for windshield projection during night drives. |
| **Pin + Speed Branding** | Custom branding integrated across the app with a unique identity badge. |
| **Background Resiliency** | A robust Foreground Service ensures tracking continues even with the screen off. |
| **Intelligent Filtering** | Advanced noise reduction logic filters out GPS jumps and unrealistic speed spikes. |
| **Interactive Maps** | High-performance OpenStreetMap (via OSMdroid) integration for live route tracking and history review. |
| **Comprehensive Analytics** | Track distance, duration, top speed, and average speed with high-precision metrics. |

---

## 🎨 Design Philosophy: "Premium Light Tech"

MOSPEE moves away from generic dark modes to a curated "Light Tech" palette that feels premium and professional.

*   **Primary Palette**: 
    *   `MospeeCream` (`#FDFCF6`): A warm, easy-on-the-eyes background.
    *   `MospeeTerracotta` (`#E65D3C`): A sophisticated, high-visibility accent color.
    *   `MospeeTerracottaLight` (`#FFF0ED`): Subtle surface tones for cards and UI grouping.
*   **Aesthetics**: Glassmorphism, smooth shadows, and micro-animations (like the glowing speed threshold) provide a state-of-the-art user experience.

---

## 🏗️ Project Architecture

Built with **MVVM + Clean Architecture**, the project is highly modular and maintainable.

```
MOSPEE/
├── app/src/main/kotlin/com/mospee/
│   ├── data/              # Data Layer
│   │   ├── local/         # Room Database, DAOs, Entities
│   │   └── repository/    # Repository Implementations
│   ├── domain/            # Domain Layer (Business Logic)
│   │   ├── model/         # Plain Data Models
│   │   └── repository/    # Repository Interfaces
│   ├── service/           # LocationForegroundService (The Engine)
│   ├── ui/                # UI Layer (Jetpack Compose)
│   │   ├── home/          # Home Screen (Split-view architecture)
│   │   ├── trip/          # Live Tracking & Speedometer
│   │   ├── summary/       # Post-trip Analytics
│   │   ├── history/       # Saved Trip List
│   │   ├── components/    # Reusable Premium UI Components
│   │   └── theme/         # Design System & Token definitions
│   └── utils/             # Constants, Formatters, & GPS Logic
```

---

## 🚀 Technical Stack

| Layer | Technology |
|:---|:---|
| **Language** | Kotlin 2.0 (Modern, expressive, and safe) |
| **UI Framework** | Jetpack Compose (Declarative UI) |
| **Design** | Material 3 (Themed with MOSPEE Premium tokens) |
| **DI** | Dagger Hilt (Dependency Injection) |
| **Database** | Room (SQLite abstraction for local persistence) |
| **Reactive** | Kotlin Coroutines & StateFlow (Clean async handling) |
| **Maps** | OSMdroid (OpenStreetMap integration — No API keys required) |
| **Settings** | DataStore Preferences (Typed, reactive preferences) |
| **Navigation** | Compose Navigation (Type-safe route management) |

---

## 🔐 Permissions & Privacy

MOSPEE prioritizes user privacy and battery efficiency.

*   `ACCESS_FINE_LOCATION`: Required for high-precision speedometer metrics.
*   `ACCESS_BACKGROUND_LOCATION`: Enables tracking while the app is in the background.
*   `FOREGROUND_SERVICE`: Keeps the GPS engine alive during long trips.
*   `POST_NOTIFICATIONS`: Shows a persistent speed tile in the notification shade.

*Tracking is local-only; your trip data NEVER leaves your device.*

---

## 🔧 Location Filtering Engine

The core of MOSPEE is its robust filtering logic located in `LocationUtils.kt`:

1.  **Accuracy Filter**: Ignores points with accuracy > 30m to prevent jitter.
2.  **Speed Filter**: Discards unrealistic spikes above 240 km/h (adjustable via Constants).
3.  **Teleport Filter**: Detects and ignores "jumps" where distance between points is physically impossible in the given time.
4.  **Displacement Filter**: Ignores small movements (< 5m) to save battery while stationary.

---

## 🧪 Development & Testing

### Simulated Driving
Use the Android Emulator's **Extended Controls** to load a GPX file or manually move the location. Click "Play Route" to see MOSPEE track speed and distance in real-time.

### Build Instructions
1. Open the project in Android Studio (Ladybug or newer recommended).
2. Sync Gradle.
3. Run the `app` module on an Android 8.0+ device or emulator.

---

## 📝 License

Distributed under the **MIT License**. Use it, modify it, build something great.

*Crafted with 🏎️ for the automotive enthusiast.*
