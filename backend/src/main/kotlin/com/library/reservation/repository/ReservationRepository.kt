package com.library.reservation.repository

import com.library.reservation.entity.Reservation
import com.library.reservation.entity.ReservationStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ReservationRepository : JpaRepository<Reservation, Long> {
    
    // 사용자별 예약 목록 조회
    fun findByUserIdOrderByReservationDateDesc(userId: Long): List<Reservation>
    
    // 도서별 예약 목록 조회
    fun findByBookIdOrderByReservationDateAsc(bookId: Long): List<Reservation>
    
    // 상태별 예약 목록 조회
    fun findByStatusOrderByReservationDateAsc(status: ReservationStatus): List<Reservation>
    
    // 사용자의 특정 상태 예약 조회
    fun findByUserIdAndStatus(userId: Long, status: ReservationStatus): List<Reservation>
    
    // 도서의 특정 상태 예약 조회
    fun findByBookIdAndStatus(bookId: Long, status: ReservationStatus): List<Reservation>
    
    // 만료된 예약 조회
    @Query("SELECT r FROM Reservation r WHERE r.status = 'READY' AND r.expiryDate < CURRENT_TIMESTAMP")
    fun findExpiredReservations(): List<Reservation>
    
    // 사용자의 특정 도서 예약 존재 여부 확인
    fun existsByUserIdAndBookIdAndStatusIn(userId: Long, bookId: Long, statuses: List<ReservationStatus>): Boolean
    
    // 도서의 대기 중인 예약 수 조회
    fun countByBookIdAndStatus(bookId: Long, status: ReservationStatus): Long
} 