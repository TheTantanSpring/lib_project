package com.library.reservation.dto

import com.library.reservation.entity.Reservation
import com.library.reservation.entity.ReservationStatus
import java.time.LocalDateTime

// 예약 생성 요청 DTO
data class ReservationCreateRequestDto(
    val userId: Long,
    val bookId: Long
)

// 예약 응답 DTO
data class ReservationResponseDto(
    val id: Long,
    val userId: Long,
    val userName: String,
    val bookId: Long,
    val bookTitle: String,
    val bookAuthor: String,
    val libraryName: String,
    val reservationDate: LocalDateTime,
    val status: ReservationStatus,
    val queuePosition: Int?, // 대기 순번
    val expiryDate: LocalDateTime?,
    val isExpired: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(reservation: Reservation, queuePosition: Int? = null): ReservationResponseDto {
            return ReservationResponseDto(
                id = reservation.id,
                userId = reservation.user.id,
                userName = reservation.user.fullName,
                bookId = reservation.book.id,
                bookTitle = reservation.book.title,
                bookAuthor = reservation.book.author,
                libraryName = reservation.book.library.name,
                reservationDate = reservation.reservationDate,
                status = reservation.status,
                queuePosition = queuePosition,
                expiryDate = reservation.expiryDate,
                isExpired = reservation.expiryDate?.isBefore(LocalDateTime.now()) ?: false,
                createdAt = reservation.createdAt,
                updatedAt = reservation.updatedAt
            )
        }
    }
}

// 예약 취소 요청 DTO
data class ReservationCancelRequestDto(
    val reservationId: Long,
    val reason: String? = null
)

// 예약 목록 조회 DTO
data class ReservationListResponseDto(
    val reservations: List<ReservationResponseDto>,
    val totalCount: Int,
    val pendingCount: Int,
    val readyCount: Int,
    val expiredCount: Int
)

// 도서별 예약 현황 DTO
data class BookReservationStatusDto(
    val bookId: Long,
    val bookTitle: String,
    val totalReservations: Int,
    val pendingReservations: Int,
    val nextAvailableDate: LocalDateTime?
) 