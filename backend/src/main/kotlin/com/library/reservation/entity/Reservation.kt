package com.library.reservation.entity

import com.library.user.entity.User
import com.library.book.entity.Book
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "reservations")
data class Reservation(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    val book: Book,

    @Column(name = "reservation_date", nullable = false)
    val reservationDate: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    val status: ReservationStatus = ReservationStatus.PENDING,

    @Column(name = "expiry_date")
    val expiryDate: LocalDateTime? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class ReservationStatus {
    PENDING,    // 예약 대기
    READY,      // 대출 가능
    COMPLETED,  // 예약 완료 (대출됨)
    CANCELLED,  // 예약 취소
    EXPIRED     // 예약 만료
} 