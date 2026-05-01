# PastryPoint Local Run

## Requirements
- Java 17
- Node.js
- PostgreSQL running locally
- Database name: `pastrypoint_db`

## Backend
```powershell
cd "C:\Users\NAGARAJU\PROJECT SAAS\pr5\PastryPointBackend"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/pastrypoint_db"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="your_password"
$env:RAZORPAY_KEY_ID="rzp_test_xxxxx"
$env:RAZORPAY_KEY_SECRET="xxxxx"
.\mvnw.cmd spring-boot:run
```

Backend URL: `http://localhost:8080`

## Frontend
```powershell
cd "C:\Users\NAGARAJU\PROJECT SAAS\pr5-frontend\pastrypoint-frontend"
npm install
$env:VITE_API_BASE_URL="http://localhost:8080"
$env:VITE_RAZORPAY_KEY_ID="rzp_test_xxxxx"
npm run dev -- --host 0.0.0.0
```

Default logins:
- owner / admin123
- staff / staff123
- picker / picker123

