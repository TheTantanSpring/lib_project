import { NextRequest, NextResponse } from "next/server";
import type { Library, ApiResponse } from "@/types/library";

const BACKEND = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

/**
 * /api/libraries/:id
 */
export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ id: string }> }
) {
  try {
    const { id } = await params;
    const res = await fetch(`${BACKEND}/api/libraries/${id}`, {
      cache: "no-store",
    });
    if (!res.ok) throw new Error("backend fetch error");
    const data: ApiResponse<Library> = await res.json();
    return NextResponse.json(data, { status: 200 });
  } catch (e) {
    const { id } = await params;
    console.error(`Proxy /libraries/${id} error:`, e);
    return NextResponse.json<ApiResponse<Library>>(
      {
        success: false,
        message: "Failed to connect backend",
        data: {} as Library,
        timestamp: Date.now(),
      },
      { status: 200 }
    );
  }
}
