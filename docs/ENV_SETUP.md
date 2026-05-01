# Environment Setup

## Backend Variables
```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/pastrypoint_db"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="specter@2027"
$env:RAZORPAY_KEY_ID="rzp_test_STm7h7fF2WtYYZ"
$env:RAZORPAY_KEY_SECRET="vj0dMbbb8Ycp2EVxhQWTlxdI"
```

## Frontend Variables
```powershell
$env:VITE_API_BASE_URL="http://localhost:8080"
$env:VITE_RAZORPAY_KEY_ID="rzp_test_STm7h7fF2WtYYZ"
```

## PhonePe Later
Do not hardcode PhonePe keys. When enabling PhonePe sandbox/live later, use:
```powershell
$env:PHONEPE_MODE="SANDBOX"
$env:PHONEPE_CLIENT_ID="..."
$env:PHONEPE_CLIENT_VERSION="..."
$env:PHONEPE_CLIENT_SECRET="..."
```

PhonePe live payments need merchant onboarding and HTTPS webhooks.

