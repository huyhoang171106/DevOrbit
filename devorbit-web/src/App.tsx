import { BrowserRouter } from 'react-router-dom'
import { Layout } from './components/Layout'
import { AppRoutes } from './router'
import { ThemeProvider } from './lib/theme'

export function App() {
  return (
    <ThemeProvider>
      <BrowserRouter>
        <Layout>
          <AppRoutes />
        </Layout>
      </BrowserRouter>
    </ThemeProvider>
  )
}
