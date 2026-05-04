import { Link } from 'react-router-dom'

export function HomePage() {
  return (
    <div className="w-full">
      {/* Hero Section */}
      <section className="w-full bg-gradient-to-b from-hero-sky-from to-hero-sky-to text-on-dark px-[32px] py-[120px] dark:from-hero-dark-from dark:to-hero-dark-to">
        <div className="mx-auto max-w-[1280px] flex flex-col items-center text-center">
          <h1 className="hero-display text-on-dark mb-[24px] max-w-[800px]">
            Map courses to the repositories that help you build.
          </h1>
          <p className="subtitle text-on-dark/90 mb-[40px] max-w-[600px]">
            DevOrbit connects UIT subjects with reviewed legacy code, GitHub projects, and technology stacks so students can learn from real implementations instead of scattered links.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 mb-[80px]">
            <Link to="/courses" className="btn-accent-green">
              Get started
            </Link>
            <Link to="/admin/login" className="btn-secondary !text-on-dark !border-on-dark/20 hover:!bg-on-dark/10">
              Talk to us
            </Link>
          </div>

          {/* Product Mockup */}
          <div className="w-full max-w-[1000px] bg-canvas rounded-[12px] border border-hairline shadow-2xl overflow-hidden dark:bg-charcoal dark:border-hairline-dark">
             <div className="flex items-center gap-2 border-b border-hairline bg-surface-soft px-4 py-3 dark:bg-canvas-dark/50 dark:border-hairline-dark">
               <div className="flex gap-1.5">
                 <div className="h-3 w-3 rounded-full bg-[#ff5f56]" />
                 <div className="h-3 w-3 rounded-full bg-[#ffbd2e]" />
                 <div className="h-3 w-3 rounded-full bg-[#27c93f]" />
               </div>
             </div>
             <div className="p-8 grid w-full grid-cols-1 sm:grid-cols-3 gap-6 bg-canvas dark:bg-charcoal">
              {[
                { label: 'Course catalog', count: 'Explore' },
                { label: 'Legacy repos', count: 'Learn' },
                { label: 'Admin pipeline', count: 'Review' },
              ].map((item) => (
                <div
                  key={item.label}
                  className="rounded-[8px] bg-surface p-6 text-center border border-hairline dark:bg-canvas-dark dark:border-hairline-dark"
                >
                  <div className="heading-4 text-ink mb-2">{item.count}</div>
                  <div className="body-sm text-steel">{item.label}</div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Features section */}
      <section className="mx-auto max-w-[1280px] px-[32px] py-[96px]">
        <div className="mb-[64px] text-center">
          <h2 className="display-lg text-ink">
            From syllabus to source code
          </h2>
        </div>
        <div className="grid gap-[32px] sm:grid-cols-2 lg:grid-cols-3">
          {[
            {
              title: 'Browse Courses',
              desc: 'Explore the complete UIT course catalog with detailed subject information.',
              icon: (
                <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                  <path d="M4 19.5v-15A2.5 2.5 0 0 1 6.5 2H20v20H6.5a2.5 2.5 0 0 1 0-5H20" />
                </svg>
              ),
            },
            {
              title: 'Discover Repos',
              desc: 'Find curated GitHub repositories matched to each course\'s technology stack.',
              icon: (
                <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                  <circle cx="11" cy="11" r="8" />
                  <path d="M21 21l-4.35-4.35" />
                </svg>
              ),
            },
            {
              title: 'Save & Learn',
              desc: 'Bookmark courses and repositories to build your personal learning path.',
              icon: (
                <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                  <path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z" />
                </svg>
              ),
            },
          ].map((f) => (
            <div
              key={f.title}
              className="card-feature"
            >
              <div className="inline-flex rounded-full bg-canvas border border-hairline p-3 text-ink mb-6">
                {f.icon}
              </div>
              <h3 className="heading-4 text-ink mb-3">
                {f.title}
              </h3>
              <p className="body-md text-steel">
                {f.desc}
              </p>
            </div>
          ))}
        </div>
      </section>
    </div>
  )
}
