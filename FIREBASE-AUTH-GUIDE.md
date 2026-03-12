# Firebase Authentication Integration Guide

## 🔥 Firebase + Spring Boot Authentication System

This guide explains how to set up Firebase OTP authentication with your Spring Boot backend.

---

## 📋 Architecture Overview

```
Frontend (React/Web) 
    ↓ (Firebase OTP)
Firebase Authentication
    ↓ (ID Token)
Spring Boot Backend
    ↓ (Verify Token via Firebase Admin SDK)
Generate JWT Token
    ↓
Store User in PostgreSQL
    ↓
Return JWT to Frontend
```

---

## 🚀 Setup Instructions

### 1. Firebase Project Setup

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project: **smartcart011**
3. Go to **Project Settings** → **Service Accounts**
4. Click **Generate New Private Key**
5. Download the JSON file (keep it secure!)

### 2. Add Firebase Service Account to Your Project

**Option A: For Local Development**
```bash
# Set environment variable with path to service account JSON
export GOOGLE_APPLICATION_CREDENTIALS="/path/to/serviceAccountKey.json"
```

**Option B: For Render Deployment**
1. Copy the entire contents of your service account JSON file
2. In Render dashboard, add Environment Variable:
   - Key: `FIREBASE_SERVICE_ACCOUNT`
   - Value: Paste the entire JSON content (as a single line)

**Option C: For Docker/Production**
```bash
# Add to your environment or .env file
FIREBASE_SERVICE_ACCOUNT='{"type":"service_account","project_id":"smartcart011",...}'
```

### 3. Install Dependencies

Dependencies are already added to `pom.xml`. Just run:
```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The app will start on `http://localhost:8080`

---

## 📡 API Endpoints

### 1. Verify Firebase Token and Authenticate

**Endpoint:** `POST /api/auth/verify-token`

**Request:**
```json
{
  "idToken": "firebase_id_token_from_frontend"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "User authenticated successfully",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "firebaseUid": "abc123xyz",
    "phoneNumber": "+919876543210",
    "email": "user@example.com",
    "displayName": "John Doe",
    "isNewUser": false
  }
}
```

**Error Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid Firebase token: Token expired"
}
```

### 2. Alternative Login Endpoint

**Endpoint:** `POST /api/auth/firebase-login`

Same as `/verify-token` - just an alias for convenience.

### 3. Get User Profile (Protected)

**Endpoint:** `GET /api/auth/profile`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "success": true,
  "user": {
    "id": 1,
    "firebaseUid": "abc123xyz",
    "phoneNumber": "+919876543210",
    "email": "user@example.com",
    "displayName": "John Doe",
    "createdAt": "2026-03-12T10:00:00",
    "lastLogin": "2026-03-12T12:30:00"
  }
}
```

### 4. Health Check

**Endpoint:** `GET /api/auth/health`

**Response:**
```json
{
  "status": "OK",
  "message": "Authentication service is running"
}
```

---

## 🔐 Protected Endpoints

The following endpoints now require JWT authentication:

- `POST /api/cart` - Add to cart
- `GET /api/cart` - View cart
- `PUT /api/cart/{id}` - Update cart
- `DELETE /api/cart/{id}` - Remove from cart
- `POST /api/orders` - Create order
- `GET /api/orders` - View orders

**How to access protected endpoints:**
```javascript
// Include JWT token in Authorization header
fetch('http://localhost:8080/api/cart', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${jwtToken}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(cartData)
});
```

---

## 💻 Frontend Integration

### Step 1: Initialize Firebase in React

```javascript
// src/firebase.js
import { initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';

const firebaseConfig = {
  apiKey: "AIzaSyB5154_H8TzGpSWsf0lYe4foywElhlikvg",
  authDomain: "smartcart011.firebaseapp.com",
  projectId: "smartcart011",
  storageBucket: "smartcart011.firebasestorage.app",
  messagingSenderId: "698998012915",
  appId: "1:698998012915:web:6f8bdf5a497e0475d63086",
  measurementId: "G-N02NCZXVLD"
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
```

### Step 2: Implement OTP Login

```javascript
// src/components/Login.jsx
import { useState } from 'react';
import { auth } from '../firebase';
import { RecaptchaVerifier, signInWithPhoneNumber } from 'firebase/auth';
import axios from 'axios';

function Login() {
  const [phoneNumber, setPhoneNumber] = useState('');
  const [otp, setOtp] = useState('');
  const [verificationId, setVerificationId] = useState(null);
  const [step, setStep] = useState(1); // 1: phone, 2: otp

  // Setup reCAPTCHA
  const setupRecaptcha = () => {
    if (!window.recaptchaVerifier) {
      window.recaptchaVerifier = new RecaptchaVerifier(
        'recaptcha-container',
        { size: 'invisible' },
        auth
      );
    }
  };

  // Send OTP
  const sendOTP = async () => {
    try {
      setupRecaptcha();
      const appVerifier = window.recaptchaVerifier;
      const confirmationResult = await signInWithPhoneNumber(
        auth,
        phoneNumber,
        appVerifier
      );
      setVerificationId(confirmationResult);
      setStep(2);
      alert('OTP sent successfully!');
    } catch (error) {
      console.error('Error sending OTP:', error);
      alert('Failed to send OTP: ' + error.message);
    }
  };

  // Verify OTP and authenticate with backend
  const verifyOTP = async () => {
    try {
      // Verify OTP with Firebase
      const result = await verificationId.confirm(otp);
      const user = result.user;
      
      // Get Firebase ID token
      const idToken = await user.getIdToken();
      
      // Send to backend for verification and JWT generation
      const response = await axios.post(
        'http://localhost:8080/api/auth/verify-token',
        { idToken }
      );
      
      if (response.data.success) {
        // Store JWT token
        localStorage.setItem('jwtToken', response.data.token);
        localStorage.setItem('user', JSON.stringify(response.data.user));
        
        alert('Login successful!');
        // Redirect to dashboard
        window.location.href = '/dashboard';
      }
    } catch (error) {
      console.error('Error verifying OTP:', error);
      alert('Invalid OTP or authentication failed');
    }
  };

  return (
    <div>
      <div id="recaptcha-container"></div>
      
      {step === 1 && (
        <div>
          <h2>Login with Phone Number</h2>
          <input
            type="tel"
            placeholder="+919876543210"
            value={phoneNumber}
            onChange={(e) => setPhoneNumber(e.target.value)}
          />
          <button onClick={sendOTP}>Send OTP</button>
        </div>
      )}
      
      {step === 2 && (
        <div>
          <h2>Enter OTP</h2>
          <input
            type="text"
            placeholder="123456"
            value={otp}
            onChange={(e) => setOtp(e.target.value)}
          />
          <button onClick={verifyOTP}>Verify OTP</button>
        </div>
      )}
    </div>
  );
}

export default Login;
```

