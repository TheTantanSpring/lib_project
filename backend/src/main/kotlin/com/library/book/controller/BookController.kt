package com.library.book.controller

import com.library.book.dto.*
import com.library.book.entity.BookCategories
import com.library.book.entity.BookStatus
import com.library.book.service.BookService
import com.library.book.service.CategoryStatsDto
import com.library.common.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(
    private val bookService: BookService
) {

    // 모든 도서 조회
    @GetMapping
    fun getAllBooks(): ResponseEntity<ApiResponse<List<BookResponseDto>>> {
        val books = bookService.getAllBooks()
        return ResponseEntity.ok(
            ApiResponse.success(books, "도서 목록 조회 성공")
        )
    }

    // 도서 ID로 조회
    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: Long): ResponseEntity<ApiResponse<BookResponseDto>> {
        val book = bookService.getBookById(id)
        return if (book != null) {
            ResponseEntity.ok(
                ApiResponse.success(book, "도서 정보 조회 성공")
            )
        } else {
            ResponseEntity.notFound().build()
        }
    }

    // 도서관별 도서 조회
    @GetMapping("/library/{libraryId}")
    fun getBooksByLibrary(@PathVariable libraryId: Long): ResponseEntity<ApiResponse<List<BookResponseDto>>> {
        val books = bookService.getBooksByLibrary(libraryId)
        return ResponseEntity.ok(
            ApiResponse.success(books, "도서관별 도서 목록 조회 성공")
        )
    }

    // 대여 가능한 도서 조회
    @GetMapping("/available")
    fun getAvailableBooks(): ResponseEntity<ApiResponse<List<BookResponseDto>>> {
        val books = bookService.getAvailableBooks()
        return ResponseEntity.ok(
            ApiResponse.success(books, "대여 가능한 도서 목록 조회 성공")
        )
    }

    // 도서관별 대여 가능한 도서 조회
    @GetMapping("/available/library/{libraryId}")
    fun getAvailableBooksByLibrary(@PathVariable libraryId: Long): ResponseEntity<ApiResponse<List<BookResponseDto>>> {
        val books = bookService.getAvailableBooksByLibrary(libraryId)
        return ResponseEntity.ok(
            ApiResponse.success(books, "도서관별 대여 가능한 도서 목록 조회 성공")
        )
    }

    // 도서 검색
    @GetMapping("/search")
    fun searchBooks(
        @RequestParam(required = false) title: String?,
        @RequestParam(required = false) author: String?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) libraryId: Long?,
        @RequestParam(required = false) isbn: String?,
        @RequestParam(required = false) publisher: String?,
        @RequestParam(required = false) status: BookStatus?
    ): ResponseEntity<ApiResponse<List<BookResponseDto>>> {
        val searchRequest = BookSearchRequestDto(
            title = title,
            author = author,
            category = category,
            libraryId = libraryId,
            isbn = isbn,
            publisher = publisher,
            status = status
        )
        
        val books = bookService.searchBooks(searchRequest)
        return ResponseEntity.ok(
            ApiResponse.success(books, "도서 검색 성공")
        )
    }

    // 도서 등록
    @PostMapping
    fun createBook(@Valid @RequestBody createRequest: BookCreateRequestDto): ResponseEntity<ApiResponse<BookResponseDto>> {
        return try {
            val book = bookService.createBook(createRequest)
            ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(book, "도서 등록 성공")
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "도서 등록 실패")
            )
        }
    }

    // 도서 정보 수정
    @PutMapping("/{id}")
    fun updateBook(
        @PathVariable id: Long,
        @Valid @RequestBody updateRequest: BookUpdateRequestDto
    ): ResponseEntity<ApiResponse<BookResponseDto>> {
        return try {
            val book = bookService.updateBook(id, updateRequest)
            ResponseEntity.ok(
                ApiResponse.success(book, "도서 정보 수정 성공")
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "도서 정보 수정 실패")
            )
        }
    }

    // 도서 삭제
    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        return try {
            bookService.deleteBook(id)
            ResponseEntity.ok(
                ApiResponse.success(Unit, "도서 삭제 성공")
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "도서 삭제 실패")
            )
        }
    }

    // 도서 대여
    @PostMapping("/{id}/borrow")
    fun borrowBook(@PathVariable id: Long): ResponseEntity<ApiResponse<BookResponseDto>> {
        return try {
            val book = bookService.borrowBook(id)
            ResponseEntity.ok(
                ApiResponse.success(book, "도서 대여 성공")
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "도서 대여 실패")
            )
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "도서 대여 실패")
            )
        }
    }

    // 도서 반납
    @PostMapping("/{id}/return")
    fun returnBook(@PathVariable id: Long): ResponseEntity<ApiResponse<BookResponseDto>> {
        return try {
            val book = bookService.returnBook(id)
            ResponseEntity.ok(
                ApiResponse.success(book, "도서 반납 성공")
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "도서 반납 실패")
            )
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "도서 반납 실패")
            )
        }
    }

    // 도서 통계 - 도서관별 총 도서 수
    @GetMapping("/stats/library/{libraryId}/total")
    fun getTotalBooksCountByLibrary(@PathVariable libraryId: Long): ResponseEntity<ApiResponse<Long>> {
        val count = bookService.getTotalBooksCountByLibrary(libraryId)
        return ResponseEntity.ok(
            ApiResponse.success(count, "도서관별 총 도서 수 조회 성공")
        )
    }

    // 도서 통계 - 도서관별 대여 가능한 도서 수
    @GetMapping("/stats/library/{libraryId}/available")
    fun getAvailableBooksCountByLibrary(@PathVariable libraryId: Long): ResponseEntity<ApiResponse<Long>> {
        val count = bookService.getAvailableBooksCountByLibrary(libraryId)
        return ResponseEntity.ok(
            ApiResponse.success(count, "도서관별 대여 가능한 도서 수 조회 성공")
        )
    }

    // 인기 카테고리 조회 (TOP 5)
    @GetMapping("/categories/popular")
    fun getTopCategories(): ResponseEntity<ApiResponse<List<String>>> {
        val categories = bookService.getTopCategories()
        return ResponseEntity.ok(
            ApiResponse.success(categories, "인기 카테고리 조회 성공")
        )
    }
    
    // 확장 고려: 카테고리 관련 API 엔드포인트 추가
    
    /**
     * 모든 카테고리 조회 (표준 + 사용자 정의)
     * 확장 고려: 추후 별도 CategoryController로 분리 가능
     */
    @GetMapping("/categories")
    fun getAllCategories(): ResponseEntity<ApiResponse<List<CategoryInfoDto>>> {
        val categories = bookService.getAllCategories()
        return ResponseEntity.ok(
            ApiResponse.success(categories, "카테고리 목록 조회 성공")
        )
    }
    
    /**
     * 카테고리별 도서 통계
     * 확장 고려: 추후 캐싱 및 실시간 업데이트로 확장 가능
     */
    @GetMapping("/categories/statistics")
    fun getCategoryStatistics(): ResponseEntity<ApiResponse<List<CategoryStatsDto>>> {
        val statistics = bookService.getCategoryStatistics()
        return ResponseEntity.ok(
            ApiResponse.success(statistics, "카테고리별 통계 조회 성공")
        )
    }
    
    /**
     * 도서관별 카테고리 통계
     * 확장 고려: 추후 캐싱으로 확장 가능
     */
    @GetMapping("/categories/statistics/library/{libraryId}")
    fun getCategoryStatisticsByLibrary(@PathVariable libraryId: Long): ResponseEntity<ApiResponse<List<CategoryStatsDto>>> {
        val statistics = bookService.getCategoryStatisticsByLibrary(libraryId)
        return ResponseEntity.ok(
            ApiResponse.success(statistics, "도서관별 카테고리 통계 조회 성공")
        )
    }
    
    /**
     * 카테고리 제안
     * 확장 고려: 추후 AI 기반 제안 시스템으로 확장 가능
     */
    @GetMapping("/categories/suggest")
    fun suggestCategories(@RequestParam input: String): ResponseEntity<ApiResponse<List<String>>> {
        val suggestions = bookService.suggestCategories(input)
        return ResponseEntity.ok(
            ApiResponse.success(suggestions, "카테고리 제안 성공")
        )
    }
    
    /**
     * 표준 카테고리 목록 조회
     * 확장 고려: 추후 Enum 기반으로 변경 시 활용
     */
    @GetMapping("/categories/standard")
    fun getStandardCategories(): ResponseEntity<ApiResponse<List<String>>> {
        val categories = BookCategories.STANDARD_CATEGORIES
        return ResponseEntity.ok(
            ApiResponse.success(categories, "표준 카테고리 조회 성공")
        )
    }
} 