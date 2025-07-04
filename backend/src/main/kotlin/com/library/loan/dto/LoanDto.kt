package com.library.loan.dto

import com.library.loan.entity.Loan
import com.library.loan.entity.LoanStatus
import java.time.LocalDateTime

// 대출 생성 요청 DTO
data class LoanCreateRequestDto(
    val userId: Long,
    val bookId: Long,
    val loanPeriodDays: Int = 14 // 기본 대출 기간 14일
)

// 대출 응답 DTO
data class LoanResponseDto(
    val id: Long,
    val userId: Long,
    val userName: String,
    val bookId: Long,
    val bookTitle: String,
    val bookAuthor: String,
    val libraryName: String,
    val loanDate: LocalDateTime,
    val dueDate: LocalDateTime,
    val returnDate: LocalDateTime?,
    val status: LoanStatus,
    val isOverdue: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(loan: Loan): LoanResponseDto {
            return LoanResponseDto(
                id = loan.id,
                userId = loan.user.id,
                userName = loan.user.fullName,
                bookId = loan.book.id,
                bookTitle = loan.book.title,
                bookAuthor = loan.book.author,
                libraryName = loan.book.library.name,
                loanDate = loan.loanDate,
                dueDate = loan.dueDate,
                returnDate = loan.returnDate,
                status = loan.status,
                isOverdue = loan.status == LoanStatus.ACTIVE && loan.dueDate.isBefore(LocalDateTime.now()),
                createdAt = loan.createdAt,
                updatedAt = loan.updatedAt
            )
        }
    }
}

// 대출 반납 요청 DTO
data class LoanReturnRequestDto(
    val loanId: Long,
    val returnDate: LocalDateTime = LocalDateTime.now()
)

// 대출 연장 요청 DTO
data class LoanExtendRequestDto(
    val loanId: Long,
    val extensionDays: Int = 7 // 기본 연장 기간 7일
)

// 대출 목록 조회 DTO
data class LoanListResponseDto(
    val loans: List<LoanResponseDto>,
    val totalCount: Int,
    val activeCount: Int,
    val overdueCount: Int
) 