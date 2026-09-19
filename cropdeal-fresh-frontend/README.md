# CropDeal Fresh Temporary Frontend

Disposable frontend/API tester. It does not modify or depend on API Gateway routes.
It calls each microservice directly from the frontend server, so browser CORS is avoided.

## Run
1. Start your CropDeal backend services on their normal ports.
2. Start RabbitMQ and MySQL.
3. Import this folder as Existing Maven Project in STS/Eclipse.
4. Run `CropDealFreshFrontendApplication`.
5. Open `http://localhost:8095`.

## Ports used
- User 8081
- Crop 8082
- Subscription 8083
- Order 8084
- Payment 8085
- Notification 8086
- Report 8087
- Auth 8088
- Wallet 8089
- Pricing 8090
- Auction 8091

The UI automatically forwards the saved JWT and internal API key.
Use the correct login role for protected endpoints.
