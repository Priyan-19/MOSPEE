# MOSPEE 🚗💨 — Premium Automotive Tracking

**MOSPEE** is a high-performance, premium-tech Android GPS speedometer and trip tracker. Designed with a sophisticated "Premium Light" aesthetic, it combines precision GPS engine logic with stunning 3D visualizations and a robust hybrid cloud-sync engine.

![MOSPEE Branding](app/src/main/res/drawable/ic_launcher_foreground.xml)

---

## 💎 Premium Features

| Feature | Description |
|:---|:---|
| **Premium Light Tech UI** | A sophisticated design language using `MospeeCream` backgrounds and `MospeeTerracotta` accents. |
| **Hybrid Storage System** | Keep your last 10 trips locally for instant access, while syncing your entire history to the cloud. |
| **Route Compression** | Uses Google Polyline Encoding to compress complex GPS routes by over 90% for efficient storage. |
| **3D Speedometer** | Real-time 3D perspective gauge with kinetic needle physics and dynamic lighting. |
| **HUD Mode** | Head-Up Display mode flips the display for windshield projection during night drives. |
| **Background Resiliency** | A robust Foreground Service ensures tracking continues even with the screen off. |
| **Intelligent Filtering** | Advanced noise reduction logic filters out GPS jumps and unrealistic speed spikes. |
| **Interactive Maps** | High-performance OpenStreetMap integration for live route tracking and history review. |

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
│   │   ├── local/         # Room Database (Last 10 trips)
│   │   ├── remote/        # Firebase Firestore & Auth
│   │   └── repository/    # Repository Implementations (Sync Logic)
│   ├── domain/            # Domain Layer (Business Logic)
│   │   ├── model/         # Plain Data Models
│   │   └── usecase/       # Logic for Sync, Decryption, etc.
│   ├── ui/                # UI Layer (Jetpack Compose)
│   │   ├── home/          # Home Screen (Split-view architecture)
│   │   ├── trip/          # Live Tracking & Speedometer
│   │   ├── summary/       # Post-trip Analytics
│   │   ├── history/       # Saved Trip List (Cloud Sync Status)
│   │   └── theme/         # Design System & Token definitions
│   └── utils/             # Polyline Encoding, GPS Logic, Constants
```

---

## 🚀 Technical Stack

| Layer | Technology |
|:---|:---|
| **Language** | Kotlin 2.0 |
| **Cloud DB** | Firebase Firestore (Real-time sync) |
| **Auth** | Firebase Anonymous Authentication |
| **Local DB** | Room (SQLite abstraction for local persistence) |
| **UI Framework** | Jetpack Compose (Declarative UI) |
| **Maps** | OSMdroid & Google Maps Utils (Polyline Encoding) |
| **DI** | Dagger Hilt (Dependency Injection) |
| **Reactive** | Kotlin Coroutines & StateFlow |

---

## 🔄 Hybrid Storage & Sync Logic

MOSPEE uses a sophisticated two-tier storage system to balance performance and reliability:

1.  **Local (Room)**: Stores only the **last 10 trips** for instantaneous loading and offline access.
2.  **Cloud (Firestore)**: Stores your **entire history**. Every trip is automatically synced once a connection is available.
3.  **Compression**: Routes are encoded into **Google Polyline Strings** before transmission, ensuring minimal data usage.
4.  **Resiliency**: If a sync fails, the app marks the trip as `Pending` and retries automatically on the next app launch.

---

## ⚙️ Configuration & Setup

To enable the cloud features, you must add your Firebase configuration:

1. Create a Firebase project in the [Firebase Console](https://console.firebase.google.com/).
2. Enable **Anonymous Authentication** and **Firestore Database**.
3. Place your `google-services.json` in the `app/` directory.

---

## 🔐 Permissions & Privacy

*   `ACCESS_FINE_LOCATION`: Required for high-precision speedometer metrics.
*   `ACCESS_BACKGROUND_LOCATION`: Enables tracking while the app is in the background.
*   `FOREGROUND_SERVICE`: Keeps the GPS engine alive during long trips.
*   `POST_NOTIFICATIONS`: Shows a persistent speed tile in the notification shade.

---

## 📝 License

Distributed under the **MIT License**. Use it, modify it, build something great.

*Crafted with 🏎️ for the automotive enthusiast.*
