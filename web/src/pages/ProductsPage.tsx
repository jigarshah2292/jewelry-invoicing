import { useCallback } from 'react'
import { productsApi } from '../api/products'
import { AsyncState } from '../components/AsyncState'
import { useAsyncData } from '../hooks/useAsyncData'
import { formatMoney } from '../lib/format'
import './Page.css'

export function ProductsPage() {
  const loadProducts = useCallback(() => productsApi.list(), [])
  const { data, loading, error } = useAsyncData(loadProducts)
  const products = data ?? []

  return (
    <section>
      <header className="page-header">
        <h2>Products</h2>
        <p>Catalog items with SKU, unit price, and stock quantity.</p>
      </header>

      <div className="panel">
        <AsyncState
          loading={loading}
          error={error}
          empty={products.length === 0}
          emptyMessage="No products yet. Create one via the REST API or a future form."
        >
          <table className="data-table">
            <thead>
              <tr>
                <th>SKU</th>
                <th>Name</th>
                <th>Unit price</th>
                <th>Stock</th>
              </tr>
            </thead>
            <tbody>
              {products.map((product) => (
                <tr key={product.id}>
                  <td>{product.sku}</td>
                  <td>{product.name}</td>
                  <td>{formatMoney(product.unitPrice)}</td>
                  <td>{product.stockQuantity}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </AsyncState>
      </div>
    </section>
  )
}
