# Firestore Database Setup Guide

## ⚠️ Important: Firestore Database Not Found Error

You're getting this error because the Firestore database hasn't been created yet:

```
Firestore: The database (default) does not exist for project just-for-fun-application
Please visit https://console.cloud.google.com/datastore/setup?project=just-for-fun-application to add a Cloud Datastore or Cloud Firestore database.
```

## 🔧 How to Fix:

### Step 1: Go to Firebase Console
1. Visit [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **just-for-fun-application**

### Step 2: Enable Firestore Database
1. In the left sidebar, click on **"Firestore Database"**
2. Click **"Create database"**
3. Choose **"Start in test mode"** (for development)
   - This allows read/write access for 30 days
   - You can change security rules later
4. Select a location for your database (choose closest to your users)
5. Click **"Done"**

### Step 3: Verify Setup
1. After creation, you should see the Firestore console
2. The database will be empty initially
3. Your app will now be able to connect to Firestore

### Step 4: Update Security Rules (Optional for Development)
If you want to keep test mode longer, go to:
1. **Firestore Database** → **Rules**
2. Use these rules for development:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Allow read/write access to all documents for authenticated users
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## 🎯 Result
After setting up Firestore:
- ✅ User signup will work properly
- ✅ User data will be saved to Firestore
- ✅ Profile page will display user information
- ✅ No more database errors in logs

## 📱 Test the App
1. Create Firestore database (steps above)
2. Run the app
3. Sign up with email/password
4. Check Profile tab - user info should display
5. Try logout functionality

The app should work perfectly after Firestore is set up!
