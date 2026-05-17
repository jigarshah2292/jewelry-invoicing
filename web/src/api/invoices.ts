import { apiRequest } from './client'
import type { Invoice, InvoiceRequest } from '../types/api'

export const invoicesApi = {
  list: () => apiRequest<Invoice[]>('/invoices'),
  get: (id: number) => apiRequest<Invoice>(`/invoices/${id}`),
  create: (payload: InvoiceRequest) =>
    apiRequest<Invoice>('/invoices', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  update: (id: number, payload: InvoiceRequest) =>
    apiRequest<Invoice>(`/invoices/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  remove: (id: number) =>
    apiRequest<void>(`/invoices/${id}`, { method: 'DELETE' }),
}
