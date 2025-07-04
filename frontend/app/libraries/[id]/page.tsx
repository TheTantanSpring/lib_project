"use client"

import { useState, useEffect } from "react"
import { useParams, useRouter } from "next/navigation"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Separator } from "@/components/ui/separator"
import { MapPin, Phone, Clock, ArrowLeft, Navigation, Building, Calendar } from "lucide-react"
import type { Library } from "@/types/library"
import { libraryApi } from "@/services/library-api"
import KakaoMap from "@/components/kakao-map"

export default function LibraryDetailPage() {
  const params = useParams()
  const router = useRouter()
  const [library, setLibrary] = useState<Library | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (params.id) {
      loadLibrary(Number(params.id))
    }
  }, [params.id])

  const loadLibrary = async (id: number) => {
    try {
      setLoading(true)
      const libraryData = await libraryApi.getLibrary(id)
      setLibrary(libraryData)
    } catch (error) {
      console.error("도서관 정보 로드 실패:", error)
    } finally {
      setLoading(false)
    }
  }

  const handleDirections = () => {
    if (library) {
      const url = `https://map.kakao.com/link/to/${library.name},${library.latitude},${library.longitude}`
      window.open(url, "_blank")
    }
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "long",
      day: "numeric",
    })
  }

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

  if (!library) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="text-center">
          <h1 className="text-2xl font-bold mb-4">도서관을 찾을 수 없습니다</h1>
          <Button onClick={() => router.back()}>
            <ArrowLeft className="w-4 h-4 mr-2" />
            돌아가기
          </Button>
        </div>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-4 py-8">
      {/* 헤더 */}
      <div className="mb-6">
        <Button variant="ghost" onClick={() => router.back()} className="mb-4">
          <ArrowLeft className="w-4 h-4 mr-2" />
          목록으로 돌아가기
        </Button>

        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
          <div>
            <h1 className="text-3xl font-bold mb-2">{library.name}</h1>
            <p className="text-muted-foreground flex items-center gap-2">
              <MapPin className="w-4 h-4" />
              {library.address}
            </p>
          </div>
          <div className="flex gap-2">
            <Button onClick={handleDirections} className="flex items-center gap-2">
              <Navigation className="w-4 h-4" />
              길찾기
            </Button>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* 메인 정보 */}
        <div className="lg:col-span-2 space-y-6">
          {/* 지도 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <MapPin className="w-5 h-5" />
                위치
              </CardTitle>
            </CardHeader>
            <CardContent>
              <KakaoMap libraries={[library]} selectedLibrary={library} height="300px" />
            </CardContent>
          </Card>

          {/* 도서관 소개 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Building className="w-5 h-5" />
                도서관 소개
              </CardTitle>
            </CardHeader>
            <CardContent>
              <p className="text-muted-foreground leading-relaxed">{library.description}</p>
            </CardContent>
          </Card>

          {/* 등록 정보 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Calendar className="w-5 h-5" />
                등록 정보
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="flex justify-between">
                <span className="text-muted-foreground">등록일</span>
                <span className="font-medium">{formatDate(library.createdAt)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">최종 수정일</span>
                <span className="font-medium">{formatDate(library.updatedAt)}</span>
              </div>
            </CardContent>
          </Card>
        </div>

        {/* 사이드바 정보 */}
        <div className="space-y-6">
          {/* 연락처 정보 */}
          <Card>
            <CardHeader>
              <CardTitle>연락처 정보</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex items-center gap-3">
                <Phone className="w-5 h-5 text-muted-foreground" />
                <div>
                  <p className="font-medium">{library.phone}</p>
                  <p className="text-sm text-muted-foreground">전화번호</p>
                </div>
              </div>

              <Separator />

              <div className="flex items-center gap-3">
                <MapPin className="w-5 h-5 text-muted-foreground" />
                <div>
                  <p className="font-medium">{library.address}</p>
                  <p className="text-sm text-muted-foreground">주소</p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* 운영시간 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Clock className="w-5 h-5" />
                운영시간
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div className="text-center">
                <p className="text-2xl font-bold text-primary">{library.openingHours}</p>
                <p className="text-sm text-muted-foreground mt-1">운영시간</p>
              </div>
            </CardContent>
          </Card>

          {/* 위치 정보 */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <MapPin className="w-5 h-5" />
                위치 좌표
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-3">
              <div className="flex justify-between">
                <span className="text-muted-foreground">위도</span>
                <span className="font-medium">{library.latitude}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-muted-foreground">경도</span>
                <span className="font-medium">{library.longitude}</span>
              </div>
            </CardContent>
          </Card>

          {/* 액션 버튼 */}
          <div className="space-y-3">
            <Button className="w-full" size="lg">
              <Building className="w-4 h-4 mr-2" />
              도서 검색하기
            </Button>
            <Button variant="outline" className="w-full bg-transparent" onClick={handleDirections}>
              <Navigation className="w-4 h-4 mr-2" />
              길찾기
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}
