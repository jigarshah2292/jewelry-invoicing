import { apiRequest } from './client'
import type { Product, ProductRequest } from '../types/api'

export const productsApi = {
  list: () => apiRequest<Product[]>('/products'),
  get: (id: number) => apiRequest<Product>(`/products/${id}`),
  create: (payload: ProductRequest) =>
    apiRequest<Product>('/products', {
      method: 'POST',
      body: JSON.stringify(payload),
    }),
  update: (id: number, payload: ProductRequest) =>
    apiRequest<Product>(`/products/${id}`, {
      method: 'PUT',
      body: JSON.stringify(payload),
    }),
  remove: (id: number) =>
    apiRequest<void>(`/products/${id}`, { method: 'DELETE' }),
}
