#  Animal Species Management App

## 📌 Overview
This Android application, built with **Kotlin**, manages animal species while monitoring **temperature** and **humidity** using sensors. It ensures that species are kept within optimal environmental conditions. The app supports **real-time data storage with Firebase** and **offline access through an internal database**.

## 📌 Features
-  **Temperature & Humidity Monitoring**: Reads data from device sensors.
-  **Threshold Alerts**: Sends notifications when environmental conditions exceed safe levels.
-  **Cloud Storage**: Stores species data externally using **Firebase Firestore**.
-  **Offline Mode**: Uses an **internal SQLite database** to store data locally.
-  **Permissions Management**: Requests and handles permissions for notifications.

## 📌 Tech Stack
- **Language**: Kotlin
- **Database**: Firebase Firestore (Cloud) + SQLite (Local)
- **Sensors**: Android Sensor 
- **Notifications**: Android Notification API

## 📌 Setup & Installation
1. Clone the repository:
   ```sh
   git clone https://github.com/RIKIEL-K/animal-species-app.git
   cd animal-species-app
