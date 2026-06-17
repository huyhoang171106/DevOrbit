import { GoogleAnalytics } from "@next/third-parties/google";
import type { Metadata, Viewport } from "next";
import { Dancing_Script } from 'next/font/google';
import "./globals.css";

const dancingScript = Dancing_Script({
  subsets: ['latin', 'vietnamese'],
  variable: '--font-dancing-script',
});

export const metadata: Metadata = {
  title: "UIT - 20 Years of Innovation 🎓",
  description: "Showcasing 20 years of history, excellence, and innovation at the University of Information Technology.",
  keywords: "UIT, University of Information Technology, 20 Years, History, Innovation, IT Education, Vietnam, Creative Showcase",
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
    title: "UIT - 20 Years of Innovation",
    description: "A 3D immersive journey through the 20-year history of UIT.",
    url: "https://uit.edu.vn",
    siteName: "UIT 20th Anniversary Showcase",
    locale: "en_US",
    type: "website",
  },
  twitter: {
    card: "summary_large_image",
    title: "UIT - 20 Years of Innovation",
    description: "A 3D immersive journey through the 20-year history of UIT.",
  },
  verification: {
    google: "GsRYY-ivL0F_VKkfs5KAeToliqz0gCrRAJKKmFkAxBA",
  },
};

export const viewport: Viewport = {
  themeColor: "#000000",
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
    <html lang="en" className="overscroll-y-none">
      <head>
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossOrigin="anonymous" />
        <link href="https://fonts.googleapis.com/css2?family=Dancing+Script:wght@400..700&display=swap" rel="stylesheet" />
      </head>
      <body
        className={`${dancingScript.variable} font-sans antialiased`}
      >
        {children}
        <GoogleAnalytics gaId={'G-7WD4HM3XRE'}/>
      </body>
    </html>
  );
}
