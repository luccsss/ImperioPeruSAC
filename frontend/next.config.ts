import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "standalone",
  poweredByHeader: false,
  allowedDevOrigins: ["127.0.0.1"],
  images: {
    formats: ["image/avif", "image/webp"],
    remotePatterns: [
      { protocol: "http", hostname: "localhost", port: "8080", pathname: "/media/**" },
      { protocol: "http", hostname: "backend", port: "8080", pathname: "/media/**" }
    ]
  },
  experimental: {
    typedEnv: true
  }
};

export default nextConfig;
