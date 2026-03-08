# 📚 BookShelf

An Android application for managing and storing your book collection online. Built with Kotlin and modern Android development practices.

## ✨ Features

- **User Authentication**: Secure login and account creation with Firebase Authentication
- **Book Management**: Create, edit, and delete books in your personal library
- **Book Details**: View comprehensive information about each book
- **My Books**: Access your personal book collection
- **Downloads**: Manage downloaded books for offline reading
- **Top Rated**: Discover highly-rated books
- **Recent Books**: Browse recently added books
- **Search**: Find books quickly with integrated search functionality
- **Contact Us**: Get in touch with support via multiple channels (SMS, Call, WhatsApp, Telegram)
- **Profile Management**: Manage your user profile and preferences
- **Offline Support**: Local database with Room for offline access
- **Cloud Sync**: Firebase Firestore and Storage integration for cloud backup

## 🛠️ Tech Stack

### Core Technologies
- **Language**: Kotlin
- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Build System**: Gradle

### Architecture & Libraries
- **Architecture Components**:
  - MVVM Pattern
  - LiveData & ViewModel
  - Navigation Component
  - Data Binding
  
- **Database**:
  - Room Database (v2.6.0-beta01)
  - Kotlin Coroutines for async operations

- **Firebase Services**:
  - Firebase Authentication
  - Firebase Firestore
  - Firebase Realtime Database
  - Firebase Storage
  - Firebase Analytics
  - Google Sign-In

- **UI Components**:
  - Material Design Components
  - Navigation Drawer
  - Fragments
  - RecyclerView
  - ConstraintLayout

- **Image Loading**:
  - Glide (v4.13.2)

- **Background Processing**:
  - WorkManager (v2.8.1)
  - Kotlin Coroutines (v1.7.1)

- **Dependency Injection**:
  - Kotlin KSP (Kotlin Symbol Processing)

## 📋 Prerequisites

- Android Studio (latest version recommended)
- JDK 17
- Android SDK 34
- Firebase account and project setup
- `google-services.json` file from Firebase Console

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/ermais/BookShelf.git
cd BookShelf
```

### 2. Firebase Setup
1. Create a Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Add an Android app to your Firebase project
3. Download the `google-services.json` file
4. Place it in the `app/` directory
5. Enable the following Firebase services:
   - Authentication (Email/Password & Google Sign-In)
   - Firestore Database
   - Realtime Database
   - Storage

### 3. Build and Run
1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an emulator or physical device

## 📱 App Structure

```
app/
├── src/main/
│   ├── java/com/example/bookshelf/
│   │   ├── ui/
│   │   │   ├── login/          # Login screen
│   │   │   ├── createaccount/  # Account creation
│   │   │   ├── main/           # Main activity with navigation
│   │   │   ├── home/           # Home fragment
│   │   │   ├── bookdetail/     # Book details
│   │   │   ├── createbook/     # Create new book
│   │   │   ├── editbook/       # Edit existing book
│   │   │   ├── mybook/         # User's book collection
│   │   │   ├── downloads/      # Downloaded books
│   │   │   ├── toprated/       # Top rated books
│   │   │   ├── recent/         # Recent books
│   │   │   ├── profile/        # User profile
│   │   │   └── contactus/      # Contact support
│   │   ├── data/               # Data layer (Room, repositories)
│   │   ├── model/              # Data models
│   │   └── util/               # Utility classes
│   ├── res/
│   │   ├── layout/             # XML layouts
│   │   ├── navigation/         # Navigation graphs
│   │   ├── menu/               # Menu resources
│   │   └── values/             # Strings, colors, themes
│   └── AndroidManifest.xml
```

## 🔑 Key Permissions

The app requires the following permissions:
- `INTERNET` - For network operations
- `ACCESS_NETWORK_STATE` - Check network connectivity
- `READ_EXTERNAL_STORAGE` - Read book files
- `WRITE_EXTERNAL_STORAGE` - Save downloaded books
- `READ_MEDIA_IMAGES` - Access images (Android 13+)
- `CALL_PHONE` - Contact support via phone
- `SEND_SMS` - Contact support via SMS
- `POST_NOTIFICATIONS` - Show notifications

## 🎨 Screenshots

<div align="center">

### Login & Authentication
![Login Screen](https://github.com/ermais/BookShelf/assets/33171889/c1a831ef-a20c-4464-a0f8-8e4d683d69fe)
![Create Account](https://github.com/ermais/BookShelf/assets/33171889/706fe57a-b20d-414a-a975-b0069e8d694e)

### Home & Navigation
![Home Dashboard](https://github.com/ermais/BookShelf/assets/33171889/1be2ef1c-bcb1-4cdf-aa13-ceb1aa599d8e)
![Navigation Drawer](https://github.com/ermais/BookShelf/assets/33171889/3180fc36-de0f-448f-8372-1b63dbc8d2b4)

### Book Management
![Book List](https://github.com/ermais/BookShelf/assets/33171889/0d87e35e-6be2-402a-9fbd-6f77ff4326bc)
![Book Details](https://github.com/ermais/BookShelf/assets/33171889/4c771c1d-c194-47e5-9db1-45cdbd467189)
![Create Book](https://github.com/ermais/BookShelf/assets/33171889/3c423d8f-a211-4db2-a4c0-701f1fa8df3e)
![Edit Book](https://github.com/ermais/BookShelf/assets/33171889/57f002e4-7458-4ff6-a601-b5d018cbc858)

### My Books & Features
![My Books Collection](https://github.com/ermais/BookShelf/assets/33171889/a67b0b58-86ae-4d2a-b548-c7dc2a7124ae)
![Top Rated Books](https://github.com/ermais/BookShelf/assets/33171889/0ca2b51b-08b7-4621-831a-108cba59bce4)
![Recent Books](https://github.com/ermais/BookShelf/assets/33171889/d578b1f0-fbc9-4dbf-a088-a92e470d2b8f)

### Profile & Support
![User Profile](https://github.com/ermais/BookShelf/assets/33171889/802b8bdc-2ef9-42ff-8f69-c3ba47eb00ef)
![Contact Us](https://github.com/ermais/BookShelf/assets/33171889/2d16bef7-3b36-4494-959e-3b171350b21d)

</div>

## 🌐 Localization

The app supports multiple languages:
- English (default)
- Amharic (አማርኛ)

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📄 License

This project is available for educational and personal use.

## 👤 Author

**Ermias**
- GitHub: [@ermais](https://github.com/ermais)

## 📞 Support

For support, please use the Contact Us feature in the app or reach out through:
- SMS
- Phone Call
- WhatsApp
- Telegram
- Email

## 🔄 Version History

- **v1.0** - Initial release
  - User authentication
  - Book CRUD operations
  - Cloud sync with Firebase
  - Offline support with Room
  - Download management

---

Made with ❤️ using Kotlin and Firebase
