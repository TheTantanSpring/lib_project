package com.library.book.service

import com.library.book.dto.*
import com.library.book.entity.Book
import com.library.book.entity.BookCategories
import com.library.book.entity.BookStatus
import com.library.book.repository.BookRepository
import com.library.library.repository.LibraryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class BookService(
    private val bookRepository: BookRepository,
    private val libraryRepository: LibraryRepository
) {

    // 모든 도서 조회
    @Transactional(readOnly = true)
    fun getAllBooks(): List<BookResponseDto> {
        return bookRepository.findAll().map { BookResponseDto.from(it) }
    }

    // 도서 ID로 조회
    @Transactional(readOnly = true)
    fun getBookById(id: Long): BookResponseDto? {
        return bookRepository.findById(id)
            .map { BookResponseDto.from(it) }
            .orElse(null)
    }

    // 도서관별 도서 조회
    @Transactional(readOnly = true)
    fun getBooksByLibrary(libraryId: Long): List<BookResponseDto> {
        return bookRepository.findByLibraryId(libraryId)
            .map { BookResponseDto.from(it) }
    }

    // 대여 가능한 도서 조회
    @Transactional(readOnly = true)
    fun getAvailableBooks(): List<BookResponseDto> {
        return bookRepository.findAvailableBooks()
            .map { BookResponseDto.from(it) }
    }

    // 도서관별 대여 가능한 도서 조회
    @Transactional(readOnly = true)
    fun getAvailableBooksByLibrary(libraryId: Long): List<BookResponseDto> {
        return bookRepository.findAvailableBooksByLibrary(libraryId)
            .map { BookResponseDto.from(it) }
    }

    // 도서 검색
    @Transactional(readOnly = true)
    fun searchBooks(searchRequest: BookSearchRequestDto): List<BookResponseDto> {
        return bookRepository.searchBooks(
            title = searchRequest.title,
            author = searchRequest.author,
            category = searchRequest.category,
            libraryId = searchRequest.libraryId,
            isbn = searchRequest.isbn,
            publisher = searchRequest.publisher,
            status = searchRequest.status
        ).map { BookResponseDto.from(it) }
    }

    // 도서 등록
    fun createBook(createRequest: BookCreateRequestDto): BookResponseDto {
        val library = libraryRepository.findById(createRequest.libraryId)
            .orElseThrow { IllegalArgumentException("해당 도서관을 찾을 수 없습니다: ${createRequest.libraryId}") }

        // 확장 고려: 카테고리 검증 및 정규화 (추후 복잡한 검증 로직으로 확장 가능)
        val normalizedCategory = validateAndNormalizeCategory(createRequest.getNormalizedCategory())

        val book = Book(
            library = library,
            title = createRequest.title,
            author = createRequest.author,
            isbn = createRequest.isbn,
            publisher = createRequest.publisher,
            publicationYear = createRequest.publicationYear,
            category = normalizedCategory,
            totalCopies = createRequest.totalCopies,
            availableCopies = createRequest.availableCopies,
            status = createRequest.status,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        val savedBook = bookRepository.save(book)
        return BookResponseDto.from(savedBook)
    }

    // 도서 정보 수정
    fun updateBook(id: Long, updateRequest: BookUpdateRequestDto): BookResponseDto {
        val book = bookRepository.findById(id)
            .orElseThrow { IllegalArgumentException("해당 도서를 찾을 수 없습니다: $id") }

        val updatedBook = book.copy(
            title = updateRequest.title ?: book.title,
            author = updateRequest.author ?: book.author,
            isbn = updateRequest.isbn ?: book.isbn,
            publisher = updateRequest.publisher ?: book.publisher,
            publicationYear = updateRequest.publicationYear ?: book.publicationYear,
            category = updateRequest.category ?: book.category,
            totalCopies = updateRequest.totalCopies ?: book.totalCopies,
            availableCopies = updateRequest.availableCopies ?: book.availableCopies,
            status = updateRequest.status ?: book.status,
            updatedAt = LocalDateTime.now()
        )

        val savedBook = bookRepository.save(updatedBook)
        return BookResponseDto.from(savedBook)
    }

    // 도서 삭제
    fun deleteBook(id: Long) {
        val book = bookRepository.findById(id)
            .orElseThrow { IllegalArgumentException("해당 도서를 찾을 수 없습니다: $id") }

        bookRepository.delete(book)
    }

    // 도서 대여 (이용 가능 권수 감소)
    fun borrowBook(id: Long): BookResponseDto {
        val book = bookRepository.findById(id)
            .orElseThrow { IllegalArgumentException("해당 도서를 찾을 수 없습니다: $id") }

        if (book.availableCopies <= 0) {
            throw IllegalStateException("대여 가능한 도서가 없습니다")
        }

        if (book.status != BookStatus.AVAILABLE) {
            throw IllegalStateException("현재 대여할 수 없는 도서입니다")
        }

        val updatedBook = book.copy(
            availableCopies = book.availableCopies - 1,
            updatedAt = LocalDateTime.now()
        )

        val savedBook = bookRepository.save(updatedBook)
        return BookResponseDto.from(savedBook)
    }

    // 도서 반납 (이용 가능 권수 증가)
    fun returnBook(id: Long): BookResponseDto {
        val book = bookRepository.findById(id)
            .orElseThrow { IllegalArgumentException("해당 도서를 찾을 수 없습니다: $id") }

        if (book.availableCopies >= book.totalCopies) {
            throw IllegalStateException("이미 모든 도서가 반납되었습니다")
        }

        val updatedBook = book.copy(
            availableCopies = book.availableCopies + 1,
            updatedAt = LocalDateTime.now()
        )

        val savedBook = bookRepository.save(updatedBook)
        return BookResponseDto.from(savedBook)
    }

    // 도서 통계 - 도서관별 총 도서 수
    @Transactional(readOnly = true)
    fun getTotalBooksCountByLibrary(libraryId: Long): Long {
        return bookRepository.countByLibraryId(libraryId)
    }

    // 도서 통계 - 도서관별 대여 가능한 도서 수
    @Transactional(readOnly = true)
    fun getAvailableBooksCountByLibrary(libraryId: Long): Long {
        return bookRepository.countAvailableBooksByLibrary(libraryId)
    }

    // 인기 카테고리 조회 (TOP 5) - 확장 고려: 캐싱 및 실시간 통계로 확장 가능
    @Transactional(readOnly = true)
    fun getTopCategories(): List<String> {
        return bookRepository.getCategoryStatistics()
            .take(5)
            .mapNotNull { it[0] as? String }
    }
    
    // 확장 고려: 카테고리 관련 비즈니스 로직 분리
    
    /**
     * 카테고리 검증 및 정규화
     * 확장 고려: 추후 DB 기반 검증, AI 기반 제안으로 확장 가능
     */
    private fun validateAndNormalizeCategory(category: String?): String? {
        return if (category.isNullOrBlank()) {
            null
        } else {
            val normalized = BookCategories.normalizeCategory(category)
            // 여기서 추가적인 검증 로직을 수행할 수 있음
            normalized
        }
    }
    
    /**
     * 모든 카테고리 조회
     * 확장 고려: 추후 별도 CategoryService로 분리 가능
     */
    @Transactional(readOnly = true)
    fun getAllCategories(): List<CategoryInfoDto> {
        val dbCategories = bookRepository.findAllDistinctCategories()
        val standardCategories = BookCategories.STANDARD_CATEGORIES
        
        // 표준 카테고리와 DB 카테고리 통합
        val allCategories = (standardCategories + dbCategories).distinct()
        
        return allCategories.map { category ->
            CategoryInfoDto.from(BookCategories.getCategoryInfo(category))
        }
    }
    
    /**
     * 카테고리별 도서 통계
     * 확장 고려: 추후 캐싱 및 실시간 통계로 확장 가능
     */
    @Transactional(readOnly = true)
    fun getCategoryStatistics(): List<CategoryStatsDto> {
        return bookRepository.getCategoryStatistics()
            .map { result ->
                val categoryName = result[0] as String
                val bookCount = result[1] as Long
                CategoryStatsDto(
                    categoryInfo = CategoryInfoDto.from(BookCategories.getCategoryInfo(categoryName)),
                    bookCount = bookCount
                )
            }
    }
    
    /**
     * 도서관별 카테고리 통계
     * 확장 고려: 추후 캐싱으로 확장 가능
     */
    @Transactional(readOnly = true)
    fun getCategoryStatisticsByLibrary(libraryId: Long): List<CategoryStatsDto> {
        return bookRepository.getCategoryStatisticsByLibrary(libraryId)
            .map { result ->
                val categoryName = result[0] as String
                val bookCount = result[1] as Long
                CategoryStatsDto(
                    categoryInfo = CategoryInfoDto.from(BookCategories.getCategoryInfo(categoryName)),
                    bookCount = bookCount
                )
            }
    }
    
    /**
     * 카테고리 제안
     * 확장 고려: 추후 AI 기반 제안 시스템으로 확장 가능
     */
    @Transactional(readOnly = true)
    fun suggestCategories(input: String): List<String> {
        return BookCategories.suggestCategory(input)
    }
}

// 카테고리 통계 DTO (확장 고려: 추후 별도 파일로 분리 가능)
data class CategoryStatsDto(
    val categoryInfo: CategoryInfoDto,
    val bookCount: Long
) 