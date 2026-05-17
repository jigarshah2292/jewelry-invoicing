export type InvoiceStatus = 'DRAFT' | 'ISSUED' | 'PAID' | 'CANCELLED'

export interface ApiErrorBody {
  timestamp: string
  status: number
  error: string
  message: string
  path: string
  details: string[]
}

export interface Customer {
  id: number
  name: string
  email: string | null
  phone: string | null
  address: string | null
  createdAt: string
  updatedAt: string
}

export interface CustomerRequest {
  name: string
  email?: string
  phone?: string
  address?: string
}

export interface Product {
  id: number
  sku: string
  name: string
  description: string | null
  unitPrice: number
  stockQuantity: number
  createdAt: string
  updatedAt: string
}

export interface ProductRequest {
  sku: string
  name: string
  description?: string
  unitPrice: number
  stockQuantity: number
}

export interface InvoiceLineItem {
  id: number
  productId: number
  productSku: string
  productName: string
  description: string | null
  quantity: number
  unitPrice: number
  lineTotal: number
}

export interface Invoice {
  id: number
  invoiceNumber: string
  invoiceDate: string
  customerId: number
  customerName: string
  status: InvoiceStatus
  notes: string | null
  subTotal: number
  taxAmount: number
  totalAmount: number
  lineItems: InvoiceLineItem[]
  createdAt: string
  updatedAt: string
}

export interface InvoiceLineItemRequest {
  productId: number
  description?: string
  quantity: number
}

export interface InvoiceRequest {
  invoiceNumber: string
  invoiceDate: string
  customerId: number
  status?: InvoiceStatus
  notes?: string
  taxAmount: number
  lineItems: InvoiceLineItemRequest[]
}