### Step 3: Create API Service with JWT

```javascript
// src/services/api.js
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

// Add JWT token to all requests
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('jwtToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Handle token expiration
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('jwtToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### Step 4: Use API Service

```javascript
// Example: Add to cart
import api from './services/api';

const addToCart = async (productId, quantity) => {
  try {
    const response = await api.post('/cart', {
      userId: getUserId(), // Get from stored user data
      productId,
      quantity
    });
    console.log('Added to cart:', response.data);
  } catch (error) {
    console.error('Failed to add to cart:', error);
  }
};

// Example: Get user profile
const getProfile = async () => {
  try {
    const response = await api.get('/auth/profile');
    console.log('User profile:', response.data);
  } catch (error) {
    console.error('Failed to fetch profile:', error);
  }
};
```

---

## 🗄️ Database Schema

The `auth_users` table is automatically created:

```sql
CREATE TABLE auth_users (
  id BIGSERIAL PRIMARY KEY,
  firebase_uid VARCHAR(255) UNIQUE NOT NULL,
  phone_number VARCHAR(20) UNIQUE,
  email VARCHAR(255),
  display_name VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_login TIMESTAMP,
  is_active BOOLEAN DEFAULT TRUE
);
```

---

## 🔒 Security Features

✅ **Firebase ID Token Verification** - Backend verifies tokens using Firebase Admin SDK  
✅ **JWT Token Generation** - Secure session tokens for API access  
✅ **Stateless Authentication** - No server-side sessions  
✅ **Token Expiration** - JWT tokens expire after 24 hours (configurable)  
✅ **Protected Endpoints** - Cart and Order APIs require authentication  
✅ **CORS Enabled** - Frontend can access backend from any origin  
✅ **User Tracking** - Last login and creation timestamps  

---

## ⚙️ Configuration

### Environment Variables

```bash
# JWT Secret (256-bit minimum)
JWT_SECRET=SmartCart2026SecureSecretKeyForJWTTokenGenerationMustBe256BitsOrMore

# JWT Expiration (milliseconds)
JWT_EXPIRATION=86400000  # 24 hours

# Firebase Service Account (JSON as string)
FIREBASE_SERVICE_ACCOUNT='{"type":"service_account",...}'

# Or use file path
GOOGLE_APPLICATION_CREDENTIALS=/path/to/serviceAccountKey.json
```

### Update JWT Secret (Recommended for Production)

Generate a strong secret:
```bash
openssl rand -base64 32
```

Set it in Render environment variables or application.properties.

---

## 🧪 Testing

### Test with cURL

```bash
# 1. Get Firebase ID token from frontend console
# After successful OTP login, run in browser console:
firebase.auth().currentUser.getIdToken().then(console.log)

# 2. Authenticate with backend
curl -X POST http://localhost:8080/api/auth/verify-token \
  -H "Content-Type: application/json" \
  -d '{"idToken":"YOUR_FIREBASE_ID_TOKEN"}'

# 3. Use JWT token for protected endpoints
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

---

## 🚨 Troubleshooting

### Error: "Firebase not initialized"
- Make sure service account JSON is properly configured
- Check FIREBASE_SERVICE_ACCOUNT or GOOGLE_APPLICATION_CREDENTIALS

### Error: "Invalid token"
- Token might be expired
- Verify token is sent in correct format: `Bearer <token>`

### Error: "User not found"
- User might not be authenticated
- Check JWT token is valid

### CORS issues
- Make sure CORS is enabled in SecurityConfig
- Check frontend is sending requests to correct URL

---

## 📚 Additional Resources

- [Firebase Admin SDK Documentation](https://firebase.google.com/docs/admin/setup)
- [Firebase Phone Authentication](https://firebase.google.com/docs/auth/web/phone-auth)
- [JWT.io](https://jwt.io/) - Decode and verify JWT tokens
- [Spring Security Documentation](https://spring.io/projects/spring-security)

---

## ✨ Summary

Your SmartCart application now has:
- ✅ Firebase OTP authentication
- ✅ Backend token verification
- ✅ JWT token generation
- ✅ Protected API endpoints
- ✅ User management in PostgreSQL
- ✅ Secure session handling
- ✅ Frontend integration ready

Users can now login with their phone number via OTP and securely access all SmartCart features! 🎉
