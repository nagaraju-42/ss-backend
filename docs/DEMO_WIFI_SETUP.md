# Demo Wi-Fi Setup

Use this only for local shop demo before deployment.

1. Connect laptop and mobile/tablet to same Wi-Fi.
2. Start backend on laptop: `http://localhost:8080`.
3. Start frontend with host mode:
```powershell
npm run dev -- --host 0.0.0.0
```
4. Get laptop LAN IP:
```powershell
ipconfig
```
5. Open on phone:
```text
http://LAPTOP_IP:5173
```
6. Owner/picker opens:
```text
http://LAPTOP_IP:5173/login
```
7. Queue screen opens:
```text
http://LAPTOP_IP:5173/queue
```

In real deployment customers use the domain and their normal internet. Same-Wi-Fi is only for demo.

