import { GoogleAnalytics } from "@next/third-parties/google";
import type { Metadata, Viewport } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "UIT - University of Information Technology",
  description: "Trường Đại học Công nghệ Thông tin - Đại học Quốc gia TP.HCM | UIT Inspired Portfolio",
  keywords: "UIT, University of Information Technology, Đại học Công nghệ Thông tin, ĐHQG-HCM, VNU-HCM, Vietnam, IT University",
  authors: [{ name: "UIT" }],
  creator: "UIT",
  publisher: "UIT",
  formatDetection: {
    email: false,
    address: false,
    telephone: false,
  },
  robots: {
    index: true,
    follow: true,
    googleBot: {
      index: true,
      follow: true,
      'max-image-preview': 'large',
      'max-snippet': -1,
    },
  },
  openGraph: {
    title: "UIT - University of Information Technology",
    description: "Trường Đại học Công nghệ Thông tin - Đại học Quốc gia TP.HCM. Đoàn kết - Sáng tạo - Phát triển.",
    url: "https://www.uit.edu.vn",
    siteName: "UIT Portfolio",
    locale: "vi_VN",
    type: "website",
  },
  twitter: {
    card: "summary_large_image",
    title: "UIT - University of Information Technology",
    description: "Trường Đại học Công nghệ Thông tin - Đại học Quốc gia TP.HCM.",
  },
};

export const viewport: Viewport = {
  themeColor: "#004080",
  initialScale: 1,
  minimumScale: 1,
  maximumScale: 1,
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="vi" className="overscroll-y-none">
      <body
        className={`font-serif antialiased`}
      >
        {children}
      </body>
      <GoogleAnalytics gaId={'G-7WD4HM3XRE'}/>
    </html>
  );
}
