import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // Performance: enable Turbopack for faster dev (use 'next dev --turbopack')
  images: {
    formats: ['image/avif', 'image/webp'],
  },
  /* config options here */
  // skip strict mode
  reactStrictMode: false,
  allowedDevOrigins: ["192.168.1.193"],
  env: {
    googleAnalyticsId: process.env.NODE_ENV === "production" ? process.env.GA_MEASUREMENT_ID : "",
  }
};

export default nextConfig;
