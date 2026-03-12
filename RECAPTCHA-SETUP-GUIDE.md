# Google reCAPTCHA Enterprise Integration Guide

This guide explains how to set up and use Google reCAPTCHA Enterprise in your Spring Boot application.

## ✅ What Has Been Implemented

### 1. Frontend Integration (login.html)
- Added reCAPTCHA Enterprise script
- Implemented JavaScript to get reCAPTCHA token on login
- Enhanced UI with better styling and error handling
- Added forgot password functionality with OTP display

### 2. Backend Integration

#### Files Created:
- `RecaptchaService.java` - Service to verify reCAPTCHA tokens with Google API
- `LoginRequest.java` - DTO for login requests including reCAPTCHA token

#### Files Modified:
- `UserController.java` - Updated login endpoint to verify reCAPTCHA
- `application.properties` - Added reCAPTCHA configuration
- `pom.xml` - Added Jackson dependency for JSON parsing

## 🔧 Configuration Steps

### Step 1: Get Your reCAPTCHA Keys

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create or select a project
3. Enable **reCAPTCHA Enterprise API**
4. Create a reCAPTCHA key:
   - Go to Security > reCAPTCHA Enterprise
   - Click "Create Key"
   - Choose "Website" platform
   - Add your domains (e.g., `localhost`, `yourdomain.com`)
   - Select "Score-based (reCAPTCHA v3)"

You'll get:
- **Site Key** (already configured): `6Lf-3ocsAAAAAKI0Nk1Ulj7R9o8WUfJwyPxAu7Xj`
- **API Key** (Secret Key): Get this from Google Cloud Console

### Step 2: Get Your API Key

1. In Google Cloud Console, go to **APIs & Services > Credentials**
2. Click **Create Credentials > API Key**
3. Restrict the API key to "reCAPTCHA Enterprise API" only
4. Copy the API key

### Step 3: Configure application.properties

Update the following values in `src/main/resources/application.properties`:

```properties
# Enable reCAPTCHA verification
recaptcha.enabled=true

# Your site key (already configured)
recaptcha.site.key=6Lf-3ocsAAAAAKI0Nk1Ulj7R9o8WUfJwyPxAu7Xj

# Your API/Secret key from Google Cloud Console
recaptcha.secret.key=YOUR_API_KEY_HERE

# Your Google Cloud Project ID
recaptcha.project.id=YOUR_PROJECT_ID

# Minimum score threshold (0.0 to 1.0)
recaptcha.score.threshold=0.5
```

### Step 4: Set Environment Variables (Production)

For production deployment, use environment variables:

```bash
export RECAPTCHA_ENABLED=true
export RECAPTCHA_SITE_KEY=6Lf-3ocsAAAAAKI0Nk1Ulj7R9o8WUfJwyPxAu7Xj
export RECAPTCHA_SECRET_KEY=your_api_key
export RECAPTCHA_PROJECT_ID=your_project_id
export RECAPTCHA_SCORE_THRESHOLD=0.5
```

## 🧪 Testing

### Test Without reCAPTCHA (Development)

To disable reCAPTCHA during development:

```properties
recaptcha.enabled=false
```

### Test With reCAPTCHA

1. Set `recaptcha.enabled=true`
2. Configure all keys properly
3. Test the login page at: `http://localhost:8080/login.html`

### Test via API

```bash
# Get reCAPTCHA token from frontend, then:
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123",
    "recaptchaToken": "YOUR_TOKEN_HERE"
  }'
```

## 📊 How It Works

1. **User submits login form**
2. **Frontend gets reCAPTCHA token** using `grecaptcha.enterprise.execute()`
3. **Token sent to backend** with login credentials
4. **Backend verifies token** with Google reCAPTCHA Enterprise API
5. **Google returns score** (0.0 = likely bot, 1.0 = likely human)
6. **Backend processes login** if score is above threshold

## 🔐 Security Features

- **Score-based verification**: Distinguishes humans from bots
- **Action verification**: Ensures token is for LOGIN action
- **Token expiry**: Tokens expire after 2 minutes
- **Configurable threshold**: Adjust score threshold based on needs

## ⚙️ Score Threshold Guide

- **0.1-0.3**: Very lenient (may allow some bots)
- **0.4-0.6**: Balanced (recommended)
- **0.7-0.9**: Strict (may block some legitimate users)
- **0.9-1.0**: Very strict (only very confident humans)

## 🛠️ Troubleshooting

### Issue: "reCAPTCHA verification failed"

**Solutions:**
1. Check if API key is correct
2. Verify project ID matches your Google Cloud project
3. Ensure reCAPTCHA Enterprise API is enabled
4. Check API key restrictions
5. Verify domain is added to allowed domains

### Issue: "Token expired"

**Solution:** Tokens are valid for 2 minutes. Ensure user submits form quickly.

### Issue: Low scores for legitimate users

**Solutions:**
1. Lower the `score.threshold` value
2. Check if users have cookies/JavaScript enabled
3. Review user's browsing behavior patterns

## 📝 Login Credentials for Testing

- **Email:** `test@example.com`
- **Password:** `password123`

OR

- **Email:** `teja@gmail.com`
- **Password:** `password123`

## 🚀 Next Steps

1. Configure your Google Cloud project
2. Get your API key and project ID
3. Update `application.properties`
4. Test the login functionality
5. Monitor reCAPTCHA scores in production
6. Adjust threshold based on results

## 📚 References

- [reCAPTCHA Enterprise Documentation](https://cloud.google.com/recaptcha-enterprise/docs)
- [reCAPTCHA v3 Guide](https://developers.google.com/recaptcha/docs/v3)
- [Google Cloud Console](https://console.cloud.google.com/)

## ⚠️ Important Notes

- Never commit your API keys to version control
- Use environment variables in production
- Monitor your reCAPTCHA usage in Google Cloud Console
- Review logs regularly for verification failures
- Adjust score threshold based on your security needs

---

**Created:** March 12, 2026
**Integration Status:** ✅ Complete
