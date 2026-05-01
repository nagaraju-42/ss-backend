# Free Deployment Options

Free hosting can work for a demo link, but it is weak for always-on shop usage.

## Option 1: Frontend on Vercel, backend local/VPS later
- Good for showing UI.
- Not enough for final real-time shop operation by itself.

## Option 2: Render/Railway free backend
- Easy to start.
- Free services may sleep.
- Sleeping breaks WebSocket reliability and first request feels slow.

## Option 3: Local laptop demo
- Best before first shop meeting.
- No public webhook support.
- PhonePe/Razorpay live webhooks need HTTPS public URL.

## Recommendation
Use free/local only for demo. For a paying shop, use a small VPS with Nginx, PostgreSQL, Spring Boot, and HTTPS.

