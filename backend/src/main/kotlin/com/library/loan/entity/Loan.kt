package com.library.loan.entity

import com.library.user.entity.User
import com.library.book.entity.Book
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "loans")
data class Loan(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    val book: Book,

    @Column(name = "loan_date", nullable = false)
    val loanDate: LocalDateTime = LocalDateTime.now(),

    @Column(name = "due_date", nullable = false)
    val dueDate: LocalDateTime,

    @Enumerated(EnumType.STRING)
    val status: LoanStatus = LoanStatus.ACTIVE,

    @Column(name = "return_date")
    val returnDate: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class LoanStatus {
    ACTIVE,    // 대출 중
    RETURNED,  // 반납 완료
    OVERDUE    // 연체
} 