# 🌾 CropDeal

<div align="center">

<img src="assets/CropDeal.png" alt="CropDeal" width="100%"/>

# CropDeal

### Connecting Farmers Directly With Dealers

A microservices-based agricultural marketplace designed to simplify crop trading,
reduce intermediary costs, and provide transparent digital transactions.

</div>

---

## 🏷️ Technology Stack

<p align="center">

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Microservices-brightgreen?style=for-the-badge&logo=springboot)

![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-Eureka-blue?style=for-the-badge&logo=spring)

![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Event%20Messaging-orange?style=for-the-badge&logo=rabbitmq)

![Angular](https://img.shields.io/badge/Angular-Frontend-red?style=for-the-badge&logo=angular)

![MySQL](https://img.shields.io/badge/MySQL-Database-blue?style=for-the-badge&logo=mysql)

![Maven](https://img.shields.io/badge/Maven-Build%20Tool-C71A36?style=for-the-badge&logo=apachemaven)

</p>

---

# 📖 Project Overview

CropDeal is a digital agricultural marketplace that acts as a bridge between **Farmers** and **Dealers**.

Farmers often face difficulties when selling crops, including transportation expenses, waiting time, price negotiations, and intermediary commissions.

CropDeal provides a platform where farmers can publish their available crops directly from their farms. Dealers can discover suitable crops, contact farmers, inspect the crop, negotiate the price, place orders, and complete payments through the application.

The system is implemented using a **Spring Boot Microservices Architecture** with service discovery, centralized configuration, event-driven communication, payment processing, notifications, and reporting.

---

# 🎯 Project Objective

| Objective | Description |
|---|---|
| 🌾 **Direct Selling** | Connect farmers directly with crop dealers |
| 💰 **Better Pricing** | Reduce unnecessary intermediary commissions |
| 🚚 **Lower Transportation Cost** | Enable transactions directly from the farm |
| 📱 **Digital Platform** | Manage crop sales through an application |
| 🔔 **Instant Notifications** | Notify dealers about matching crops |
| 💳 **Transparent Payments** | Process and record digital payments |
| 🧾 **Digital Receipts** | Generate receipts and invoices |
| 📊 **Reporting** | Provide useful transaction and business reports |
| 🔐 **Secure Access** | Support authenticated farmer and dealer accounts |
| ⚙️ **Scalable Architecture** | Use independent microservices for major business capabilities |

---

# 👥 Team Members

| # | Team Member | GitHub Profile | Service Responsibility |
|---:|---|---|---|
| 1 | **Janani J** | [jananijagan](https://github.com/jananijagan) | `Crop_Service`,`User_Service`|
| 2 | **Harshini R** | [harshu-2025](https://github.com/harshu-2025) | `API_Gateway`, `Eureka_Server`, `Config_Service`, `Auth_Service` |
| 3 | **Gobika S** | [Gobika-Subramaniam](https://github.com/Gobika-Subramaniam) | `Order_Service` |
| 4 | **Sriman Ragaventra S K** | [srimansk](https://github.com/srimansk) | `Report_Service`,`Subscription_Service` |
| 5 | **Hinthu Mithran P** | [Mithran12](https://github.com/Mithran12) | `Payment_Service`, `Notification_Service` |

---

# 🧩 Microservices

| Service | Responsibility |
|---|---|
| **API Gateway** | Single entry point for client requests and service routing |
| **Eureka Server** | Service registration and service discovery |
| **Auth Service** | Authenticate & Authorization of the Users  |
| **Subscription Service** | Order request and commodity Matching |
| **Config Service** | Centralized external configuration |
| **User Service** | Farmer and dealer registration, login and profile management |
| **Crop Service** | Crop publishing and crop information management |
| **Order Service** | Crop purchase and order lifecycle management |
| **Payment Service** | Payment processing and transaction management |
| **Notification Service** | Crop, order and payment notifications |
| **Report Service** | Invoice, receipt and report generation |

---

# ✨ Core Features

| Feature | Description |
|---|---|
| 👨‍🌾 **Farmer Registration** | Farmers can create accounts and manage profiles |
| 🧑‍💼 **Dealer Registration** | Dealers can register and manage their profiles |
| 🌾 **Crop Publishing** | Farmers can publish crop type, quantity and location |
| 🔎 **Crop Discovery** | Dealers can find crops available for purchase |
| 🔔 **Notifications** | Dealers receive notifications for matching crops |
| 🤝 **Direct Negotiation** | Dealers can connect with farmers directly |
| 📦 **Order Management** | Manage crop purchase orders |
| 💳 **Digital Payment** | Process and record payments |
| 🧾 **Invoice & Receipt** | Generate purchase invoices and farmer receipts |
| 📊 **Reports** | Generate business and transaction reports |
| 🔐 **Authentication** | Secure farmer and dealer access |
| ⚙️ **Centralized Configuration** | Manage service configuration externally |

---

# 🔄 CropDeal Workflow

CropDeal connects farmers and dealers directly, allowing crops to be published, discovered, negotiated, purchased, and paid for through a distributed microservices platform.

```text
                         ┌──────────────────────┐
                         │        FARMER        │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │     USER SERVICE     │
                         │ Register / Login     │
                         │ Profile Management   │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │   PRODUCT SERVICE    │
                         │ Publish Crop Details │
                         │ Crop Type / Quantity │
                         │ Location / Price     │
                         └──────────┬───────────┘
                                    │
                                    │ Crop Event
                                    ▼
                         ┌──────────────────────┐
                         │ NOTIFICATION SERVICE │
                         │ Notify Interested    │
                         │ Dealers              │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │        DEALER        │
                         │ View Crop Details    │
                         │ Contact Farmer       │
                         │ Negotiate Price      │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │    ORDER SERVICE     │
                         │ Create Purchase      │
                         │ Manage Order Status  │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │   PAYMENT SERVICE    │
                         │ Process Payment      │
                         │ Record Transactions  │
                         └──────────┬───────────┘
                                    │
                                    │ Payment Event
                                    ▼
                         ┌──────────────────────┐
                         │ NOTIFICATION SERVICE │
                         │ Payment Confirmation │
                         └──────────┬───────────┘
                                    │
                                    ▼
                         ┌──────────────────────┐
                         │    REPORT SERVICE    │
                         │ Invoice / Receipt    │
                         │ Reports & Analytics  │
                         └──────────────────────┘
```

---

# 🏗️ System Architecture

![CropDeal Architecture](assets/CropDeal-Architecture.png)

```text
                         ┌────────────────────┐
                         │   Angular Frontend │
                         └─────────┬──────────┘
                                   │
                                   ▼
                         ┌────────────────────┐
                         │    API Gateway     │
                         │       :8080        │
                         └─────────┬──────────┘
                                   │
             ┌─────────────────────┼─────────────────────┐
             │                     │                     │
             ▼                     ▼                     ▼
      ┌─────────────┐       ┌─────────────┐       ┌─────────────┐
      │User Service │       │Product      │       │Order        │
      │             │       │Service      │       │Service      │
      └──────┬──────┘       └──────┬──────┘       └──────┬──────┘
             │                     │                     │
             ▼                     ▼                     ▼
      ┌─────────────┐       ┌─────────────┐       ┌─────────────┐
      │ User DB     │       │ Product DB  │       │ Order DB    │
      └─────────────┘       └─────────────┘       └─────────────┘

             │                     │                     │
             └─────────────────────┼─────────────────────┘
                                   │
                                   ▼
                         ┌────────────────────┐
                         │     RabbitMQ       │
                         │ Event Messaging    │
                         └─────────┬──────────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
                    ▼              ▼              ▼
             ┌────────────┐ ┌────────────┐ ┌────────────┐
             │  Payment   │ │Notification│ │  Report    │
             │  Service   │ │  Service   │ │  Service   │
             └────────────┘ └────────────┘ └────────────┘

                         ┌────────────────────┐
                         │   Eureka Server    │
                         │ Service Registry   │
                         └────────────────────┘

                         ┌────────────────────┐
                         │   Config Service   │
                         │ Centralized Config │
                         └────────────────────┘
```

---

# 🔐 Authentication & User Flow

CropDeal supports two primary user roles:

- 👨‍🌾 **Farmer**
- 🧑‍💼 **Dealer**

Both users can register and authenticate through the User Service.

```text
              ┌───────────────┐
              │     User      │
              └───────┬───────┘
                      │
                      ▼
             ┌─────────────────┐
             │  API Gateway    │
             └────────┬────────┘
                      │
                      ▼
             ┌─────────────────┐
             │  User Service   │
             └────────┬────────┘
                      │
              ┌───────┴────────┐
              │                │
              ▼                ▼
       ┌─────────────┐  ┌─────────────┐
       │ Email Login │  │ Federated   │
       │             │  │ Identity    │
       └─────────────┘  └─────────────┘
```

---

# 👨‍🌾 Farmer Flow

```text
Farmer
   │
   ├── Register / Login
   │
   ├── Manage Profile
   │
   ├── Add Bank Details
   │
   └── Publish Crop
          │
          ├── Crop Type
          ├── Crop Name
          ├── Quantity
          └── Location
                 │
                 ▼
          Product Service
                 │
                 ▼
           Crop Published
                 │
                 ▼
       Interested Dealers
                 │
                 ▼
           Order Created
                 │
                 ▼
           Payment Done
                 │
                 ▼
          Receipt Generated
```

### Farmer Features

- Register and login
- Manage profile
- Add bank account details
- Publish crop information
- Select crop type and crop name
- Enter available quantity
- Provide location
- Receive notifications
- View generated receipts

---

# 🧑‍💼 Dealer Flow

```text
Dealer
   │
   ├── Register / Login
   │
   ├── Manage Profile
   │
   ├── Add Bank Details
   │
   └── Subscribe to Crops
            │
            ▼
      Receive Notification
            │
            ▼
      View Crop Details
            │
            ▼
      Contact Farmer
            │
            ▼
      Inspect Crop
            │
            ▼
      Negotiate Price
            │
            ▼
      Create Order
            │
            ▼
      Make Payment
            │
            ▼
      Receive Invoice
```

### Dealer Features

- Register and login
- Manage profile
- Add bank account details
- Subscribe to crop information
- Receive crop notifications
- View crop details
- Contact farmers
- Inspect crops
- Negotiate prices
- Purchase crops
- Make digital payments
- Receive invoices

---

# 💳 Payment Flow

```text
Dealer
  │
  ▼
Order Service
  │
  │ Payment Request
  ▼
Payment Service
  │
  ├── Validate Payment
  │
  ├── Process Transaction
  │
  └── Store Transaction
          │
          ▼
     Payment Event
          │
          ▼
       RabbitMQ
          │
          ├──────────────► Notification Service
          │
          └──────────────► Report Service
```

---

# 📩 Event-Driven Communication

RabbitMQ is used for asynchronous communication between services.

| Event | Producer | Consumers |
|---|---|---|
| `CROP_PUBLISHED` | Product Service | Notification Service |
| `ORDER_CREATED` | Order Service | Notification Service |
| `PAYMENT_SUCCESS` | Payment Service | Notification Service, Report Service |
| `PAYMENT_FAILED` | Payment Service | Notification Service |
| `ORDER_COMPLETED` | Order Service | Notification Service, Report Service |

```text
Product Service
      │
      │ CROP_PUBLISHED
      ▼
   RabbitMQ
      │
      ▼
Notification Service
      │
      ▼
Dealer Notification
```

---

# 🧠 Design Patterns

## 1. Service Registry & Discovery

**Technology:** Spring Cloud Netflix Eureka

All microservices register themselves with Eureka and can discover other services through the registry.

```text
                 ┌─────────────────┐
                 │  Eureka Server  │
                 │      :8761      │
                 └────────┬────────┘
                          │
        ┌─────────────────┼─────────────────┐
        │                 │                 │
        ▼                 ▼                 ▼
 User Service      Product Service    Order Service
 Payment Service   Notification       Report Service
```

---

## 2. Externalized Configuration

**Technology:** Spring Cloud Config Server + GitHub

Application configuration is maintained separately from the application deployment packages.

```text
                 ┌─────────────────────┐
                 │       GitHub        │
                 │ Configuration Files │
                 └──────────┬──────────┘
                            │
                            ▼
                 ┌─────────────────────┐
                 │   Config Service    │
                 └──────────┬──────────┘
                            │
          ┌─────────────────┼─────────────────┐
          │                 │                 │
          ▼                 ▼                 ▼
     User Service     Product Service    Order Service
```

---

## 3. CQRS

**Command Query Responsibility Segregation**

CQRS can be used to separate write operations from read operations, especially for reporting and invoice-generation use cases.

```text
                 ┌───────────────────┐
                 │      Request      │
                 └─────────┬─────────┘
                           │
                  ┌────────┴────────┐
                  │                 │
                  ▼                 ▼
             Command Side       Query Side
                  │                 │
                  ▼                 ▼
            Write Database     Read Database
                  │                 │
                  └────────┬────────┘
                           ▼
                     Response
```

---

## 4. Event Sourcing

Event Sourcing can be used for payment transactions by storing the sequence of events that led to the current payment state.

```text
Payment Requested
       │
       ▼
Payment Processing
       │
       ▼
Payment Successful
       │
       ▼
Payment Recorded
       │
       ▼
Payment Event Published
```

---

## 5. Federated Identity

Federated Identity can be used to allow users to authenticate using external identity providers.

```text
          ┌──────────────┐
          │     User     │
          └──────┬───────┘
                 │
                 ▼
        ┌──────────────────┐
        │ Identity Provider│
        └────────┬─────────┘
                 │
                 ▼
          User Service
                 │
                 ▼
          Authenticated User
```

---

# 🧰 Technology Stack

| Category | Technology |
|---|---|
| **Frontend** | Angular |
| **Backend** | Java 21 |
| **Framework** | Spring Boot |
| **Architecture** | Microservices |
| **API** | REST / JSON |
| **Service Discovery** | Spring Cloud Netflix Eureka |
| **Configuration** | Spring Cloud Config |
| **Messaging** | RabbitMQ |
| **Database** | MySQL / H2 / MongoDB |
| **ORM** | Spring Data JPA / Hibernate |
| **Build Tool** | Maven |
| **API Gateway** | Spring Cloud Gateway |
| **Authentication** | Spring Security / Federated Identity |
| **Testing** | JUnit / Mockito |
| **Containerization** | Docker |
| **Version Control** | Git / GitHub |

---

# 📁 Project Structure

```text
CropDeal/
│
├── API_Gateway/
│   ├── src/
│   └── pom.xml
│
├── Eureka_Server/
│   ├── src/
│   └── pom.xml
│
├── Config_Service/
│   ├── src/
│   └── pom.xml
│
├── User_Service/
│   ├── src/
│   └── pom.xml
│
├── Product_Service/
│   ├── src/
│   └── pom.xml
│
├── Order_Service/
│   ├── src/
│   └── pom.xml
│
├── Payment_Service/
│   ├── src/
│   └── pom.xml
│
├── Notification_Service/
│   ├── src/
│   └── pom.xml
│
├── Report_Service/
│   ├── src/
│   └── pom.xml
│
├── frontend/
│   └── Angular Application
│
├── assets/
│   ├── CropDeal.png
│   ├── CropDeal-Architecture.png
│   ├── home.png
│   └── dashboard.png
│
├── .gitignore
└── README.md
```

---

# 🧪 Engineering Practices

CropDeal follows standard software engineering practices for developing maintainable and scalable microservices.

## Exception Handling

Centralized exception handling is used to provide consistent API responses.

```text
Controller
    │
    ▼
Service
    │
    ▼
Exception
    │
    ▼
Global Exception Handler
    │
    ▼
Standard JSON Error Response
```

## Logging

Services maintain meaningful logs for:

- Request processing
- Business operations
- Errors
- Payment transactions
- Messaging events
- Service communication

## Testing

Testing can be implemented using:

- JUnit
- Mockito
- Spring Boot Test

## Static Code Analysis

Static analysis tools can be used to maintain code quality and identify potential issues.

## Build Automation

Maven is used for:

- Dependency management
- Compilation
- Testing
- Packaging
- Application builds

---

# 🐳 Docker Deployment

Each microservice can be packaged and deployed as an independent Docker container.

```text
                    ┌──────────────────┐
                    │   Docker Host    │
                    └────────┬─────────┘
                             │
       ┌─────────────┬───────┼────────┬─────────────┐
       │             │       │        │             │
       ▼             ▼       ▼        ▼             ▼
   API Gateway   Eureka   User    Product       Order
       │         Server   Service  Service      Service
       │
       ├────────────── Payment Service
       │
       ├────────────── Notification Service
       │
       └────────────── Report Service

                    ┌──────────────────┐
                    │     RabbitMQ     │
                    └──────────────────┘
```

---

# 🔄 End-to-End CropDeal Flow

```text
1. Farmer registers
          │
          ▼
2. Farmer logs in
          │
          ▼
3. Farmer publishes crop
          │
          ▼
4. Product Service stores crop
          │
          ▼
5. Crop event published to RabbitMQ
          │
          ▼
6. Interested dealers receive notification
          │
          ▼
7. Dealer views crop details
          │
          ▼
8. Dealer contacts farmer
          │
          ▼
9. Crop is inspected and price negotiated
          │
          ▼
10. Dealer creates order
          │
          ▼
11. Payment Service processes payment
          │
          ▼
12. Payment event published
          │
          ├──────────────► Notification Service
          │
          └──────────────► Report Service
          │
          ▼
13. Purchase completed
          │
          ▼
14. Invoice / Receipt generated
          │
          ▼
15. Farmer receives sale receipt
```

---

# 📸 Project Preview

## 🏠 Home Page

![CropDeal Home](assets/home.png)

## 📊 Dashboard

![CropDeal Dashboard](assets/dashboard.png)

## 🏗️ Architecture

![CropDeal Architecture](assets/CropDeal-Architecture.png)

---

# 🌱 Why CropDeal?

Traditional agricultural markets can involve transportation costs, waiting time, price negotiations, and intermediary commissions.

CropDeal aims to simplify this process by creating a direct digital bridge between farmers and dealers.

### Traditional Flow

```text
Farmer
  │
  ▼
Transportation
  │
  ▼
Market
  │
  ▼
Intermediary
  │
  ▼
Negotiation
  │
  ▼
Dealer
```

### CropDeal Flow

```text
Farmer
  │
  │ Publish Crop
  ▼
CropDeal
  │
  │ Notify
  ▼
Dealer
  │
  │ Purchase
  ▼
Payment
  │
  ▼
Farmer
```

---

# 🚀 Future Enhancements

- GPS-based farm location
- Crop price analytics
- AI-based crop price recommendations
- Weather information integration
- Advanced order tracking
- Production payment gateway integration
- Push notifications
- Advanced admin dashboards
- Dedicated Android / iOS applications
- Cloud deployment
- CI/CD pipeline
- Enhanced authentication and authorization
- Advanced report export

---

# 📌 Project Status

🚧 **Under Development**

CropDeal is being developed as a distributed microservices-based application with:

- REST APIs
- Spring Boot Microservices
- Eureka Service Discovery
- Spring Cloud Config
- RabbitMQ Event Messaging
- Payment Processing
- Notification Services
- Reporting
- Angular Frontend
- Docker-based Deployment

---

# 🏁 Conclusion

**CropDeal** is a microservices-based agricultural marketplace designed to connect farmers and dealers directly.

By combining **Spring Boot, Spring Cloud, Eureka, RabbitMQ, Angular, centralized configuration, event-driven communication, payment processing, and distributed services**, CropDeal provides a scalable foundation for digital crop trading.

<div align="center">

## 🌾 CropDeal

### Connecting Farmers. Empowering Dealers. Simplifying Crop Trade.

**Built with ❤️ using Spring Boot, Microservices & RabbitMQ.**

</div>

---

<div align="center">

<img src="assets/CropThanks.png" alt="CropDeal" width="100%"/>

</div>
