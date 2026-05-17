import { useCallback } from 'react'
import { customersApi } from '../api/customers'
import { AsyncState } from '../components/AsyncState'
import { useAsyncData } from '../hooks/useAsyncData'
import './Page.css'

export function CustomersPage() {
  const loadCustomers = useCallback(() => customersApi.list(), [])
  const { data, loading, error } = useAsyncData(loadCustomers)
  const customers = data ?? []

  return (
    <section>
      <header className="page-header">
        <h2>Customers</h2>
        <p>Customer records from the invoicing service.</p>
      </header>

      <div className="panel">
        <AsyncState
          loading={loading}
          error={error}
          empty={customers.length === 0}
          emptyMessage="No customers yet. Create one via the REST API or a future form."
        >
          <table className="data-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Email</th>
                <th>Phone</th>
                <th>Address</th>
              </tr>
            </thead>
            <tbody>
              {customers.map((customer) => (
                <tr key={customer.id}>
                  <td>{customer.name}</td>
                  <td>{customer.email ?? '—'}</td>
                  <td>{customer.phone ?? '—'}</td>
                  <td>{customer.address ?? '—'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </AsyncState>
      </div>
    </section>
  )
}
