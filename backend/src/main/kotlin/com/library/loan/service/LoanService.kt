package com.library.loan.service

import com.library.loan.dto.*
import com.library.loan.entity.Loan
import com.library.loan.entity.LoanStatus
import com.library.loan.repository.LoanRepository
import com.library.book.repository.BookRepository
import com.library.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class LoanService(
    private val loanRepository: LoanRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) {

    // 대출 생성
    fun createLoan(request: LoanCreateRequestDto): LoanResponseDto {
        val user = userRepository.findById(request.userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다: ${request.userId}") }
        
        val book = bookRepository.findById(request.bookId)
            .orElseThrow { IllegalArgumentException("도서를 찾을 수 없습니다: ${request.bookId}") }

        // 도서 대출 가능 여부 확인
        if (book.availableCopies <= 0) {
            throw IllegalStateException("대출 가능한 도서가 없습니다")
        }

        // 이미 대출 중인지 확인
        if (loanRepository.existsByBookIdAndStatus(request.bookId, LoanStatus.ACTIVE)) {
            val activeLoansCount = loanRepository.findByBookIdAndStatus(request.bookId, LoanStatus.ACTIVE).size
            if (activeLoansCount >= book.totalCopies) {
                throw IllegalStateException("모든 도서가 대출 중입니다")
            }
        }

        val dueDate = LocalDateTime.now().plusDays(request.loanPeriodDays.toLong())
        
        val loan = Loan(
            user = user,
            book = book,
            dueDate = dueDate
        )

        val savedLoan = loanRepository.save(loan)
        
        // 도서 대출 가능 수량 감소
        updateBookAvailableCopies(book.id, -1)
        
        return LoanResponseDto.from(savedLoan)
    }

    // 대출 반납
    fun returnLoan(request: LoanReturnRequestDto): LoanResponseDto {
        val loan = loanRepository.findById(request.loanId)
            .orElseThrow { IllegalArgumentException("대출 정보를 찾을 수 없습니다: ${request.loanId}") }

        if (loan.status != LoanStatus.ACTIVE) {
            throw IllegalStateException("이미 반납된 도서입니다")
        }

        val updatedLoan = loan.copy(
            status = LoanStatus.RETURNED,
            returnDate = request.returnDate,
            updatedAt = LocalDateTime.now()
        )

        val savedLoan = loanRepository.save(updatedLoan)
        
        // 도서 대출 가능 수량 증가
        updateBookAvailableCopies(loan.book.id, 1)
        
        return LoanResponseDto.from(savedLoan)
    }

    // 대출 연장
    fun extendLoan(request: LoanExtendRequestDto): LoanResponseDto {
        val loan = loanRepository.findById(request.loanId)
            .orElseThrow { IllegalArgumentException("대출 정보를 찾을 수 없습니다: ${request.loanId}") }

        if (loan.status != LoanStatus.ACTIVE) {
            throw IllegalStateException("활성 상태의 대출만 연장 가능합니다")
        }

        val newDueDate = loan.dueDate.plusDays(request.extensionDays.toLong())
        
        val updatedLoan = loan.copy(
            dueDate = newDueDate,
            updatedAt = LocalDateTime.now()
        )

        val savedLoan = loanRepository.save(updatedLoan)
        return LoanResponseDto.from(savedLoan)
    }

    // 대출 목록 조회 (전체)
    @Transactional(readOnly = true)
    fun getAllLoans(): LoanListResponseDto {
        val loans = loanRepository.findAll()
        val loanResponses = loans.map { LoanResponseDto.from(it) }
        
        return LoanListResponseDto(
            loans = loanResponses,
            totalCount = loans.size,
            activeCount = loans.count { it.status == LoanStatus.ACTIVE },
            overdueCount = loans.count { 
                it.status == LoanStatus.ACTIVE && it.dueDate.isBefore(LocalDateTime.now()) 
            }
        )
    }

    // 사용자별 대출 목록 조회
    @Transactional(readOnly = true)
    fun getLoansByUser(userId: Long): LoanListResponseDto {
        val loans = loanRepository.findByUserIdOrderByLoanDateDesc(userId)
        val loanResponses = loans.map { LoanResponseDto.from(it) }
        
        return LoanListResponseDto(
            loans = loanResponses,
            totalCount = loans.size,
            activeCount = loans.count { it.status == LoanStatus.ACTIVE },
            overdueCount = loans.count { 
                it.status == LoanStatus.ACTIVE && it.dueDate.isBefore(LocalDateTime.now()) 
            }
        )
    }

    // 연체 대출 조회
    @Transactional(readOnly = true)
    fun getOverdueLoans(): List<LoanResponseDto> {
        val overdueLoans = loanRepository.findOverdueLoans()
        return overdueLoans.map { LoanResponseDto.from(it) }
    }

    // 대출 상세 조회
    @Transactional(readOnly = true)
    fun getLoanById(loanId: Long): LoanResponseDto {
        val loan = loanRepository.findById(loanId)
            .orElseThrow { IllegalArgumentException("대출 정보를 찾을 수 없습니다: $loanId") }
        
        return LoanResponseDto.from(loan)
    }

    // 도서 대출 가능 수량 업데이트 (private helper method)
    private fun updateBookAvailableCopies(bookId: Long, change: Int) {
        val book = bookRepository.findById(bookId)
            .orElseThrow { IllegalArgumentException("도서를 찾을 수 없습니다: $bookId") }
        
        val newAvailableCopies = book.availableCopies + change
        if (newAvailableCopies < 0) {
            throw IllegalStateException("대출 가능한 도서 수량이 부족합니다")
        }
        
        val updatedBook = book.copy(
            availableCopies = newAvailableCopies,
            updatedAt = LocalDateTime.now()
        )
        
        bookRepository.save(updatedBook)
    }
} 