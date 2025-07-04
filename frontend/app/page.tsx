import Link from "next/link"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { MapPin, Book, Search, Users } from "lucide-react"

export default function HomePage() {
  return (
    <div className="container mx-auto px-4 py-8">
      {/* 헤더 */}
      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold mb-4">도서관 시스템</h1>
        <p className="text-xl text-muted-foreground mb-8">가까운 도서관을 찾고 도서를 대출해보세요</p>
        <Button asChild size="lg">
          <Link href="/libraries">
            <MapPin className="w-5 h-5 mr-2" />
            도서관 찾기
          </Link>
        </Button>
      </div>

      {/* 기능 소개 */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-12">
        <Card>
          <CardHeader className="text-center">
            <MapPin className="w-12 h-12 mx-auto mb-2 text-primary" />
            <CardTitle>도서관 찾기</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-center text-muted-foreground">지도에서 가까운 도서관을 찾아보세요</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="text-center">
            <Search className="w-12 h-12 mx-auto mb-2 text-primary" />
            <CardTitle>도서 검색</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-center text-muted-foreground">원하는 도서를 쉽게 검색할 수 있습니다</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="text-center">
            <Book className="w-12 h-12 mx-auto mb-2 text-primary" />
            <CardTitle>온라인 대출</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-center text-muted-foreground">온라인으로 도서를 대출하고 예약하세요</p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="text-center">
            <Users className="w-12 h-12 mx-auto mb-2 text-primary" />
            <CardTitle>회원 관리</CardTitle>
          </CardHeader>
          <CardContent>
            <p className="text-center text-muted-foreground">대출 현황과 이력을 관리할 수 있습니다</p>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
