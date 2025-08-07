# CraysCircle 📱

A modern Android application for peer-to-peer communication using Wi-Fi Aware technology. Connect with nearby devices instantly without internet connectivity.

[![Android](https://img.shields.io/badge/Android-API%2024+-green.svg)](https://developer.android.com/about/versions/14)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-blue.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5+-purple.svg)](https://developer.android.com/jetpack/compose)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 👨‍💻 Developer

**Vivekanand Pandey** - [LinkedIn](https://www.linkedin.com/in/itsvnp/)

- **Role**: Lead Android Developer
- **Expertise**: Kotlin, Jetpack Compose, Wi-Fi Aware, Android Architecture
- **Contact**: [LinkedIn Profile](https://www.linkedin.com/in/itsvnp/)

## 🌟 Features

### 🔗 **Peer-to-Peer Communication**
- **Wi-Fi Aware Technology**: Connect with nearby devices without internet
- **Real-time Messaging**: Instant message delivery with status tracking
- **Distance Estimation**: See approximate distance to connected peers
- **Auto-Connect**: Automatically connect to discovered devices

### 👤 **User Profiles**
- **Custom Avatars**: Choose from 15 unique emoji avatars
- **Personal Information**: Set nickname, bio, interests, and contact details
- **Profile Customization**: Complete profile setup with preferences
- **Privacy-First**: All data stays on your device

### 🎨 **Modern UI/UX**
- **Material Design 3**: Latest Material Design components and theming
- **Dark/Light Theme**: Automatic theme switching based on system preferences
- **Responsive Design**: Optimized for various screen sizes
- **Smooth Animations**: Fluid transitions and micro-interactions

### 🔒 **Security & Privacy**
- **No Server Required**: Direct device-to-device communication
- **Local Data Storage**: All messages and profiles stored locally
- **Permission-Based**: Minimal required permissions for functionality
- **Secure Communication**: Encrypted peer-to-peer messaging

## 📱 Screenshots

*Screenshots will be added here*

## 🏗️ Architecture

### **Technology Stack**
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with Repository pattern
- **Database**: Room Database with Kotlin Coroutines
- **Networking**: Wi-Fi Aware API
- **Build System**: Gradle with Kotlin DSL

### **Project Structure**
```
app/src/main/java/me/vivekanand/crayscircle/
├── chat/                    # Chat functionality
│   ├── ChatMessage.kt      # Message data models
│   └── ChatScreen.kt       # Chat UI components
├── data/                   # Data layer
│   ├── ChatDatabase.kt     # Room database setup
│   ├── UserProfile.kt      # User profile models
│   └── UserPreferences.kt  # DataStore preferences
├── setup/                  # Onboarding
│   └── WelcomeScreen.kt    # Welcome and onboarding UI
├── ui/                     # UI components
│   ├── Buttons.kt         # Custom button components
│   ├── ProfileScreen.kt   # Profile management UI
│   └── theme/             # App theming
│       ├── Color.kt       # Color definitions
│       ├── Shapes.kt      # Shape definitions
│       ├── Theme.kt       # Theme configuration
│       └── Type.kt        # Typography definitions
├── wifi/                   # Wi-Fi functionality
│   ├── PeerDevice.kt      # Peer device models
│   └── WifiAwareController.kt # Wi-Fi Aware implementation
├── MainActivity.kt         # Main activity
└── NearbyDevicesScreen.kt  # Device discovery UI
```

## 🚀 Getting Started

### **Prerequisites**
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK API 34 (Android 14)
- Kotlin 1.9.0 or later
- Device with Wi-Fi Aware support (Android 8.0+)

### **Installation**

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/CraysCircle.git
   cd CraysCircle
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned directory and select it

3. **Sync and Build**
   - Wait for Gradle sync to complete
   - Build the project: `Build > Make Project`
   - Run on device: `Run > Run 'app'`

### **Required Permissions**
The app requires the following permissions:
- `ACCESS_FINE_LOCATION` - For device discovery
- `ACCESS_WIFI_STATE` - For Wi-Fi status monitoring
- `CHANGE_WIFI_STATE` - For Wi-Fi configuration
- `NEARBY_WIFI_DEVICES` - For Wi-Fi Aware functionality (Android 13+)

## 🔧 Configuration

### **Build Configuration**
The app uses the following key configurations:

```kotlin
// app/build.gradle.kts
android {
    compileSdk = 34
    targetSdk = 34
    minSdk = 26 // Android 8.0 for Wi-Fi Aware support
}
```

### **Dependencies**
Key dependencies include:
- **Jetpack Compose**: UI framework
- **Room**: Local database
- **DataStore**: Preferences storage
- **Coroutines**: Asynchronous programming
- **Accompanist**: Permission handling

## 📊 Usage

### **First Launch**
1. **Welcome Screen**: Introduction to app features
2. **Onboarding**: Step-by-step setup guide
3. **Permissions**: Grant required permissions
4. **Profile Setup**: Create your user profile

### **Discovering Peers**
1. **Enable Wi-Fi**: Ensure Wi-Fi is turned on
2. **Start Discovery**: App automatically scans for nearby devices
3. **View Peers**: See discovered devices with distance information
4. **Connect**: Tap on a peer to start chatting

### **Chatting**
1. **Send Messages**: Type and send messages instantly
2. **Message Status**: Track delivery and read status
3. **Real-time**: Messages appear in real-time
4. **History**: View chat history for connected peers

## 🛠️ Development

### **Building for Development**
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test
```

### **Code Style**
- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Follow Material Design guidelines for UI

### **Testing**
- Unit tests for data layer
- Instrumented tests for UI components
- Manual testing for Wi-Fi Aware functionality

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Commit your changes**: `git commit -m 'Add amazing feature'`
4. **Push to the branch**: `git push origin feature/amazing-feature`
5. **Open a Pull Request**

### **Development Guidelines**
- Follow the existing code style
- Add tests for new functionality
- Update documentation as needed
- Ensure all tests pass before submitting

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Google**: For Wi-Fi Aware API and Material Design
- **JetBrains**: For Kotlin language and Android Studio
- **Android Community**: For excellent documentation and support

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/yourusername/CraysCircle/issues)
- **Discussions**: [GitHub Discussions](https://github.com/yourusername/CraysCircle/discussions)
- **Developer Contact**: [Vivekanand Pandey](https://www.linkedin.com/in/itsvnp/)

## 🔮 Roadmap

- [ ] **Group Chats**: Multi-peer communication
- [ ] **File Sharing**: Send images and documents
- [ ] **Voice Messages**: Audio message support
- [ ] **Offline Mode**: Enhanced offline functionality
- [ ] **Web Dashboard**: Web interface for management
- [ ] **Cross-Platform**: iOS companion app

---

**Developed with ❤️ by [Vivekanand Pandey](https://www.linkedin.com/in/itsvnp/)**

*Connect. Communicate. Collaborate.* 