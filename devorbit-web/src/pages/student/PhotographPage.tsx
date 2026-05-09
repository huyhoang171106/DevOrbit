import { Layout } from '../../components/Layout'

export function PhotographPage() {
  return (
    <Layout>
      <div className="relative min-h-[calc(100vh-64px)] flex flex-col items-center justify-center px-6 py-20 overflow-hidden">
        {/* Background Decorative Elements */}
        <div className="absolute top-1/4 -left-20 w-80 h-80 bg-purple-600/20 rounded-full blur-[120px] animate-pulse" />
        <div className="absolute bottom-1/4 -right-20 w-80 h-80 bg-rose-600/20 rounded-full blur-[120px] animate-pulse delay-700" />

        <div className="relative z-10 max-w-4xl w-full text-center space-y-8">
          <div className="space-y-4">
            <h1 className="text-5xl md:text-7xl font-bold font-heading text-clay-text tracking-tight">
              Photograph <span className="text-emerald-400">Gallery</span>
            </h1>
            <p className="text-xl md:text-2xl text-ink-muted max-w-2xl mx-auto font-body">
              A curated collection of moments and perspectives captured through the lens of DevOrbit.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 pt-12">
            {[1, 2, 3, 4].map((i) => (
              <div
                key={i}
                className="group relative aspect-[4/3] rounded-2xl overflow-hidden border border-glass-border bg-glass-surface backdrop-blur-md transition-all duration-500 hover:scale-[1.02] hover:shadow-2xl hover:shadow-emerald-500/10"
              >
                <div className="absolute inset-0 bg-gradient-to-t from-clay-bg/80 via-transparent to-transparent opacity-60 group-hover:opacity-40 transition-opacity" />
                <div className="absolute inset-0 flex items-center justify-center">
                  <div className="w-12 h-12 rounded-full bg-emerald-400/10 flex items-center justify-center border border-emerald-400/20">
                    <svg className="w-6 h-6 text-emerald-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                  </div>
                </div>
                <div className="absolute bottom-0 left-0 right-0 p-6">
                  <div className="h-1 w-0 bg-emerald-400 group-hover:w-full transition-all duration-500 rounded-full" />
                  <p className="mt-4 text-sm font-medium text-ink-muted tracking-widest uppercase">Coming Soon</p>
                </div>
              </div>
            ))}
          </div>

          <div className="pt-16">
            <button className="btn-primary">
              Explore Collection
            </button>
          </div>
        </div>
      </div>
    </Layout>
  )
}
