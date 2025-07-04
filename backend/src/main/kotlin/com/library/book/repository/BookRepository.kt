package com.library.book.repository

import com.library.book.entity.Book
import com.library.book.entity.BookStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : JpaRepository<Book, Long> {
    
    // 도서관별 도서 조회
    fun findByLibraryId(libraryId: Long): List<Book>
    
    // 도서관별 도서 조회 (페이징)
    fun findByLibraryId(libraryId: Long, pageable: Pageable): Page<Book>
    
    // 제목으로 검색
    fun findByTitleContainingIgnoreCase(title: String): List<Book>
    
    // 저자로 검색
    fun findByAuthorContainingIgnoreCase(author: String): List<Book>
    
    // 카테고리로 검색 (확장 고려: 대소문자 구분 없음)
    fun findByCategoryIgnoreCase(category: String): List<Book>
    
    // 카테고리 포함 검색 (확장 고려: 부분 일치 지원)
    fun findByCategoryContainingIgnoreCase(category: String): List<Book>
    
    // 여러 카테고리로 검색 (확장 고려: 하위 카테고리 검색 지원)
    fun findByCategoryIn(categories: List<String>): List<Book>
    
    // ISBN으로 검색
    fun findByIsbn(isbn: String): Book?
    
    // 상태별 검색
    fun findByStatus(status: BookStatus): List<Book>
    
    // 복합 검색 (도서관 + 제목)
    fun findByLibraryIdAndTitleContainingIgnoreCase(libraryId: Long, title: String): List<Book>
    
    // 복합 검색 (도서관 + 저자)
    fun findByLibraryIdAndAuthorContainingIgnoreCase(libraryId: Long, author: String): List<Book>
    
    // 복합 검색 (도서관 + 카테고리)
    fun findByLibraryIdAndCategory(libraryId: Long, category: String): List<Book>
    
    // 대여 가능한 도서 조회
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0 AND b.status = 'AVAILABLE'")
    fun findAvailableBooks(): List<Book>
    
    // 도서관별 대여 가능한 도서 조회
    @Query("SELECT b FROM Book b WHERE b.library.id = :libraryId AND b.availableCopies > 0 AND b.status = 'AVAILABLE'")
    fun findAvailableBooksByLibrary(@Param("libraryId") libraryId: Long): List<Book>
    
    // 도서 통계 - 도서관별 총 도서 수
    @Query("SELECT COUNT(b) FROM Book b WHERE b.library.id = :libraryId")
    fun countByLibraryId(@Param("libraryId") libraryId: Long): Long
    
    // 도서 통계 - 도서관별 대여 가능한 도서 수
    @Query("SELECT COUNT(b) FROM Book b WHERE b.library.id = :libraryId AND b.availableCopies > 0 AND b.status = 'AVAILABLE'")
    fun countAvailableBooksByLibrary(@Param("libraryId") libraryId: Long): Long
    
    // 확장 고려: 카테고리별 도서 통계 (추후 실시간 통계로 확장 가능)
    @Query("SELECT b.category, COUNT(b) FROM Book b WHERE b.category IS NOT NULL GROUP BY b.category ORDER BY COUNT(b) DESC")
    fun getCategoryStatistics(): List<Array<Any>>
    
    // 확장 고려: 도서관별 카테고리 통계 (추후 캐싱으로 확장 가능)
    @Query("SELECT b.category, COUNT(b) FROM Book b WHERE b.library.id = :libraryId AND b.category IS NOT NULL GROUP BY b.category ORDER BY COUNT(b) DESC")
    fun getCategoryStatisticsByLibrary(@Param("libraryId") libraryId: Long): List<Array<Any>>
    
    // 확장 고려: 모든 고유 카테고리 조회 (추후 별도 테이블로 분리 가능)
    @Query("SELECT DISTINCT b.category FROM Book b WHERE b.category IS NOT NULL ORDER BY b.category")
    fun findAllDistinctCategories(): List<String>
    
    // 동적 검색 쿼리
    @Query("""
        SELECT b FROM Book b 
        WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
        AND (:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')))
        AND (:category IS NULL OR b.category = :category)
        AND (:libraryId IS NULL OR b.library.id = :libraryId)
        AND (:isbn IS NULL OR b.isbn = :isbn)
        AND (:publisher IS NULL OR LOWER(b.publisher) LIKE LOWER(CONCAT('%', :publisher, '%')))
        AND (:status IS NULL OR b.status = :status)
        ORDER BY b.createdAt DESC
    """)
    fun searchBooks(
        @Param("title") title: String?,
        @Param("author") author: String?,
        @Param("category") category: String?,
        @Param("libraryId") libraryId: Long?,
        @Param("isbn") isbn: String?,
        @Param("publisher") publisher: String?,
        @Param("status") status: BookStatus?
    ): List<Book>
} 