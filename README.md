# MOSPEE 🏎️💨 — Premium Automotive Performance Tracker

**MOSPEE** is a state-of-the-art Android GPS telemetry application engineered for precision, performance, and visual excellence. Built using a modern Kotlin-first stack, it provides real-time speed analysis, trip historical mapping, and a seamless cloud-sync experience, all wrapped in a high-contrast "Dark Tech" interface.

---

## 💎 Core Capabilities

### 📡 High-Fidelity Tracking
- **Precision Telemetry**: Real-time speed, distance, and duration tracking using Android's location services with high-accuracy configurations.
- **Background Resiliency**: Integrated **Foreground Service** keeps the GPS engine alive even when the app is minimized or the screen is locked, ensuring no data loss during long drives.
- **Intelligent Noise Filtering**: Custom logic to filter out GPS inaccuracies, jumps, and unrealistic speed spikes for clean metrics.

### 📊 Performance Visualization
- **Kinetic Speedometer**: A custom-built gauge component featuring fluid needle physics, digital readouts, and high-contrast markers designed for rapid glanceability.
- **Dynamic Mapping**: Live route visualization using **OSMdroid**, allowing drivers to see their trail in real-time.
- **Post-Trip Analytics**: Comprehensive summaries including top speed, average speed, elevation metrics, and path visualization.

### 🔄 Hybrid Data Engine
- **Local-First Architecture**: Powered by **Room Persistence**, caching the last 10 trips for instantaneous offline access.
- **Cloud historical Storage**: Automatic synchronization to **Firebase Firestore** for long-term data persistence across devices.
- **Polyline Encoding**: Uses Google's Polyline Algorithm to compress coordinate paths by >90% before cloud transmission, drastically reducing bandwidth and storage usage.
- **Startup Sync**: On every launch, the app checks for pending trips and synchronizes them to the cloud in the background.

---

## 🎨 Design Philosophy: "Premium Dark Tech"

MOSPEE's aesthetic is inspired by modern high-end automotive instrument clusters.

- **Contrast-First UI**: Deep blacks (`#0C0E14`) paired with high-visibility accents (Racing Orange `#FF4D00` and Electric Green `#00E676`).
- **Glanceable Metrics**: Bold typography (Inter Black) ensures critical information is readable under varying light conditions.
- **Tactile Feedback**: Subtle micro-animations and transition effects provide a premium, reactive feel to every interaction.

---

## 🏗️ Architecture & Implementation

The codebase adheres to **Clean Architecture** and **MVVM** principles, ensuring a separation of concerns and high testability.

### 📂 Directory Structure
```
MOSPEE/
├── app/src/main/kotlin/com/mospee/
│   ├── data/              # Implementation of Repositories & Data Sources
│   │   ├── local/         # Room Database & DataStore (Preferences)
│   │   └── remote/        # Firebase Manager & Firestore Logic
│   ├── domain/            # Pure Business Logic (Use Cases & Entities)
│   │   ├── model/         # Core Trip & Location Models
│   │   └── usecase/       # Start/Stop/Sync Business Logic
│   ├── service/           # Android Foreground Service for Tracking
│   ├── ui/                # Jetpack Compose UI Layer
│   │   ├── components/    # Reusable Gauges, Buttons, and Cards
│   │   ├── navigation/    # Typed Compose Navigation Graph
│   │   └── screens/       # Home, Trip, Summary, History, Statistics
│   └── utils/             # Polyline Encoding, GPS Filtering, Date Formatting
```

---

## 🚀 Technical Stack

| Layer | Component | Description |
|:---|:---|:---|
| **Language** | Kotlin 2.0 | Using latest Coroutines & StateFlow for reactivity. |
| **UI** | Jetpack Compose | Declarative UI with a custom Material3-based Design System. |
| **DI** | Hilt | Robust dependency injection for decoupled components. |
| **Persistence** | Room | Local SQLite storage for high-performance caching. |
| **Backend** | Firebase | Firestore for NoSQL storage & Anonymous Auth for identity. |
| **Maps** | OSMdroid | Offline-capable, high-performance map rendering. |
| **Location** | Google Play Services | Precision Fused Location Provider integration. |

---

## ⚙️ Configuration & Setup

1. **Firebase Integration**:
   - Create a project at [Firebase Console](https://console.firebase.google.com/).
   - Enable **Anonymous Authentication** and **Cloud Firestore**.
   - Place `google-services.json` in the `/app` folder.
2. **IDE**: Use **Android Studio Ladybug (2024.2.1)** or higher.
3. **Build**: Run `./gradlew assembleDebug` to generate a development build.

---

## 🔐 Permissions
The application requires the following to function correctly:
- `ACCESS_FINE_LOCATION`: Core GPS metric collection.
- `FOREGROUND_SERVICE`: Ensuring tracking continuity in the background.
- `POST_NOTIFICATIONS`: Displaying a persistent "Live Trip" status tile.

---

## 📝 License
Distributed under the **MIT License**.

*Crafted with precision for the performance driver.*
