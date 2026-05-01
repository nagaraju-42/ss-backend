# VPS Deployment Step By Step

## Server
Use Hetzner, Hostinger VPS, DigitalOcean, or similar.

Install:
```bash
sudo apt update
sudo apt install -y openjdk-17-jdk nginx postgresql postgresql-contrib certbot python3-certbot-nginx
```

## Database
```bash
sudo -u postgres psql
CREATE DATABASE pastrypoint_db;
CREATE USER pastrypoint_user WITH PASSWORD 'strong_password';
GRANT ALL PRIVILEGES ON DATABASE pastrypoint_db TO pastrypoint_user;
\q
```

## Backend
Build locally or on server:
```bash
./mvnw test
./mvnw package -DskipTests
```

Run with environment variables:
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pastrypoint_db
SPRING_DATASOURCE_USERNAME=pastrypoint_user
SPRING_DATASOURCE_PASSWORD=strong_password
RAZORPAY_KEY_ID=rzp_live_or_test_key
RAZORPAY_KEY_SECRET=secret
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## Frontend
```bash
npm install
VITE_API_BASE_URL=https://api.yourdomain.in VITE_RAZORPAY_KEY_ID=rzp_live_or_test_key npm run build
```

Serve `dist` with Nginx.

## Nginx Notes
- `/` serves frontend.
- `/api` proxies to Spring Boot `localhost:8080`.
- `/ws-kitchen` proxies WebSocket/SockJS traffic to Spring Boot.

## HTTPS
```bash
sudo certbot --nginx -d yourdomain.in
```

Payment webhooks must use HTTPS.

