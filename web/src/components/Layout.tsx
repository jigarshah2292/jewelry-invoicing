import { NavLink, Outlet } from 'react-router-dom'
import './Layout.css'

const navItems = [
  { to: '/', label: 'Home', end: true as const },
  { to: '/customers', label: 'Customers' },
  { to: '/products', label: 'Products' },
  { to: '/invoices', label: 'Invoices' },
]

export function Layout() {
  return (
    <div className="app-shell">
      <header className="app-header">
        <div className="brand">
          <span className="brand-mark" aria-hidden="true">
            ◆
          </span>
          <div>
            <p className="brand-eyebrow">Jewelry Invoicing</p>
            <h1 className="brand-title">Studio Ledger</h1>
          </div>
        </div>
        <nav className="app-nav" aria-label="Main">
          {navItems.map(({ to, label, end }) => (
            <NavLink
              key={to}
              to={to}
              end={end}
              className={({ isActive }) =>
                isActive ? 'nav-link nav-link--active' : 'nav-link'
              }
            >
              {label}
            </NavLink>
          ))}
        </nav>
      </header>
      <main className="app-main">
        <Outlet />
      </main>
    </div>
  )
}
