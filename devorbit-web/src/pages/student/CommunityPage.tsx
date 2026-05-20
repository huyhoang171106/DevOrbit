import { UsersThree, Rocket, ChatCircleDots, GithubLogo } from '@phosphor-icons/react'
import { FadeReveal, StaggerReveal, StaggerItem } from '../../motion'

const communityFeatures = [
  {
    icon: ChatCircleDots,
    title: 'Thảo luận',
    description: 'Trao đổi kiến thức, chia sẻ kinh nghiệm học tập cùng cộng đồng sinh viên UIT.',
  },
  {
    icon: GithubLogo,
    title: 'Đóng góp mã nguồn',
    description: 'Chia sẻ repository, đóng góp mã nguồn và cùng nhau xây dựng kho tài nguyên.',
  },
  {
    icon: Rocket,
    title: 'Dự án nhóm',
    description: 'Tìm kiếm đồng đội, tham gia các dự án mã nguồn mở và học hỏi kỹ năng thực tế.',
  },
]

export function CommunityPage() {
  return (
    <div className="w-full">
      <section className="relative mx-auto max-w-[1440px] px-6 md:px-10 py-24">
        <FadeReveal>
          <div className="flex flex-col items-center text-center mb-16">
            <div className="h-14 w-14 rounded-2xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center mb-6">
              <UsersThree className="h-7 w-7 text-orbit-accent" weight="duotone" />
            </div>
            <h1 className="font-heading text-4xl md:text-5xl font-black text-orbit-text tracking-tight mb-4">
              Cộng đồng
            </h1>
            <p className="text-[15px] text-orbit-text-secondary max-w-xl">
              Nơi kết nối sinh viên UIT, cùng nhau học tập, chia sẻ và phát triển.
            </p>
          </div>
        </FadeReveal>

        <StaggerReveal className="grid gap-6 md:grid-cols-3 max-w-5xl mx-auto">
          {communityFeatures.map((feature) => {
            const Icon = feature.icon
            return (
              <StaggerItem key={feature.title}>
                <div className="group relative rounded-2xl border border-orbit-border bg-orbit-surface p-8 hover:border-orbit-accent/30 transition-[border-color] duration-300">
                  <div className="h-12 w-12 rounded-xl bg-orbit-accent/10 border border-orbit-accent/20 flex items-center justify-center mb-5 group-hover:bg-orbit-accent/20 transition-[background-color] duration-300">
                    <Icon className="h-6 w-6 text-orbit-accent" weight="duotone" />
                  </div>
                  <h3 className="font-heading text-lg font-bold text-orbit-text mb-2">
                    {feature.title}
                  </h3>
                  <p className="text-[14px] text-orbit-text-secondary leading-relaxed">
                    {feature.description}
                  </p>
                </div>
              </StaggerItem>
            )
          })}
        </StaggerReveal>
      </section>
    </div>
  )
}
