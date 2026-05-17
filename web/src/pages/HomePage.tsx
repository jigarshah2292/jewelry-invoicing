import { Link } from 'react-router-dom'
import './Page.css'

const sections = [
  {
    to: '/customers',
    title: 'Customers',
    description: 'Manage buyer profiles, contact details, and addresses.',
  },
  {
    to: '/products',
    title: 'Products',
    description: 'Track SKUs, pricing, and inventory for jewelry pieces.',
  },
  {
    to: '/invoices',
    title: 'Invoices',
    description: 'Review invoice totals, tax, and line items from the API.',
  },
]

export function HomePage() {
  return (
    <section>
      <header className="page-header">
        <h2>Welcome</h2>
        <p>
          This React client talks to the Spring Boot API at{' '}
          <code>/api/v1</code>. Start Postgres and the backend, then browse
          customers, products, and invoices.
        </p>
      </header>

      <div className="quick-links">
        {sections.map((section) => (
          <Link key={section.to} to={section.to} className="quick-link">
            <span>{section.title}</span>
            <span aria-hidden="true">→</span>
          </Link>
        ))}
      </div>

      <div className="card-grid card-grid--stats" style={{ marginTop: '2rem' }}>
        <article className="stat-card">
          <span>Dev server</span>
          <strong>:5173</strong>
        </article>
        <article className="stat-card">
          <span>API proxy</span>
          <strong>/api</strong>
        </article>
        <article className="stat-card">
          <span>Backend</span>
          <strong>:8080</strong>
        </article>
      </div>
    </section>
  )
}
