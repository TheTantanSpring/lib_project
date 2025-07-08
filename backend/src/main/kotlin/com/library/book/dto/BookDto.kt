package com.library.book.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.library.book.entity.Book
import com.library.book.entity.BookCategories
import com.library.book.entity.BookStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

// 도서 응답 DTO
data class BookResponseDto(
    val id: Long,
    val libraryId: Long,
    val libraryName: String,
    val title: String,
    val author: String,
    val isbn: String?,
    val publisher: String?,
    val publicationYear: Int?,
    val category: String?,
    val totalCopies: Int,
    val availableCopies: Int,
    val status: BookStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    // 확장 고려: 카테고리 메타데이터 (추후 별도 테이블로 분리 시 활용)
    val categoryInfo: CategoryInfoDto? = null
) {
    companion object {
        fun from(book: Book): BookResponseDto {
            return BookResponseDto(
                id = book.id,
                libraryId = book.library.id,
                libraryName = book.library.name,
                title = book.title,
                author = book.author,
                isbn = book.isbn,
                publisher = book.publisher,
                publicationYear = book.publicationYear,
                category = book.category,
                totalCopies = book.totalCopies,
                availableCopies = book.availableCopies,
                status = book.status,
                createdAt = book.createdAt,
                updatedAt = book.updatedAt,
                // 확장 고려: 카테고리 정보 자동 매핑
                categoryInfo = book.category?.let { 
                    CategoryInfoDto.from(BookCategories.getCategoryInfo(it))
                }
            )
        }
    }
}

// 카테고리 정보 DTO (확장 고려: 추후 별도 API로 분리 가능)
data class CategoryInfoDto(
    val name: String,
    val description: String?,
    val isStandard: Boolean,
    val subCategories: List<String> = emptyList()
) {
    companion object {
        fun from(categoryStats: BookCategories.CategoryStats): CategoryInfoDto {
            return CategoryInfoDto(
                name = categoryStats.name,
                description = categoryStats.description,
                isStandard = categoryStats.isStandard,
                subCategories = categoryStats.subCategories
            )
        }
    }
}

// 도서 생성 요청 DTO
data class BookCreateRequestDto(
    @field:NotNull(message = "도서관 ID는 필수입니다")
    val libraryId: Long,

    @field:NotBlank(message = "도서 제목은 필수입니다")
    val title: String,

    @field:NotBlank(message = "저자는 필수입니다")
    val author: String,

    val isbn: String? = null,
    val publisher: String? = null,
    val publicationYear: Int? = null,
    
    // 확장 고려: 카테고리 검증 로직 추가 (추후 @Valid 어노테이션으로 확장 가능)
    val category: String? = null,

    @field:Positive(message = "총 권수는 1 이상이어야 합니다")
    val totalCopies: Int = 1,

    @field:Positive(message = "이용 가능한 권수는 1 이상이어야 합니다")
    val availableCopies: Int = 1,

    val status: BookStatus = BookStatus.AVAILABLE
) {
    // 확장 고려: 카테고리 정규화 메서드 (추후 더 복잡한 로직으로 확장 가능)
    @JsonIgnore
    fun getNormalizedCategory(): String? {
        return category?.let { BookCategories.normalizeCategory(it) }
    }
    
    // 확장 고려: 카테고리 유효성 검증 (추후 DB 기반 검증으로 확장 가능)
    @JsonIgnore
    fun isValidCategory(): Boolean {
        return BookCategories.isValidCategory(category)
    }
    
    // 확장 고려: 카테고리 제안 기능 (추후 AI 기반으로 확장 가능)
    @JsonIgnore
    fun getCategorySuggestions(): List<String> {
        return if (category.isNullOrBlank()) {
            emptyList()
        } else {
            BookCategories.suggestCategory(category)
        }
    }
}

// 도서 수정 요청 DTO
data class BookUpdateRequestDto(
    val title: String? = null,
    val author: String? = null,
    val isbn: String? = null,
    val publisher: String? = null,
    val publicationYear: Int? = null,
    val category: String? = null,
    val totalCopies: Int? = null,
    val availableCopies: Int? = null,
    val status: BookStatus? = null
) {
    // 확장 고려: 카테고리 정규화 (추후 더 복잡한 로직으로 확장 가능)
    @JsonIgnore
    fun getNormalizedCategory(): String? {
        return category?.let { BookCategories.normalizeCategory(it) }
    }
    
    // 확장 고려: 카테고리 유효성 검증 (추후 DB 기반으로 확장 가능)
    @JsonIgnore
    fun isValidCategory(): Boolean {
        return category == null || BookCategories.isValidCategory(category)
    }
}

// 도서 검색 요청 DTO
data class BookSearchRequestDto(
    val title: String? = null,
    val author: String? = null,
    val category: String? = null,
    val libraryId: Long? = null,
    val isbn: String? = null,
    val publisher: String? = null,
    val status: BookStatus? = null,
    // 확장 고려: 카테고리 검색 옵션 (추후 전체 텍스트 검색으로 확장 가능)
    val includeSubCategories: Boolean = false
) {
    // 확장 고려: 검색 조건 정규화 (추후 복잡한 검색 로직으로 확장 가능)
    @JsonIgnore
    fun getNormalizedCategory(): String? {
        return category?.let { BookCategories.normalizeCategory(it) }
    }
    
    // 확장 고려: 카테고리 검색 확장 (하위 카테고리 포함)
    @JsonIgnore
    fun getExpandedCategories(): List<String> {
        return if (category.isNullOrBlank()) {
            emptyList()
        } else {
            val categories = mutableListOf(category)
            if (includeSubCategories) {
                categories.addAll(BookCategories.getSubCategories(category))
            }
            categories
        }
    }
    
    // 확장 고려: 빈 검색 조건 체크 (추후 복잡한 검증으로 확장 가능)
    @JsonIgnore
    fun hasSearchCriteria(): Boolean {
        return !title.isNullOrBlank() ||
                !author.isNullOrBlank() ||
                !category.isNullOrBlank() ||
                libraryId != null ||
                !isbn.isNullOrBlank() ||
                !publisher.isNullOrBlank() ||
                status != null
    }
} 