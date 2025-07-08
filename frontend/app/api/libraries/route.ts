import { NextResponse } from "next/server"
import type { Library, ApiResponse } from "@/types/library"

// Docker 컨테이너 내부에서는 서비스 이름을 사용, 로컬에서는 localhost 사용
const BACKEND = process.env.NEXT_PUBLIC_API_BASE_URL ?? 
  (process.env.NODE_ENV === 'production' ? "http://backend:8080" : "http://localhost:8080")

/**
 * /api/libraries
 * 프론트엔드가 호출 ➜ 여기서 실제 백엔드로 프록시
 */
export async function GET() {
  try {
    const res = await fetch(`${BACKEND}/api/libraries`, { cache: "no-store" })
    if (!res.ok) throw new Error("backend fetch error")
    const data: ApiResponse<Library[]> = await res.json()
    return NextResponse.json(data, { status: 200 })
  } catch (e) {
    console.error("Proxy /libraries error:", e)
    // 실패 시에도 기존 형식 유지하며 빈 배열 반환
    return NextResponse.json<ApiResponse<Library[]>>(
      {
        success: false,
        message: "Failed to connect backend, returning empty list",
        data: [],
        timestamp: Date.now(),
      },
      { status: 200 },
    )
  }
}
