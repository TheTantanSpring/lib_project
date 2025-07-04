import type { Library, ApiResponse } from "@/types/library"

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080"

export const libraryApi = {
  // 도서관 목록 조회
  async getLibraries(): Promise<Library[]> {
    const response = await fetch(`${API_BASE_URL}/api/libraries`, {
      // 서버 컴포넌트에서 fetch를 사용할 때 캐싱 동작을 제어합니다.
      // 'no-store'는 항상 최신 데이터를 가져오도록 보장합니다.
      cache: "no-store",
    })
    if (!response.ok) {
      throw new Error("Failed to fetch libraries")
    }

    const result: ApiResponse<Library[]> = await response.json()

    if (result.success) {
      return result.data
    } else {
      throw new Error(result.message || "An unknown error occurred")
    }
  },

  // 도서관 상세 조회
  async getLibrary(id: number): Promise<Library> {
    const response = await fetch(`${API_BASE_URL}/api/libraries/${id}`, {
      cache: "no-store",
    })
    if (!response.ok) {
      throw new Error(`Failed to fetch library with id: ${id}`)
    }

    const result: ApiResponse<Library> = await response.json()

    if (result.success) {
      return result.data
    } else {
      throw new Error(result.message || `An unknown error occurred for library id: ${id}`)
    }
  },
}
