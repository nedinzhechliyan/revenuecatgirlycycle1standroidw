<div align="center">

# 🌸 GirlyCycle

### Perimenopause Symptom Tracker for Gen X Women

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![RevenueCat](https://img.shields.io/badge/RevenueCat-Subscriptions-F25A5A?logo=data:image/svg+xml;base64,&logoColor=white)](https://www.revenuecat.com)
[![Firebase](https://img.shields.io/badge/Firebase-Backend-FFCA28?logo=firebase&logoColor=black)](https://firebase.google.com)

*A beautifully designed Android app empowering women (ages 45–60) to track perimenopause symptoms, monitor cycles, generate doctor visit reports, and gain actionable health insights — powered by Gemini AI.*

</div>

---

## ✨ Features

| Feature | Description |
|---------|-------------|
| 📝 **Daily Symptom Logging** | Track hot flashes, mood changes, sleep quality, and 30+ perimenopause symptoms |
| 📅 **Cycle Tracking** | Monitor irregular periods with smart predictions and calendar views |
| 🩺 **Doctor Visit Reports** | Auto-generate comprehensive reports to share with your healthcare provider |
| 💡 **AI-Powered Insights** | Personalized, actionable health insights powered by the Gemini API |
| 👥 **Community Screen** | Connect with other women on the same journey |
| 🎨 **Onboarding Flow** | Guided setup to personalize the experience from day one |
| 💎 **Premium Subscriptions** | In-app purchases managed via RevenueCat for premium features |
| ⚙️ **Profile & Settings** | Customize notifications, preferences, and account settings |

---

## 🏗️ Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose + Material 3
- **Architecture:** MVVM (ViewModel + Repository pattern)
- **Local Database:** Room (with KSP annotation processing)
- **Networking:** Retrofit + OkHttp + Moshi
- **AI Integration:** Firebase AI (Gemini API)
- **Monetization:** RevenueCat SDK for in-app subscriptions
- **Backend Services:** Firebase (App Check, Google Services)
- **Build System:** Gradle (Kotlin DSL) with Version Catalogs

---

## 📂 Project Structure

```
GirlyCycle/
├── app/
│   └── src/main/java/com/example/
│       ├── MainActivity.kt              # App entry point
│       ├── MainApplication.kt           # Application class (RevenueCat init)
│       ├── data/
│       │   ├── local/                    # Room database & DAOs
│       │   ├── model/                    # Data models & entities
│       │   └── repository/              # Data repositories
│       ├── domain/
│       │   ├── DoctorReportGenerator.kt  # Doctor visit report generation
│       │   └── RuleBasedInsightsEngine.kt # Health insights engine
│       └── ui/
│           ├── components/               # Reusable UI components
│           ├── navigation/               # Navigation graph & routes
│           ├── screens/
│           │   ├── HomeScreen.kt         # Main dashboard
│           │   ├── TrackScreen.kt        # Daily symptom logging
│           │   ├── InsightsScreen.kt     # AI-powered health insights
│           │   ├── DoctorReportScreen.kt # Doctor visit report viewer
│           │   ├── CommunityScreen.kt    # Community features
│           │   ├── OnboardingScreen.kt   # First-time user setup
│           │   ├── PaywallScreen.kt      # RevenueCat subscription paywall
│           │   └── ProfileSettingsScreen.kt # User settings
│           ├── theme/                    # Material 3 theming
│           └── viewmodel/               # ViewModels (MVVM)
├── .env.example                          # Environment variable template
├── build.gradle.kts                      # Root build configuration
├── settings.gradle.kts                   # Gradle settings
└── metadata.json                         # App metadata
```

---

## 🚀 Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (latest stable)
- Android SDK 24+ (target SDK 36)
- JDK 11+

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/nedinzhechliyan/revenuecatgirlycycle1standroidw.git
   cd revenuecatgirlycycle1standroidw
   ```

2. **Open in Android Studio**
   - Select **File → Open** and choose the project directory
   - Allow Android Studio to sync Gradle and resolve dependencies

3. **Configure environment variables**
   - Copy `.env.example` to `.env` in the project root
   - Set your API keys:
     ```env
     GEMINI_API_KEY=your_gemini_api_key_here
     REVENUECAT_API_KEY=your_revenuecat_api_key_here
     ```

4. **Run the app**
   - Select an emulator or connect a physical device
   - Click **Run ▶️** or use `Shift + F10`

> **Note:** For local development, you may need to remove the release signing config line from `app/build.gradle.kts`:
> ```
> signingConfig = signingConfigs.getByName("debugConfig")
> ```

---

## 🔑 RevenueCat Integration

This app uses [RevenueCat](https://www.revenuecat.com/) for managing in-app subscriptions:

- **SDK:** `com.revenuecat.purchases` 
- **Paywall:** Built-in paywall screen with subscription offerings
- **Initialization:** Configured in `MainApplication.kt` on app startup
- **API Key:** Stored securely via environment variables (`.env`)

---

## 🤖 Gemini AI Integration

The app leverages Google's **Gemini AI** via Firebase AI SDK to provide:

- Personalized health insights based on logged symptoms
- Smart pattern recognition across symptom data
- Natural language doctor visit report generation

---

## 📄 License

This project is for educational and demonstration purposes.

---

<div align="center">

**Built with 💜 for women navigating perimenopause**

</div>
