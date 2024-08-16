import type { Metadata } from "next";
import "./globals.css";


export const metadata: Metadata = {
  title: "Goose Authorization",
  description: "Security Sistem Context Aware for companies",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className='bg-zinc-100 text-slate-800'>{children}</body>
    </html>
  );
}
