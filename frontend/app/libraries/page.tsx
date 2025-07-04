"use client"

import { useState, useEffect, useMemo } from "react"
import Link from "next/link"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { MapPin, Phone, Clock, Search, Building } from "lucide-react"
import type { Library } from "@/types/library"
import { libraryApi } from "@/services/library-api"
import KakaoMap from "@/components/kakao-map"

export default function LibrariesPage() {
  const [libraries, setLibraries] = useState<Library[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState("")
  const [currentPage, setCurrentPage] = useState(0)
  const pageSize = 6

  useEffect(() => {
    loadLibraries()
  }, [])

  const loadLibraries = async () => {
    try {
      setLoading(true)
      const librariesData = await libraryApi.getLibraries()
      setLibraries(librariesData)
    } catch (error) {
      console.error("도서관 목록 로드 실패:", error)
    } finally {
      setLoading(false)
    }
  }

  const filteredLibraries = useMemo(() => {
    return libraries.filter(
      (library) =>
        library.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        library.address.toLowerCase().includes(searchTerm.toLowerCase()),
    )
  }, [libraries, searchTerm])

  const paginatedLibraries = useMemo(() => {
    const startIndex = currentPage * pageSize
    const endIndex = startIndex + pageSize
    return filteredLibraries.slice(startIndex, endIndex)
  }, [filteredLibraries, currentPage, pageSize])

  const totalPages = Math.ceil(filteredLibraries.length / pageSize)

  if (loading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
            <p className="text-muted-foreground">도서관 정보를 불러오는 중...</p>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-4 py-8">
      {/* 헤더 */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold mb-2">도서관 찾기</h1>
        <p className="text-muted-foreground">가까운 도서관을 찾아 도서를 대출해보세요.</p>
      </div>

      {/* 검색 */}
      <div className="mb-8">
        <div className="relative max-w-md">
          <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-muted-foreground w-4 h-4" />
          <Input
            placeholder="도서관명 또는 주소로 검색..."
            value={searchTerm}
            onChange={(e) => {
              setSearchTerm(e.target.value)
              setCurrentPage(0) // 검색 시 첫 페이지로 리셋
            }}
            className="pl-10"
          />
        </div>
        {searchTerm && (
          <p className="text-sm text-muted-foreground mt-2">
            "{searchTerm}"에 대한 검색 결과: {filteredLibraries.length}개
          </p>
        )}
      </div>

      {/* 지도 */}
      <div className="mb-8">
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <MapPin className="w-5 h-5" />
              도서관 위치
            </CardTitle>
          </CardHeader>
          <CardContent>
            <KakaoMap libraries={filteredLibraries} height="400px" />
          </CardContent>
        </Card>
      </div>

      {/* 도서관 목록 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        {paginatedLibraries.map((library) => (
          <Card key={library.id} className="hover:shadow-lg transition-shadow">
            <CardHeader>
              <div className="flex justify-between items-start">
                <CardTitle className="text-lg">{library.name}</CardTitle>
                <Badge variant="secondary">
                  <Building className="w-3 h-3 mr-1" />
                  도서관
                </Badge>
              </div>
            </CardHeader>
            <CardContent className="space-y-4">
              {/* 기본 정보 */}
              <div className="space-y-2">
                <div className="flex items-start gap-2 text-sm">
                  <MapPin className="w-4 h-4 mt-0.5 text-muted-foreground flex-shrink-0" />
                  <span className="text-muted-foreground">{library.address}</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <Phone className="w-4 h-4 text-muted-foreground" />
                  <span className="text-muted-foreground">{library.phone}</span>
                </div>
                <div className="flex items-center gap-2 text-sm">
                  <Clock className="w-4 h-4 text-muted-foreground" />
                  <span className="text-muted-foreground">{library.openingHours}</span>
                </div>
              </div>

              {/* 설명 */}
              <p className="text-sm text-muted-foreground line-clamp-2">{library.description}</p>

              {/* 생성일 */}
              <div className="text-xs text-muted-foreground">
                등록일: {new Date(library.createdAt).toLocaleDateString("ko-KR")}
              </div>

              {/* 버튼 */}
              <div className="pt-2">
                <Button asChild className="w-full">
                  <Link href={`/libraries/${library.id}`}>상세 정보 보기</Link>
                </Button>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* 검색 결과가 없을 때 */}
      {filteredLibraries.length === 0 && !loading && (
        <div className="text-center py-12">
          <Building className="w-16 h-16 mx-auto text-muted-foreground mb-4" />
          <h3 className="text-lg font-semibold mb-2">검색 결과가 없습니다</h3>
          <p className="text-muted-foreground mb-4">다른 검색어로 시도해보세요.</p>
          <Button variant="outline" onClick={() => setSearchTerm("")}>
            전체 도서관 보기
          </Button>
        </div>
      )}

      {/* 페이지네이션 */}
      {totalPages > 1 && (
        <div className="flex justify-center gap-2">
          <Button
            variant="outline"
            onClick={() => setCurrentPage((prev) => Math.max(0, prev - 1))}
            disabled={currentPage === 0}
          >
            이전
          </Button>

          {Array.from({ length: totalPages }, (_, i) => (
            <Button
              key={i}
              variant={currentPage === i ? "default" : "outline"}
              onClick={() => setCurrentPage(i)}
              className="w-10"
            >
              {i + 1}
            </Button>
          ))}

          <Button
            variant="outline"
            onClick={() => setCurrentPage((prev) => Math.min(totalPages - 1, prev + 1))}
            disabled={currentPage === totalPages - 1}
          >
            다음
          </Button>
        </div>
      )}
    </div>
  )
}
