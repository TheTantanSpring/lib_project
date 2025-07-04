package com.library.reservation.controller

import com.library.reservation.dto.*
import com.library.reservation.service.ReservationService
import com.library.common.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reservations")
class ReservationController(
    private val reservationService: ReservationService
) {

    // 예약 생성 (임시 버전)
    @PostMapping
    fun createReservation(@RequestBody request: ReservationCreateRequestDto): ResponseEntity<ApiResponse<String>> {
        return try {
            ResponseEntity.ok(
                ApiResponse.success("임시 응답", "예약 생성 API 호출됨 (미구현)")
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "예약 생성 실패")
            )
        }
    }

    // 예약 취소
    @PutMapping("/{reservationId}/cancel")
    fun cancelReservation(
        @PathVariable reservationId: Long,
        @RequestBody(required = false) request: ReservationCancelRequestDto?
    ): ResponseEntity<ApiResponse<ReservationResponseDto>> {
        return try {
            val cancelRequest = request ?: ReservationCancelRequestDto(reservationId)
            val reservation = reservationService.cancelReservation(cancelRequest)
            ResponseEntity.ok(
                ApiResponse.success(reservation, "예약이 취소되었습니다")
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "잘못된 요청입니다")
            )
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "취소할 수 없는 상태입니다")
            )
        }
    }

    // 전체 예약 목록 조회 (임시 버전)
    @GetMapping
    fun getAllReservations(): ResponseEntity<ApiResponse<String>> {
        return try {
            ResponseEntity.ok(
                ApiResponse.success("임시 응답", "예약 목록 조회 API 호출됨 (미구현)")
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "예약 목록 조회 실패")
            )
        }
    }

    // 사용자별 예약 목록 조회
    @GetMapping("/user/{userId}")
    fun getReservationsByUser(@PathVariable userId: Long): ResponseEntity<ApiResponse<ReservationListResponseDto>> {
        return try {
            val reservations = reservationService.getReservationsByUser(userId)
            ResponseEntity.ok(
                ApiResponse.success(reservations, "사용자 예약 목록 조회 성공")
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "사용자 예약 목록 조회 실패")
            )
        }
    }

    // 도서별 예약 현황 조회
    @GetMapping("/book/{bookId}")
    fun getBookReservationStatus(@PathVariable bookId: Long): ResponseEntity<ApiResponse<BookReservationStatusDto>> {
        return try {
            val status = reservationService.getBookReservationStatus(bookId)
            ResponseEntity.ok(
                ApiResponse.success(status, "도서 예약 현황 조회 성공")
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "도서를 찾을 수 없습니다")
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "예약 현황 조회 실패")
            )
        }
    }

    // 특정 예약 상세 조회
    @GetMapping("/{reservationId}")
    fun getReservationById(@PathVariable reservationId: Long): ResponseEntity<ApiResponse<ReservationResponseDto>> {
        return try {
            val reservation = reservationService.getReservationById(reservationId)
            ResponseEntity.ok(
                ApiResponse.success(reservation, "예약 정보 조회 성공")
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "예약 정보를 찾을 수 없습니다")
            )
        }
    }

    // 만료된 예약 처리 (관리자용)
    @PutMapping("/expired/process")
    fun processExpiredReservations(): ResponseEntity<ApiResponse<List<ReservationResponseDto>>> {
        return try {
            val expiredReservations = reservationService.processExpiredReservations()
            ResponseEntity.ok(
                ApiResponse.success(expiredReservations, "만료된 예약 처리 완료")
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "만료된 예약 처리 실패")
            )
        }
    }
} 