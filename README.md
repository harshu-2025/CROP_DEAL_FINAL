CropDeal – Microservices-Based Agricultural Marketplace

1. Overview

CropDeal is a Spring Boot microservices-based agricultural marketplace that connects farmers directly with dealers.

The platform supports farmer and dealer profiles, crop management, subscriptions, order negotiation, payments, wallets, escrow, auctions, reviews, notifications, pricing and reports.

2. Technologies

Java

Spring Boot

Spring Data JPA

MySQL / H2 where configured

Spring Security

JWT

Spring Cloud Eureka

Spring Cloud Config

Spring Cloud Gateway

OpenFeign

Resilience4j

RabbitMQ

SLF4J

Saga Pattern

Outbox Pattern

Swagger / OpenAPI

Spring Boot Actuator

Docker

Government of India Open Data API for mandi price reference

3. Microservices

Service

Port

Responsibility

API Gateway

8080

Central API entry point and routing

User Service

8081

Farmer/dealer profiles

Crop Service

8082

Crop management

Subscription Service

8083

Dealer subscriptions

Order Service

8084

Orders and negotiation

Payment Service

8085

Payments, Saga and escrow

Notification Service

8086

Notifications

Report Service

8087

Reports and invoices

Auth Service

8088

Registration, login and JWT

Wallet Service

8089

Wallet and fund management

Pricing Service

8090

Government market-price reference

Auction Service

8091

Auctions and bidding

Review Service

8092

Farmer/dealer reviews

Config Server

8888

Centralized configuration

Eureka Server

8761

Service discovery

4. Architecture

                    HTML API Tester
                           |
                           v
                    API Gateway :8080
                           |
       ------------------------------------------------
       |       |       |       |       |              |
      User    Crop   Order   Payment  Wallet      Other Services
     :8081   :8082  :8084   :8085   :8089
                           |
                        RabbitMQ
                           |
              -----------------------------
              |                           |
        Eureka :8761              Config :8888

5. Authentication

Auth Service provides registration and login.

After login, the client receives a JWT.

Protected requests use:

Authorization: Bearer <JWT>

Internal service-to-service endpoints use:

X-Internal-Key: cropdeal-internal-123

The internal key and JWT secret should be changed for production.

6. User Service

Responsibilities:

Farmer profile creation

Dealer profile creation

Profile retrieval/update

User activation/deactivation

Internal user lookup

Example:

POST http://localhost:8081/api/users/farmers
POST http://localhost:8081/api/users/dealers

7. Crop Service

Responsibilities:

Create and update crops

Search and filter crops

Crop status management

Quantity reservation

Quantity restoration during payment compensation

Example:

POST http://localhost:8082/api/crops
GET  http://localhost:8082/api/crops
GET  http://localhost:8082/api/crops/{id}

8. Subscription Service

Dealers can subscribe to crop requirements.

It manages:

Crop subscriptions

Dealer subscriptions

Activation/deactivation

Matching crop notifications

9. Order and Negotiation

Orders support negotiation between farmers and dealers.

Order Created
      |
      v
NEGOTIATION_PENDING
      |
      +---- Accept ----> ACCEPTED
      |
      +---- Reject ----> REJECTED
      |
      +---- Counter Offer
                 |
                 v
              COUNTERED

Negotiation APIs:

GET   /orders/{id}/offers
PATCH /orders/{id}/offer/accept
PATCH /orders/{id}/offer/reject
POST  /orders/{id}/offer/counter

10. Payment Saga

The Payment Service uses a Saga-style orchestration flow.

Payment Initiated
       |
       v
Payment Authorized
       |
       v
Funds Reserved
       |
       v
Escrow Held
       |
       v
Order Payment Confirmed
       |
       v
Payment Successful

If a later operation fails, compensation is performed:

Failure
  |
  +-- Rollback escrow
  +-- Release reserved funds
  +-- Mark payment failed
  +-- Restore crop quantity
  |
  v
Compensation Completed

11. Escrow

Escrow temporarily holds payment until delivery is confirmed.

Dealer Payment
      |
      v
Reserved Funds
      |
      v
Escrow HELD
      |
      | Delivery confirmed
      v
Escrow RELEASED
      |
      v
Farmer receives funds

Escrow states:

NOT_CREATED
HELD
RELEASED
REFUNDED

12. Outbox Pattern

The Payment Service uses an Outbox mechanism for reliable event publishing.

Business Transaction
       |
       +--> Database update
       |
       +--> Outbox event
                |
                v
         Outbox Publisher
                |
                v
             RabbitMQ

13. RabbitMQ

RabbitMQ provides asynchronous messaging for events and notifications.

Management UI:

http://localhost:15672

Development credentials commonly used:

guest / guest

Docker:

docker run -d --hostname cropdeal-rabbit --name cropdeal-rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

14. Pricing Service

Pricing uses Government of India Open Government Data mandi information as a market-price reference.

The project uses the AGMARKNET-related daily mandi price dataset.

