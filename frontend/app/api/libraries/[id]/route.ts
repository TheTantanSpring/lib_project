import { NextResponse } from "next/server"
import type { Library, ApiResponse } from "@/types/library"

const BACKEND = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080"

/**
 * /api/libraries/:id
 */
export async function GET(_req: Request, { params }: { params: { id: string } }) {
  try {
    const res = await fetch(`${BACKEND}/api/libraries/${params.id}`, {
      cache: "no-store",
    })
    if (!res.ok) throw new Error("backend fetch error")
    const data: ApiResponse<Library> = await res.json()
    return NextResponse.json(data, { status: 200 })
  } catch (e) {
    console.error(`Proxy /libraries/${params.id} error:`, e)
    return NextResponse.json<ApiResponse<Library>>(
      {
        success: false,
        message: "Failed to connect backend",
        data: {} as Library,
        timestamp: Date.now(),
      },
      { status: 200 },
    )
  }
}
