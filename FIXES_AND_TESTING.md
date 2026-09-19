# CropDeal fixes and testing

## Default admin login

- Email: `admin@cropdeal.com`
- Password: `admin123`

You can override these values with the `CROPDEAL_ADMIN_EMAIL` and
`CROPDEAL_ADMIN_PASSWORD` environment variables. Auth Service creates or repairs
this development admin account when it starts.

## Recommended startup order

1. MySQL
2. RabbitMQ
3. Eureka Server
4. Auth, User, Crop, Subscription, Wallet, Pricing, Order, Payment,
   Notification, Report, and Auction services
5. API Gateway and the temporary frontend

## Wallet top-up

RCTS is no longer accepted by the wallet top-up endpoint. Use `IMPS` or `NEFT`:

```json
{
  "amount": 100000,
  "paymentMethod": "IMPS",
  "accountHolderName": "Dealer One",
  "bankName": "SBI",
  "accountNumber": "123456789012345",
  "ifscCode": "SBIN0001234",
  "mpin": "1234"
}
```

The bank details and MPIN are used only for fake request validation. They are not
stored in the database. Only the generated IMPS/NEFT transaction reference is
stored.

## Notifications

Keep RabbitMQ running on port `5672`. The services now declare their durable
queues and bindings, so notifications are retained while a consumer service is
temporarily restarting. The temporary frontend subscription example is also
configured to match its sample Tomato crop.

## Payment database compatibility

Payment Service automatically repairs an older `payments.payment_id` column to
the current `payments.id` mapping during startup. Do not drop the payment
database just to apply this fix.