Pricing is a project reference price and should not be interpreted as a guaranteed transaction price.

The API key is supplied through configuration:

DATA_GOV_IN_API_KEY=<your-api-key>

Example:

POST http://localhost:8090/pricing/calculate

15. Auction Service

Auction flow:

Farmer creates auction
        |
        v
Auction OPEN
        |
        v
Dealers place bids
        |
        v
Auction closes
        |
        v
Winner / Order / Payment

16. Review Service

Reviews are supported after completed orders.

Rules include:

Only order participants can review.

Farmer can review dealer.

Dealer can review farmer.

Self-review is not allowed.

One review per reviewer per order.

Order must be completed.

Endpoints:

POST   /reviews
GET    /reviews/{id}
GET    /reviews/user/{userId}
GET    /reviews/user/{userId}/summary
GET    /reviews/mine
PUT    /reviews/{id}
DELETE /reviews/{id}

17. Resilience4j

Resilience4j is used for fault tolerance and circuit breakers.

Service A
   |
   v
Service B
   X
Circuit Breaker
   |
   v
Fallback

It helps prevent failures in one service from cascading through the application.

18. SLF4J

SLF4J is used for application logging.

Example:

log.info("Farmer created successfully: {}", farmerId);
log.warn("Farmer not found: {}", farmerId);
log.error("Payment processing failed", exception);

19. Eureka

Eureka Server:

http://localhost:8761

Services register with Eureka so they can communicate using service names.

20. Config Server

Config Server:

http://localhost:8888

Services use Spring Cloud Config Client to obtain centralized configuration.

The current implementation uses a native Config Server repository. A Git-backed repository can be used for a production setup.

21. Swagger

Where Springdoc is configured, Swagger UI is available at:

http://localhost:<PORT>/swagger-ui/index.html

Swagger is used to view and test REST APIs.

22. Actuator

Health endpoint:

http://localhost:<PORT>/actuator/health

Actuator is used for health and monitoring information.

23. HTML API Tester

The project uses a plain HTML/CSS/JavaScript frontend for API testing.

It does not require Angular, npm or Node.js.

It can test:

Auth

User

Crop

Subscription

Order

Negotiation

Payment

Wallet

Pricing

Auction

Review

Notification

Report

24. Recommended Startup Order

RabbitMQ

Eureka Server

Config Server

Auth Service

User Service

Crop Service

Subscription Service

Order Service

Wallet Service

Payment Service

Pricing Service

Notification Service

Report Service

Auction Service

Review Service

API Gateway

HTML API Tester

25. End-to-End Test Flow

Register
   |
Login
   |
JWT
   |
Create Farmer / Dealer
   |
Create Dealer Wallet
   |
Create Crop
   |
Create Subscription
   |
Create Order
   |
Negotiate
   |
Accept Order
   |
Payment
   |
Wallet Reservation
   |
Escrow
   |
Confirm Delivery
   |
Release Escrow
   |
Complete Order
   |
Review
   |
Reports

26. Important Environment Variables

JWT_SECRET=<secure-secret>

DATA_GOV_IN_API_KEY=<api-key>

CROPDEAL_ADMIN_EMAIL=<admin-email>
CROPDEAL_ADMIN_PASSWORD=<admin-password>

CONFIG_SERVER_URL=http://localhost:8888

INTERNAL_API_KEY=<secure-internal-key>

Never commit production secrets or passwords to Git.

27. Project Structure

CropDeal/
|
+-- api-gateway/
+-- auth-service/
+-- user-service/
+-- crop-service/
+-- subscription-service/
+-- order-service/
+-- payment-service/
+-- notification-service/
+-- report-service/
+-- wallet-service/
+-- pricing-service/
+-- auction-service/
+-- review-service/
+-- config-server/
+-- eureka-server/
|
+-- cropdeal-fresh-frontend/
|   +-- HTML
|   +-- CSS
|   +-- JavaScript
|
+-- README.md

28. Error Handling

The services use validation and exception handling to return meaningful HTTP responses.

Common statuses:

200 OK
201 CREATED
400 BAD REQUEST
401 UNAUTHORIZED
403 FORBIDDEN
404 NOT FOUND
409 CONFLICT
500 INTERNAL SERVER ERROR

29. Testing with Postman

For secured APIs:

Authorization
Type: Bearer Token
Token: <JWT>

For JSON requests:

Content-Type: application/json

For internal service communication:

X-Internal-Key: cropdeal-internal-123

30. Final Summary

CropDeal demonstrates:

Microservices architecture

REST APIs

API Gateway

Eureka Service Discovery

Centralized Configuration

JWT Security

OpenFeign

Resilience4j Circuit Breaker

RabbitMQ

SLF4J Logging

Saga Pattern

Outbox Pattern

Wallet

Escrow

Government market-price reference

Auctions

Reviews

Notifications

Reports

Swagger/OpenAPI

Actuator

HTML API testing frontend
