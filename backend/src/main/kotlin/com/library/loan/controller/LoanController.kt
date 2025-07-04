package com.library.loan.controller

import com.library.loan.dto.*
import com.library.loan.service.LoanService
import com.library.common.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/loans")
class LoanController(
    private val loanService: LoanService
) {

    // 대출 생성
    @PostMapping
    fun createLoan(@RequestBody request: LoanCreateRequestDto): ResponseEntity<ApiResponse<LoanResponseDto>> {
        return try {
            val loan = loanService.createLoan(request)
            ResponseEntity.ok(
                ApiResponse.success(loan, "대출이 성공적으로 생성되었습니다")
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "잘못된 요청입니다")
            )
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "대출할 수 없는 상태입니다")
            )
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(
                ApiResponse.error("대출 생성 중 오류가 발생했습니다")
            )
        }
    }

    // 대출 반납
    @PutMapping("/{loanId}/return")
    fun returnLoan(
        @PathVariable loanId: Long,
        @RequestBody(required = false) request: LoanReturnRequestDto?
    ): ResponseEntity<ApiResponse<LoanResponseDto>> {
        return try {
            val returnRequest = request ?: LoanReturnRequestDto(loanId)
            val loan = loanService.returnLoan(returnRequest)
            ResponseEntity.ok(
                ApiResponse.success(loan, "도서가 성공적으로 반납되었습니다")
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "잘못된 요청입니다")
            )
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "반납할 수 없는 상태입니다")
            )
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(
                ApiResponse.error("반납 처리 중 오류가 발생했습니다")
            )
        }
    }

    // 대출 연장
    @PutMapping("/{loanId}/extend")
    fun extendLoan(
        @PathVariable loanId: Long,
        @RequestBody(required = false) request: LoanExtendRequestDto?
    ): ResponseEntity<ApiResponse<LoanResponseDto>> {
        return try {
            val extendRequest = request ?: LoanExtendRequestDto(loanId)
            val loan = loanService.extendLoan(extendRequest)
            ResponseEntity.ok(
                ApiResponse.success(loan, "대출이 성공적으로 연장되었습니다")
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "잘못된 요청입니다")
            )
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "연장할 수 없는 상태입니다")
            )
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(
                ApiResponse.error("대출 연장 중 오류가 발생했습니다")
            )
        }
    }

    // 전체 대출 목록 조회
    @GetMapping
    fun getAllLoans(): ResponseEntity<ApiResponse<LoanListResponseDto>> {
        return try {
            val loans = loanService.getAllLoans()
            ResponseEntity.ok(
                ApiResponse.success(loans, "대출 목록 조회 성공")
            )
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(
                ApiResponse.error("대출 목록 조회 중 오류가 발생했습니다")
            )
        }
    }

    // 사용자별 대출 목록 조회
    @GetMapping("/user/{userId}")
    fun getLoansByUser(@PathVariable userId: Long): ResponseEntity<ApiResponse<LoanListResponseDto>> {
        return try {
            val loans = loanService.getLoansByUser(userId)
            ResponseEntity.ok(
                ApiResponse.success(loans, "사용자 대출 목록 조회 성공")
            )
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(
                ApiResponse.error("사용자 대출 목록 조회 중 오류가 발생했습니다")
            )
        }
    }

    // 연체 대출 목록 조회
    @GetMapping("/overdue")
    fun getOverdueLoans(): ResponseEntity<ApiResponse<List<LoanResponseDto>>> {
        return try {
            val overdueLoans = loanService.getOverdueLoans()
            ResponseEntity.ok(
                ApiResponse.success(overdueLoans, "연체 대출 목록 조회 성공")
            )
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(
                ApiResponse.error("연체 대출 목록 조회 중 오류가 발생했습니다")
            )
        }
    }

    // 특정 대출 상세 조회
    @GetMapping("/{loanId}")
    fun getLoanById(@PathVariable loanId: Long): ResponseEntity<ApiResponse<LoanResponseDto>> {
        return try {
            val loan = loanService.getLoanById(loanId)
            ResponseEntity.ok(
                ApiResponse.success(loan, "대출 정보 조회 성공")
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "대출 정보를 찾을 수 없습니다")
            )
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(
                ApiResponse.error("대출 정보 조회 중 오류가 발생했습니다")
            )
        }
    }

    // 대출 API 테스트 (디버깅용)
    @GetMapping("/test")
    fun testLoanController(): ResponseEntity<ApiResponse<String>> {
        return ResponseEntity.ok(
            ApiResponse.success("LoanController is working!", "대출 컨트롤러 테스트 성공")
        )
    }
} 