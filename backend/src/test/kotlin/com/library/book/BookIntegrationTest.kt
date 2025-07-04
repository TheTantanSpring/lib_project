package com.library.book

import com.fasterxml.jackson.databind.ObjectMapper
import com.library.book.dto.BookCreateRequestDto
import com.library.book.dto.BookUpdateRequestDto
import com.library.book.entity.BookStatus
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@DisplayName("도서 도메인 통합 테스트")
class BookIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("모든 도서 조회 API 테스트")
    fun getAllBooks() {
        // when & then
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("도서 목록 조회 성공"))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").isNumber)
            // 첫 번째 도서 정보 검증
            .andExpect(jsonPath("$.data[0].title").value("스프링 부트 실전 활용"))
            .andExpect(jsonPath("$.data[0].author").value("김철수"))
            .andExpect(jsonPath("$.data[0].category").value("컴퓨터/IT"))
            .andExpect(jsonPath("$.data[0].libraryName").exists())
    }

    @Test
    @DisplayName("도서 ID로 조회 API 테스트")
    fun getBookById() {
        // when & then - 존재하는 도서 조회
        mockMvc.perform(get("/api/books/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("도서 정보 조회 성공"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("스프링 부트 실전 활용"))
            .andExpect(jsonPath("$.data.author").value("김철수"))
            .andExpect(jsonPath("$.data.category").value("컴퓨터/IT"))
            .andExpect(jsonPath("$.data.totalCopies").value(3))
            .andExpect(jsonPath("$.data.availableCopies").value(3))
            .andExpect(jsonPath("$.data.status").value("AVAILABLE"))
    }

    @Test
    @DisplayName("존재하지 않는 도서 조회 API 테스트")
    fun getBookByIdNotFound() {
        // when & then
        mockMvc.perform(get("/api/books/9999"))
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("도서관별 도서 조회 API 테스트")
    fun getBooksByLibrary() {
        // when & then - 도서관 ID 1의 도서들 조회
        mockMvc.perform(get("/api/books/library/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("도서관별 도서 목록 조회 성공"))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(3)) // 샘플 데이터 기준
            // 모든 도서가 같은 도서관 소속인지 확인
            .andExpect(jsonPath("$.data[*].libraryId").value(everyItem(equalTo(1))))
    }

    @Test
    @DisplayName("대여 가능한 도서 조회 API 테스트")
    fun getAvailableBooks() {
        // when & then
        mockMvc.perform(get("/api/books/available"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("대여 가능한 도서 목록 조회 성공"))
            .andExpect(jsonPath("$.data").isArray)
            // 모든 도서가 대여 가능 상태인지 확인
            .andExpect(jsonPath("$.data[*].status").value(everyItem(equalTo("AVAILABLE"))))
            .andExpect(jsonPath("$.data[*].availableCopies").value(everyItem(greaterThan(0))))
    }

    @Test
    @DisplayName("도서관별 대여 가능한 도서 조회 API 테스트")
    fun getAvailableBooksByLibrary() {
        // when & then
        mockMvc.perform(get("/api/books/available/library/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            // 특정 도서관의 대여 가능한 도서들
            .andExpect(jsonPath("$.data[*].libraryId").value(everyItem(equalTo(1))))
            .andExpect(jsonPath("$.data[*].status").value(everyItem(equalTo("AVAILABLE"))))
    }

    @Test
    @DisplayName("도서 제목으로 검색 API 테스트")
    fun searchBooksByTitle() {
        // when & then
        mockMvc.perform(get("/api/books/search")
            .param("title", "스프링"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("도서 검색 성공"))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].title").value("스프링 부트 실전 활용"))
    }

    @Test
    @DisplayName("도서 저자로 검색 API 테스트")
    fun searchBooksByAuthor() {
        // when & then
        mockMvc.perform(get("/api/books/search")
            .param("author", "김철수"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isArray)
            // 김철수가 저자인 모든 도서 (스프링 부트, 파이썬 기초, 클라우드 네이티브)
            .andExpect(jsonPath("$.data[*].author").value(everyItem(equalTo("김철수"))))
    }

    @Test
    @DisplayName("도서 카테고리로 검색 API 테스트")
    fun searchBooksByCategory() {
        // when & then
        mockMvc.perform(get("/api/books/search")
            .param("category", "컴퓨터/IT"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isArray)
            // 모든 샘플 도서가 컴퓨터/IT 카테고리
            .andExpect(jsonPath("$.data[*].category").value(everyItem(equalTo("컴퓨터/IT"))))
    }

    @Test
    @DisplayName("복합 검색 조건 API 테스트")
    fun searchBooksWithMultipleParams() {
        // when & then
        mockMvc.perform(get("/api/books/search")
            .param("author", "김철수")
            .param("category", "컴퓨터/IT")
            .param("libraryId", "1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isArray)
            // 조건에 맞는 도서만 반환
            .andExpect(jsonPath("$.data[*].author").value(everyItem(equalTo("김철수"))))
            .andExpect(jsonPath("$.data[*].category").value(everyItem(equalTo("컴퓨터/IT"))))
            .andExpect(jsonPath("$.data[*].libraryId").value(everyItem(equalTo(1))))
    }

    @Test
    @DisplayName("도서 등록 API 테스트")
    fun createBook() {
        // given
        val createRequest = BookCreateRequestDto(
            libraryId = 1L,
            title = "테스트 도서",
            author = "테스트 저자",
            isbn = "978-1234567890",
            publisher = "테스트 출판사",
            publicationYear = 2023,
            category = "컴퓨터/IT",
            totalCopies = 2,
            availableCopies = 2,
            status = BookStatus.AVAILABLE
        )

        // when & then
        mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("도서 등록 성공"))
            .andExpect(jsonPath("$.data.title").value("테스트 도서"))
            .andExpect(jsonPath("$.data.author").value("테스트 저자"))
            .andExpect(jsonPath("$.data.category").value("컴퓨터/IT"))
            .andExpect(jsonPath("$.data.totalCopies").value(2))
    }

    @Test
    @DisplayName("잘못된 데이터로 도서 등록 API 테스트")
    fun createBookWithInvalidData() {
        // given - 필수 필드 누락
        val createRequest = BookCreateRequestDto(
            libraryId = 1L,
            title = "", // 빈 제목
            author = "테스트 저자",
            totalCopies = 0, // 잘못된 권수
            availableCopies = 1
        )

        // when & then
        mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("존재하지 않는 도서관에 도서 등록 API 테스트")
    fun createBookWithInvalidLibrary() {
        // given
        val createRequest = BookCreateRequestDto(
            libraryId = 9999L, // 존재하지 않는 도서관
            title = "테스트 도서",
            author = "테스트 저자"
        )

        // when & then
        mockMvc.perform(post("/api/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(containsString("도서관을 찾을 수 없습니다")))
    }

    @Test
    @DisplayName("도서 정보 수정 API 테스트")
    fun updateBook() {
        // given
        val updateRequest = BookUpdateRequestDto(
            title = "수정된 제목",
            category = "문학",
            totalCopies = 5
        )

        // when & then
        mockMvc.perform(put("/api/books/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("도서 정보 수정 성공"))
            .andExpect(jsonPath("$.data.title").value("수정된 제목"))
            .andExpect(jsonPath("$.data.category").value("문학"))
            .andExpect(jsonPath("$.data.totalCopies").value(5))
    }

    @Test
    @DisplayName("도서 삭제 API 테스트")
    fun deleteBook() {
        // when & then
        mockMvc.perform(delete("/api/books/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("도서 삭제 성공"))

        // 삭제 후 조회 시 404 반환 확인
        mockMvc.perform(get("/api/books/1"))
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("도서 대여 API 테스트")
    fun borrowBook() {
        // when & then
        mockMvc.perform(post("/api/books/1/borrow"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("도서 대여 성공"))
            .andExpect(jsonPath("$.data.availableCopies").value(2)) // 3에서 2로 감소
            
        // 대여 후 상태 확인
        mockMvc.perform(get("/api/books/1"))
            .andExpect(jsonPath("$.data.availableCopies").value(2))
    }

    @Test
    @DisplayName("대여 불가능한 도서 대여 시도 API 테스트")
    fun borrowUnavailableBook() {
        // given - 먼저 모든 재고를 대여 처리
        repeat(3) {
            mockMvc.perform(post("/api/books/1/borrow"))
        }

        // when & then - 재고가 없는 상태에서 대여 시도
        mockMvc.perform(post("/api/books/1/borrow"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value(containsString("대여 가능한 도서가 없습니다")))
    }

    @Test
    @DisplayName("도서 반납 API 테스트")
    fun returnBook() {
        // given - 먼저 도서 대여
        mockMvc.perform(post("/api/books/1/borrow"))

        // when & then - 도서 반납
        mockMvc.perform(post("/api/books/1/return"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("도서 반납 성공"))
            .andExpect(jsonPath("$.data.availableCopies").value(3)) // 원래 수량으로 복구
    }

    @Test
    @DisplayName("카테고리별 통계 API 테스트")
    fun getCategoryStatistics() {
        // when & then
        mockMvc.perform(get("/api/books/categories/statistics"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("카테고리별 통계 조회 성공"))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data[0].categoryInfo.name").exists())
            .andExpect(jsonPath("$.data[0].bookCount").isNumber)
    }

    @Test
    @DisplayName("도서관별 카테고리 통계 API 테스트")
    fun getCategoryStatisticsByLibrary() {
        // when & then
        mockMvc.perform(get("/api/books/categories/statistics/library/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isArray)
            // 특정 도서관의 카테고리 통계
    }

    @Test
    @DisplayName("인기 카테고리 조회 API 테스트")
    fun getTopCategories() {
        // when & then
        mockMvc.perform(get("/api/books/categories/popular"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(lessThanOrEqualTo(5))) // TOP 5
    }

    @Test
    @DisplayName("표준 카테고리 조회 API 테스트")
    fun getStandardCategories() {
        // when & then
        mockMvc.perform(get("/api/books/categories/standard"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(12)) // 표준 카테고리 12개
            .andExpect(jsonPath("$.data").value(hasItem("컴퓨터/IT")))
            .andExpect(jsonPath("$.data").value(hasItem("문학")))
    }

    @Test
    @DisplayName("카테고리 제안 API 테스트")
    fun suggestCategories() {
        // when & then
        mockMvc.perform(get("/api/books/categories/suggest")
            .param("input", "컴퓨"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data").value(hasItem("컴퓨터/IT")))
    }

    @Test
    @DisplayName("모든 카테고리 조회 API 테스트")
    fun getAllCategories() {
        // when & then
        mockMvc.perform(get("/api/books/categories"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data[0].name").exists())
            .andExpect(jsonPath("$.data[0].description").exists())
            .andExpect(jsonPath("$.data[0].isStandard").isBoolean)
    }

    @Test
    @DisplayName("도서관별 총 도서 수 통계 API 테스트")
    fun getTotalBooksCountByLibrary() {
        // when & then
        mockMvc.perform(get("/api/books/stats/library/1/total"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isNumber)
            .andExpect(jsonPath("$.data").value(greaterThan(0)))
    }

    @Test
    @DisplayName("도서관별 대여 가능한 도서 수 통계 API 테스트")
    fun getAvailableBooksCountByLibrary() {
        // when & then
        mockMvc.perform(get("/api/books/stats/library/1/available"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data").isNumber)
            .andExpect(jsonPath("$.data").value(greaterThanOrEqualTo(0)))
    }

    @Test
    @DisplayName("API 응답 구조 검증 테스트")
    fun validateApiResponseStructure() {
        // when & then
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").exists())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.timestamp").exists())
            // 도서 객체 필드 검증
            .andExpect(jsonPath("$.data[0].id").exists())
            .andExpect(jsonPath("$.data[0].title").exists())
            .andExpect(jsonPath("$.data[0].author").exists())
            .andExpect(jsonPath("$.data[0].libraryId").exists())
            .andExpect(jsonPath("$.data[0].libraryName").exists())
            .andExpect(jsonPath("$.data[0].category").exists())
            .andExpect(jsonPath("$.data[0].totalCopies").exists())
            .andExpect(jsonPath("$.data[0].availableCopies").exists())
            .andExpect(jsonPath("$.data[0].status").exists())
    }

    @Test
    @DisplayName("데이터 유효성 검증 테스트")
    fun validateDataIntegrity() {
        // when & then
        mockMvc.perform(get("/api/books/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.totalCopies").value(greaterThan(0)))
            .andExpect(jsonPath("$.data.availableCopies").value(greaterThanOrEqualTo(0)))
    }

    @Test
    @DisplayName("성능 테스트 - 대량 도서 조회")
    fun performanceTestGetAllBooks() {
        val startTime = System.currentTimeMillis()
        
        // when
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk)
        
        val endTime = System.currentTimeMillis()
        val responseTime = endTime - startTime
        
        // then - 응답 시간이 2초 이내
        assert(responseTime < 2000) { "응답 시간이 2초를 초과했습니다: ${responseTime}ms" }
    }

    @Test
    @DisplayName("동시성 테스트 - 동시 대여 시도")
    fun concurrencyTestBorrowBook() {
        // given
        val threads = mutableListOf<Thread>()
        val results = mutableListOf<Int>() // HTTP 상태 코드 저장

        // when - 5개의 스레드로 동시 대여 시도 (총 권수는 3권)
        repeat(5) { index ->
            val thread = Thread {
                try {
                    val result = mockMvc.perform(post("/api/books/1/borrow"))
                        .andReturn()
                    results.add(result.response.status)
                } catch (e: Exception) {
                    results.add(500) // 에러 발생 시
                }
            }
            threads.add(thread)
            thread.start()
        }

        // 모든 스레드 종료 대기
        threads.forEach { it.join() }

        // then - 3번은 성공(200), 2번은 실패(400)해야 함
        val successCount = results.count { it == 200 }
        val failCount = results.count { it == 400 }
        
        assert(successCount == 3) { "성공한 대여 수가 3이 아닙니다: $successCount" }
        assert(failCount == 2) { "실패한 대여 수가 2가 아닙니다: $failCount" }
    }
} 