import { useCallback } from 'react'
import { invoicesApi } from '../api/invoices'
import { AsyncState } from '../components/AsyncState'
import { useAsyncData } from '../hooks/useAsyncData'
import type { InvoiceStatus } from '../types/api'
import { formatDate, formatMoney } from '../lib/format'
import './Page.css'

function statusClass(status: InvoiceStatus): string {
  return `status-pill status-pill--${status.toLowerCase()}`
}

export function InvoicesPage() {
  const loadInvoices = useCallback(() => invoicesApi.list(), [])
  const { data, loading, error } = useAsyncData(loadInvoices)
  const invoices = data ?? []

  return (
    <section>
      <header className="page-header">
        <h2>Invoices</h2>
        <p>Invoice headers with calculated totals from the backend.</p>
      </header>

      <div className="panel">
        <AsyncState
          loading={loading}
          error={error}
          empty={invoices.length === 0}
          emptyMessage="No invoices yet. Create one via the REST API or a future form."
        >
          <table className="data-table">
            <thead>
              <tr>
                <th>Number</th>
                <th>Date</th>
                <th>Customer</th>
                <th>Status</th>
                <th>Total</th>
                <th>Lines</th>
              </tr>
            </thead>
            <tbody>
              {invoices.map((invoice) => (
                <tr key={invoice.id}>
                  <td>{invoice.invoiceNumber}</td>
                  <td>{formatDate(invoice.invoiceDate)}</td>
                  <td>{invoice.customerName}</td>
                  <td>
                    <span className={statusClass(invoice.status)}>
                      {invoice.status}
                    </span>
                  </td>
                  <td>{formatMoney(invoice.totalAmount)}</td>
                  <td>{invoice.lineItems.length}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </AsyncState>
      </div>
    </section>
  )
}
