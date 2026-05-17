# jewelry-invoicing

A Spring Boot 3.5.x service for jewelry invoicing.

## Stack

- Java 17
- Spring Boot 3.5.14 (Web, Data JPA, Validation)
- PostgreSQL 16 + Flyway
- Lombok
- Maven

## Domain

- `Customer` — name, email, phone, address
- `Product` — sku, name, description, unitPrice, stockQuantity
- `Invoice` — invoiceNumber, invoiceDate, customer, status, notes, subTotal, taxAmount, totalAmount
- `InvoiceLineItem` — product, description, quantity, unitPrice, lineTotal

`subTotal` and `totalAmount` are calculated server-side from line items + provided `taxAmount`.

## Getting started

### 1. Start Postgres

```bash
docker compose up -d
```

This starts Postgres on `localhost:5432` with:

- db: `jewelry_invoicing`
- user: `invoicing`
- password: `invoicing`

### 2. Run the app

```bash
./mvnw spring-boot:run
```

Or override DB connection via env vars: `DB_URL`, `DB_USER`, `DB_PASSWORD`.

The app listens on `http://localhost:8080`.

Flyway runs `V1__init_schema.sql` on startup.

### 3. Run tests

```bash
./mvnw test
```

> Tests use the same Postgres instance (profile `test`). Start `docker compose up -d` first.

## REST endpoints

All under `/api/v1`.

### Customers — `/api/v1/customers`

| Method | Path             | Body              | Returns         |
|--------|------------------|-------------------|-----------------|
| GET    | `/`              | —                 | List            |
| GET    | `/{id}`          | —                 | Single          |
| POST   | `/`              | `CustomerRequest` | 201 + entity    |
| PUT    | `/{id}`          | `CustomerRequest` | Updated entity  |
| DELETE | `/{id}`          | —                 | 204             |

### Products — `/api/v1/products`

Same shape with `ProductRequest`.

### Invoices — `/api/v1/invoices`

Same shape with `InvoiceRequest`.

### Sample requests

Create a customer:

```bash
curl -X POST http://localhost:8080/api/v1/customers \
  -H 'Content-Type: application/json' \
  -d '{"name":"Acme Jewelers","email":"info@acme.test","phone":"555-0101","address":"1 Main St"}'
```

Create a product:

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H 'Content-Type: application/json' \
  -d '{"sku":"RING-001","name":"Gold Ring","description":"22K","unitPrice":499.00,"stockQuantity":10}'
```

Create an invoice:

```bash
curl -X POST http://localhost:8080/api/v1/invoices \
  -H 'Content-Type: application/json' \
  -d '{
    "invoiceNumber":"INV-0001",
    "invoiceDate":"2026-05-09",
    "customerId":1,
    "status":"DRAFT",
    "notes":"First sale",
    "taxAmount":49.90,
    "lineItems":[{"productId":1,"quantity":1,"description":"Gold Ring"}]
  }'
```

## Project layout

```
src/main/java/com/example/invoicing
├── JewelryInvoicingApplication.java
├── common/
│   ├── Auditable.java
│   └── exception/  (ApiError, ResourceNotFoundException, GlobalExceptionHandler)
├── customer/       (Customer, repo, service, controller, dto/)
├── product/        (Product, repo, service, controller, dto/)
└── invoice/        (Invoice, InvoiceLineItem, InvoiceStatus, repo, service, controller, dto/)
```

## Notes

- `spring.jpa.hibernate.ddl-auto` is `validate`; schema changes go through Flyway migrations in `src/main/resources/db/migration/`.
- A copy of `application.yml` named `application-local.yml` is git-ignored for local overrides.
