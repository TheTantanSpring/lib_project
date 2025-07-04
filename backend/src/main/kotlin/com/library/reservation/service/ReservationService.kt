package com.library.reservation.service

import com.library.reservation.dto.*
import com.library.reservation.entity.Reservation
import com.library.reservation.entity.ReservationStatus
import com.library.reservation.repository.ReservationRepository
import com.library.book.repository.BookRepository
import com.library.user.repository.UserRepository
import com.library.loan.repository.LoanRepository
import com.library.loan.entity.LoanStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ReservationService(
    private val reservationRepository: ReservationRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val loanRepository: LoanRepository
) {

    // 예약 생성
    fun createReservation(request: ReservationCreateRequestDto): ReservationResponseDto {
        val user = userRepository.findById(request.userId)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다: ${request.userId}") }
        
        val book = bookRepository.findById(request.bookId)
            .orElseThrow { IllegalArgumentException("도서를 찾을 수 없습니다: ${request.bookId}") }

        // 중복 예약 확인
        val activeStatuses = listOf(ReservationStatus.PENDING, ReservationStatus.READY)
        if (reservationRepository.existsByUserIdAndBookIdAndStatusIn(request.userId, request.bookId, activeStatuses)) {
            throw IllegalStateException("이미 예약된 도서입니다")
        }

        // 도서 대출 가능 여부 확인
        if (book.availableCopies > 0) {
            val activeLoansCount = loanRepository.findByBookIdAndStatus(request.bookId, LoanStatus.ACTIVE).size
            if (activeLoansCount < book.totalCopies) {
                throw IllegalStateException("대출 가능한 도서는 예약할 수 없습니다. 직접 대출하세요.")
            }
        }

        val reservation = Reservation(
            user = user,
            book = book,
            status = ReservationStatus.PENDING
        )

        val savedReservation = reservationRepository.save(reservation)
        val queuePosition = getQueuePosition(request.bookId, savedReservation.id)
        
        return ReservationResponseDto.from(savedReservation, queuePosition)
    }

    // 예약 취소
    fun cancelReservation(request: ReservationCancelRequestDto): ReservationResponseDto {
        val reservation = reservationRepository.findById(request.reservationId)
            .orElseThrow { IllegalArgumentException("예약 정보를 찾을 수 없습니다: ${request.reservationId}") }

        if (reservation.status !in listOf(ReservationStatus.PENDING, ReservationStatus.READY)) {
            throw IllegalStateException("취소할 수 없는 예약 상태입니다: ${reservation.status}")
        }

        val updatedReservation = reservation.copy(
            status = ReservationStatus.CANCELLED,
            updatedAt = LocalDateTime.now()
        )

        val savedReservation = reservationRepository.save(updatedReservation)
        
        // 다음 대기자에게 알림 처리 (여기서는 상태만 업데이트)
        processNextInQueue(reservation.book.id)
        
        return ReservationResponseDto.from(savedReservation)
    }

    // 예약 목록 조회 (전체)
    @Transactional(readOnly = true)
    fun getAllReservations(): ReservationListResponseDto {
        val reservations = reservationRepository.findAll()
        val reservationResponses = reservations.map { reservation ->
            val queuePosition = if (reservation.status == ReservationStatus.PENDING) {
                getQueuePosition(reservation.book.id, reservation.id)
            } else null
            ReservationResponseDto.from(reservation, queuePosition)
        }
        
        return ReservationListResponseDto(
            reservations = reservationResponses,
            totalCount = reservations.size,
            pendingCount = reservations.count { it.status == ReservationStatus.PENDING },
            readyCount = reservations.count { it.status == ReservationStatus.READY },
            expiredCount = reservations.count { it.status == ReservationStatus.EXPIRED }
        )
    }

    // 사용자별 예약 목록 조회
    @Transactional(readOnly = true)
    fun getReservationsByUser(userId: Long): ReservationListResponseDto {
        val reservations = reservationRepository.findByUserIdOrderByReservationDateDesc(userId)
        val reservationResponses = reservations.map { reservation ->
            val queuePosition = if (reservation.status == ReservationStatus.PENDING) {
                getQueuePosition(reservation.book.id, reservation.id)
            } else null
            ReservationResponseDto.from(reservation, queuePosition)
        }
        
        return ReservationListResponseDto(
            reservations = reservationResponses,
            totalCount = reservations.size,
            pendingCount = reservations.count { it.status == ReservationStatus.PENDING },
            readyCount = reservations.count { it.status == ReservationStatus.READY },
            expiredCount = reservations.count { it.status == ReservationStatus.EXPIRED }
        )
    }

    // 도서별 예약 현황 조회
    @Transactional(readOnly = true)
    fun getBookReservationStatus(bookId: Long): BookReservationStatusDto {
        val book = bookRepository.findById(bookId)
            .orElseThrow { IllegalArgumentException("도서를 찾을 수 없습니다: $bookId") }

        val allReservations = reservationRepository.findByBookIdOrderByReservationDateAsc(bookId)
        val pendingReservations = allReservations.filter { it.status == ReservationStatus.PENDING }
        
        // 다음 대출 가능 예상 날짜 계산 (간단한 추정)
        val activeLoans = loanRepository.findByBookIdAndStatus(bookId, LoanStatus.ACTIVE)
        val nextAvailableDate = if (activeLoans.isNotEmpty()) {
            activeLoans.minOfOrNull { it.dueDate }
        } else null

        return BookReservationStatusDto(
            bookId = bookId,
            bookTitle = book.title,
            totalReservations = allReservations.size,
            pendingReservations = pendingReservations.size,
            nextAvailableDate = nextAvailableDate
        )
    }

    // 예약 상세 조회
    @Transactional(readOnly = true)
    fun getReservationById(reservationId: Long): ReservationResponseDto {
        val reservation = reservationRepository.findById(reservationId)
            .orElseThrow { IllegalArgumentException("예약 정보를 찾을 수 없습니다: $reservationId") }
        
        val queuePosition = if (reservation.status == ReservationStatus.PENDING) {
            getQueuePosition(reservation.book.id, reservation.id)
        } else null
        
        return ReservationResponseDto.from(reservation, queuePosition)
    }

    // 만료된 예약 처리
    fun processExpiredReservations(): List<ReservationResponseDto> {
        val expiredReservations = reservationRepository.findExpiredReservations()
        
        val updatedReservations = expiredReservations.map { reservation ->
            val updated = reservation.copy(
                status = ReservationStatus.EXPIRED,
                updatedAt = LocalDateTime.now()
            )
            reservationRepository.save(updated)
        }
        
        return updatedReservations.map { ReservationResponseDto.from(it) }
    }

    // 대기 순번 계산 (private helper method)
    private fun getQueuePosition(bookId: Long, reservationId: Long): Int {
        val pendingReservations = reservationRepository.findByBookIdAndStatus(bookId, ReservationStatus.PENDING)
            .sortedBy { it.reservationDate }
        
        return pendingReservations.indexOfFirst { it.id == reservationId } + 1
    }

    // 다음 대기자 처리 (private helper method)
    private fun processNextInQueue(bookId: Long) {
        val book = bookRepository.findById(bookId).orElse(null) ?: return
        
        if (book.availableCopies > 0) {
            val nextReservation = reservationRepository.findByBookIdAndStatus(bookId, ReservationStatus.PENDING)
                .minByOrNull { it.reservationDate }
            
            nextReservation?.let { reservation ->
                val updatedReservation = reservation.copy(
                    status = ReservationStatus.READY,
                    expiryDate = LocalDateTime.now().plusDays(3), // 3일 후 만료
                    updatedAt = LocalDateTime.now()
                )
                reservationRepository.save(updatedReservation)
            }
        }
    }
} 