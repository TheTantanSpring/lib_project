package com.library.loan.repository

import com.library.loan.entity.Loan
import com.library.loan.entity.LoanStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LoanRepository : JpaRepository<Loan, Long> {
    
    // 사용자별 대출 목록 조회
    fun findByUserIdOrderByLoanDateDesc(userId: Long): List<Loan>
    
    // 도서별 대출 목록 조회
    fun findByBookIdOrderByLoanDateDesc(bookId: Long): List<Loan>
    
    // 상태별 대출 목록 조회
    fun findByStatusOrderByLoanDateDesc(status: LoanStatus): List<Loan>
    
    // 사용자의 활성 대출 조회
    fun findByUserIdAndStatus(userId: Long, status: LoanStatus): List<Loan>
    
    // 연체된 대출 조회
    @Query("SELECT l FROM Loan l WHERE l.status = 'ACTIVE' AND l.dueDate < CURRENT_TIMESTAMP")
    fun findOverdueLoans(): List<Loan>
    
    // 특정 도서의 활성 대출 존재 여부 확인
    fun existsByBookIdAndStatus(bookId: Long, status: LoanStatus): Boolean
    
    // 사용자의 특정 도서 대출 이력 조회
    fun findByUserIdAndBookIdOrderByLoanDateDesc(userId: Long, bookId: Long): List<Loan>
    
    // 도서의 특정 상태 대출 조회
    fun findByBookIdAndStatus(bookId: Long, status: LoanStatus): List<Loan>
} 