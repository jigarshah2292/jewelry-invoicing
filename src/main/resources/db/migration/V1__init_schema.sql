CREATE TABLE customers (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(150)  NOT NULL,
    email           VARCHAR(150)  UNIQUE,
    phone           VARCHAR(30),
    address         VARCHAR(500),
    version         BIGINT        NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id              BIGSERIAL PRIMARY KEY,
    sku             VARCHAR(64)   NOT NULL UNIQUE,
    name            VARCHAR(200)  NOT NULL,
    description     VARCHAR(1000),
    unit_price      NUMERIC(14,2) NOT NULL,
    stock_quantity  INTEGER       NOT NULL DEFAULT 0,
    version         BIGINT        NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE invoices (
    id              BIGSERIAL PRIMARY KEY,
    invoice_number  VARCHAR(50)   NOT NULL UNIQUE,
    invoice_date    DATE          NOT NULL,
    customer_id     BIGINT        NOT NULL REFERENCES customers(id),
    sub_total       NUMERIC(14,2) NOT NULL DEFAULT 0,
    tax_amount      NUMERIC(14,2) NOT NULL DEFAULT 0,
    total_amount    NUMERIC(14,2) NOT NULL DEFAULT 0,
    status          VARCHAR(20)   NOT NULL DEFAULT 'DRAFT',
    notes           VARCHAR(1000),
    version         BIGINT        NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ   NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_invoices_customer_id ON invoices(customer_id);
CREATE INDEX idx_invoices_status      ON invoices(status);

CREATE TABLE invoice_line_items (
    id              BIGSERIAL PRIMARY KEY,
    invoice_id      BIGINT        NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    product_id      BIGINT        NOT NULL REFERENCES products(id),
    description     VARCHAR(500),
    quantity        INTEGER       NOT NULL,
    unit_price      NUMERIC(14,2) NOT NULL,
    line_total      NUMERIC(14,2) NOT NULL
);

CREATE INDEX idx_line_items_invoice_id ON invoice_line_items(invoice_id);
CREATE INDEX idx_line_items_product_id ON invoice_line_items(product_id);
