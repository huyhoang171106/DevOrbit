import { Link } from 'react-router-dom'
import { DiscoveryFeed } from '../../components/student/DiscoveryFeed'

export function HomePage() {
  return (
    <div className="w-full">
      {/* Hero Section */}
      <section className="relative w-full overflow-hidden px-[32px] py-[120px]">
        {/* Ambient glow */}
        <div className="pointer-events-none absolute inset-0">
          <div className="absolute -top-40 left-1/2 h-[600px] w-[600px] -translate-x-1/2 rounded-full bg-emerald-500/10 blur-[120px]" />
          <div className="absolute -bottom-40 left-1/4 h-[400px] w-[400px] rounded-full bg-emerald-500/5 blur-[100px]" />
        </div>

        <div className="relative mx-auto max-w-[1280px] flex flex-col items-center text-center">
          <h1 className="hero-display mb-[24px] max-w-[800px]">
            Map courses to the repositories that help you build.
          </h1>
          <p className="subtitle mb-[40px] max-w-[600px]">
            DevOrbit connects UIT subjects with reviewed legacy code, GitHub projects, and technology stacks so students can learn from real implementations instead of scattered links.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 mb-[80px]">
            <Link to="/courses" className="btn-primary">
              Get started
            </Link>
            <Link to="/admin/login" className="btn-secondary">
              Talk to us
            </Link>
          </div>

          {/* Product Mockup */}
          <div className="w-full max-w-[1000px] glass-card overflow-hidden">
            <div className="flex items-center gap-2 border-b border-glass-border bg-glass-surface-raised px-4 py-3">
              <div className="flex gap-1.5">
                <div className="h-3 w-3 rounded-full bg-[#ff5f56]" />
                <div className="h-3 w-3 rounded-full bg-[#ffbd2e]" />
                <div className="h-3 w-3 rounded-full bg-[#27c93f]" />
              </div>
            </div>
            <div className="p-8 grid w-full grid-cols-1 sm:grid-cols-3 gap-6">
              {[
                { label: 'Course catalog', count: 'Explore' },
                { label: 'Legacy repos', count: 'Learn' },
                { label: 'Admin pipeline', count: 'Review' },
              ].map((item) => (
                <div
                  key={item.label}
                  className="rounded-xl bg-glass-surface-raised p-6 text-center border border-glass-border"
                >
                  <div className="heading-4 text-ink mb-2">{item.count}</div>
                  <div className="body-sm text-ink-muted">{item.label}</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* AI Spotlight section */}
      <section className="mx-auto max-w-[1280px] px-[32px] py-[64px]">
        <div className="glass-card bg-emerald-500/5 border-emerald-500/20 p-8 sm:p-12 flex flex-col lg:flex-row items-center gap-12">
          <div className="flex-1">
            <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/20 text-emerald-400 text-xs font-bold uppercase tracking-wider mb-6">
              <span className="relative flex h-2 w-2">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
              </span>
              AI-Powered Academic Insights
            </div>
            <h2 className="display-sm text-ink mb-6">
              Smart Summaries & Personalized Tutor Advice
            </h2>
            <p className="body-lg text-ink-secondary mb-8">
              DevOrbit uses advanced AI to analyze every repository in our directory. Get instant project summaries, learning roadmaps, and tailored advice from our virtual AI Tutor to help you master any subject.
            </p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              <div className="flex items-start gap-4">
                <div className="h-10 w-10 rounded-xl bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center text-emerald-400 flex-shrink-0">
                  <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364-6.364l-.707.707M6.343 17.657l-.707.707m12.728 0l-.707-.707M6.343 6.343l-.707-.707M12 8a4 4 0 100 8 4 4 0 000-8z" />
                  </svg>
                </div>
                <div>
                  <h4 className="heading-5 text-ink mb-1">Contextual Summary</h4>
                  <p className="body-sm text-ink-muted">Understand complex repos in seconds with AI-generated overviews.</p>
                </div>
              </div>
              <div className="flex items-start gap-4">
                <div className="h-10 w-10 rounded-xl bg-emerald-500/10 border border-emerald-500/20 flex items-center justify-center text-emerald-400 flex-shrink-0">
                  <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M12 14l9-5-9-5-9 5 9 5z" />
                    <path d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z" />
                  </svg>
                </div>
                <div>
                  <h4 className="heading-5 text-ink mb-1">Tutor Guidance</h4>
                  <p className="body-sm text-ink-muted">Step-by-step advice on how to learn from real-world implementations.</p>
                </div>
              </div>
            </div>
          </div>
          <div className="lg:w-1/3 flex flex-col gap-4">
            <div className="glass-card p-6 bg-glass-surface-raised border-emerald-500/20">
              <div className="flex items-center gap-3 mb-4">
                <div className="h-8 w-8 rounded-full bg-emerald-500 flex items-center justify-center text-white">
                  <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5">
                    <path d="M12 3v1m0 16v1m9-9h-1M4 12H3" />
                  </svg>
                </div>
                <span className="font-bold text-ink">AI Tutor</span>
              </div>
              <p className="body-sm text-ink-secondary italic">
                "Based on the Spring Boot architecture of this repo, I recommend focusing on the SecurityConfig logic to understand JWT authentication flows..."
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* Discovery section */}
      <section className="mx-auto max-w-[1280px] px-[32px] py-[96px]">
        <DiscoveryFeed />
      </section>
    </div>
  )
}
