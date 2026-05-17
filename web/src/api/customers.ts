import { apiRequest } from './client'
import type { Customer, CustomerRequest } from '../types/api'

export const customersApi = {
  list: () => apiRequest<Customer[]>('/customers'),
  get: (id: number) => apiRequest<Customer>(`/customers/${id}`),
  create: (payload: CustomerRequest) =>
    apiRequest<Customer>('/customers', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  update: (id: number, payload: CustomerRequest) =>
    apiRequest<Customer>(`/customers/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  remove: (id: number) =>
    apiRequest<void>(`/customers/${id}`, { method: 'DELETE' }),
}
