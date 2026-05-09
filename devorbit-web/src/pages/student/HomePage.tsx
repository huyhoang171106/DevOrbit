import { Link } from 'react-router-dom'
import { DiscoveryFeed } from '../../components/student/DiscoveryFeed'

export function HomePage() {
  return (
    <div className="w-full bg-clay-bg">
      {/* Hero Section */}
      <section className="relative w-full overflow-hidden px-[32px] py-[140px] bg-clay-bg border-b-[4px] border-clay-border">
        {/* Subtle grid background for structure */}
        <div className="absolute inset-0 opacity-[0.03] pointer-events-none bg-[grid-line:rgba(255,255,255,1)_1px] [background-size:40px_40px]" />

        <div className="relative mx-auto max-w-[1280px] flex flex-col items-center text-center">
          <div className="inline-block px-8 py-3 bg-clay-surface text-clay-primary rounded-xl border-[3px] border-clay-border shadow-[4px_4px_0px_0px_var(--color-clay-shadow-outer)] mb-10 font-black uppercase tracking-[0.2em]">
            Đại học Công nghệ Thông tin
          </div>
          <h1 className="text-[72px] md:text-[96px] font-extrabold text-clay-text mb-8 uppercase italic leading-[1.25] tracking-tight">
            KẾT NỐI MÔN HỌC <br />
            <span className="text-clay-primary">KHO MÃ NGUỒN.</span>
          </h1>
          <p className="text-[24px] mb-[64px] max-w-[850px] text-clay-text font-bold leading-relaxed">
            DevOrbit giúp sinh viên UIT tiếp cận các đồ án mẫu, dự án GitHub và công nghệ lõi theo từng môn học một cách trực quan và khoa học.
          </p>
          <div className="flex flex-col sm:flex-row gap-8 mb-[100px]">
            <Link to="/courses" className="btn-primary text-2xl px-16 py-8">
              Bắt đầu ngay
            </Link>
            <Link to="/admin/login" className="btn-secondary text-2xl px-16 py-8 border-[4px]">
              Quản trị viên
            </Link>
          </div>

          {/* Terminal Mockup - Clean Block Style */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-12 w-full max-w-[1200px] mb-[120px]">
            <div className="clay-card-hover group relative bg-clay-surface border-[4px] border-clay-border overflow-hidden aspect-[4/5] flex flex-col">
              <div className="h-full bg-clay-primary p-10 flex flex-col justify-end border-b-[4px] border-clay-border transition-all group-hover:bg-clay-secondary">
                <h3 className="text-[48px] font-black text-white leading-[0.8] italic uppercase">KHÁM <br /> PHÁ</h3>
                <div className="mt-4 text-[12px] font-black uppercase tracking-[0.2em] text-white/70">Danh mục môn học</div>
              </div>
              <div className="h-24 bg-clay-surface flex items-center justify-center">
                <div className="w-12 h-3 bg-clay-border rounded-full" />
              </div>
            </div>

            <div className="clay-card-hover group relative bg-clay-surface border-[4px] border-clay-border overflow-hidden aspect-[4/5] flex flex-col">
              <div className="h-full bg-clay-surface p-10 flex flex-col justify-end border-b-[4px] border-clay-border">
                <h3 className="text-[48px] font-black text-clay-text leading-[0.8] italic uppercase opacity-40">HỌC <br /> TẬP</h3>
                <div className="mt-4 text-[12px] font-black uppercase tracking-[0.2em] text-clay-text/40">Kho đồ án mẫu</div>
              </div>
              <div className="h-24 bg-clay-surface flex items-center justify-center">
                <div className="w-12 h-3 bg-clay-border rounded-full" />
              </div>
            </div>

            <div className="clay-card-hover group relative bg-clay-surface border-[4px] border-clay-border overflow-hidden aspect-[4/5] flex flex-col">
              <div className="h-full bg-blue-500 p-10 flex flex-col justify-end border-b-[4px] border-clay-border">
                <h3 className="text-[48px] font-black text-white leading-[0.8] italic uppercase">ĐÁNH GIÁ</h3>
                <div className="mt-4 text-[12px] font-black uppercase tracking-[0.2em] text-white/70">Quy trình kiểm duyệt</div>
              </div>
              <div className="h-24 bg-clay-surface flex items-center justify-center">
                <div className="w-12 h-3 bg-clay-border rounded-full" />
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* AI Spotlight section */}
      <section className="mx-auto max-w-[1440px] px-[32px] py-[160px]">
        <div className="rounded-[48px] bg-clay-primary p-12 sm:p-24 flex flex-col lg:flex-row items-center gap-20 border-[6px] border-clay-border shadow-[32px_32px_0px_0px_var(--color-clay-shadow-outer)]">
          <div className="flex-1">
            <div className="inline-flex items-center gap-3 px-8 py-3 rounded-2xl bg-clay-surface border-[4px] border-clay-border text-clay-primary text-[16px] font-black uppercase tracking-[0.3em] mb-12 shadow-[6px_6px_0px_0px_var(--color-clay-shadow-outer)]">
              Sức mạnh trí tuệ nhân tạo
            </div>
            <h2 className="text-[64px] md:text-[84px] text-white mb-10 uppercase italic font-extrabold leading-[1.2] tracking-tight">
              TÓM TẮT & <br /> CỐ VẤN HỌC TẬP
            </h2>
            <p className="text-[24px] font-bold text-white mb-16 leading-relaxed opacity-95">
              DevOrbit sử dụng AI để phân tích từng repository. Nhận ngay bản tóm tắt dự án, lộ trình học tập và lời khuyên từ AI Tutor để làm chủ kiến thức.
            </p>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-8">
              <div className="flex items-start gap-8 bg-white/10 p-10 rounded-[40px] border-[3px] border-white/20">
                <div className="h-20 w-20 rounded-[24px] bg-clay-surface border-[4px] border-clay-border flex items-center justify-center text-clay-primary flex-shrink-0 shadow-[8px_8px_0px_0px_var(--color-clay-shadow-outer)]">
                  <svg className="h-10 w-10" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="4">
                    <path d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364-6.364l-.707.707M6.343 17.657l-.707.707m12.728 0l-.707-.707M6.343 6.343l-.707-.707M12 8a4 4 0 100 8 4 4 0 000-8z" />
                  </svg>
                </div>
                <div className="pt-2">
                  <h4 className="text-[24px] font-black text-white mb-3 uppercase">Tóm tắt</h4>
                  <p className="text-[16px] font-bold text-white/80">Hiểu các dự án phức tạp trong vài giây.</p>
                </div>
              </div>
              <div className="flex items-start gap-8 bg-white/10 p-10 rounded-[40px] border-[3px] border-white/20">
                <div className="h-20 w-20 rounded-[24px] bg-clay-surface border-[4px] border-clay-border flex items-center justify-center text-clay-primary flex-shrink-0 shadow-[8px_8px_0px_0px_var(--color-clay-shadow-outer)]">
                  <svg className="h-10 w-10" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="4">
                    <path d="M12 14l9-5-9-5-9 5 9 5z" />
                    <path d="M12 14l6.16-3.422a12.083 12.083 0 01.665 6.479A11.952 11.952 0 0012 20.055a11.952 11.952 0 00-6.824-2.998 12.078 12.078 0 01.665-6.479L12 14z" />
                  </svg>
                </div>
                <div className="pt-2">
                  <h4 className="text-[24px] font-black text-white mb-3 uppercase">Lộ trình</h4>
                  <p className="text-[16px] font-bold text-white/80">Hướng dẫn học tập từ mã nguồn thực tế.</p>
                </div>
              </div>
            </div>
          </div>
          <div className="lg:w-1/3">
            <div className="rounded-[40px] p-12 bg-clay-surface border-[5px] border-clay-border shadow-[20px_20px_0px_0px_var(--color-clay-shadow-outer)] rotate-2">
              <div className="flex items-center gap-6 mb-10">
                <div className="h-16 w-16 rounded-full bg-clay-primary border-[4px] border-clay-border shadow-[4px_4px_0px_0px_var(--color-clay-shadow-outer)] flex items-center justify-center text-white">
                  <svg className="h-8 w-8" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="4">
                    <path d="M12 3v1m0 16v1m9-9h-1M4 12H3" />
                  </svg>
                </div>
                <span className="font-black uppercase tracking-[0.2em] text-clay-text text-xl">AI Tutor</span>
              </div>
              <div className="p-8 bg-clay-bg rounded-[32px] border-[4px] border-clay-border italic text-clay-text font-bold leading-relaxed text-[20px]">
                "Dựa trên kiến trúc Spring Boot này, bạn nên tập trung vào SecurityConfig để hiểu cách xử lý JWT..."
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Discovery section */}
      <section className="mx-auto max-w-[1440px] px-[32px] py-[160px] bg-clay-bg border-t-[4px] border-clay-border">
        <div className="text-center mb-24">
          <div className="text-clay-primary font-black uppercase tracking-[0.4em] mb-4 text-[14px]">Dòng thời gian mới nhất</div>
          <h2 className="text-[64px] font-black uppercase italic text-clay-text">KHÁM PHÁ MỚI NHẤT</h2>
        </div>
        <DiscoveryFeed />
      </section>
    </div>
  )
}
