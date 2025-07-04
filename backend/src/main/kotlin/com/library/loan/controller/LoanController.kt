package com.library.loan.controller

import com.library.loan.dto.*
import com.library.loan.service.LoanService
import com.library.common.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/loans")
class LoanController {

    // 대출 API 테스트 (디버깅용)
    @GetMapping("/test")
    fun testLoanController(): ResponseEntity<ApiResponse<String>> {
        return ResponseEntity.ok(
            ApiResponse.success("LoanController is working!", "대출 컨트롤러 테스트 성공")
        )
    }

    // 대출 생성 (임시 버전)
    @PostMapping
    fun createLoan(@RequestBody request: LoanCreateRequestDto): ResponseEntity<ApiResponse<String>> {
        return try {
            ResponseEntity.ok(
                ApiResponse.success("임시 응답", "대출 생성 API 호출됨 (미구현)")
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "대출 생성 실패")
            )
        }
    }

    // 대출 반납 (임시 버전)
    @PutMapping("/{loanId}/return")
    fun returnLoan(@PathVariable loanId: Long): ResponseEntity<ApiResponse<String>> {
        return try {
            ResponseEntity.ok(
                ApiResponse.success("임시 응답", "대출 반납 API 호출됨 (미구현) - LoanId: $loanId")
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "대출 반납 실패")
            )
        }
    }

    // 대출 연장 (임시 버전)
    @PutMapping("/{loanId}/extend")
    fun extendLoan(@PathVariable loanId: Long): ResponseEntity<ApiResponse<String>> {
        return try {
            ResponseEntity.ok(
                ApiResponse.success("임시 응답", "대출 연장 API 호출됨 (미구현) - LoanId: $loanId")
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "대출 연장 실패")
            )
        }
    }

    // 전체 대출 목록 조회 (임시 버전)
    @GetMapping
    fun getAllLoans(): ResponseEntity<ApiResponse<String>> {
        return try {
            ResponseEntity.ok(
                ApiResponse.success("임시 응답", "대출 목록 조회 API 호출됨 (미구현)")
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "대출 목록 조회 실패")
            )
        }
    }

    // 사용자별 대출 목록 조회 (임시 버전)
    @GetMapping("/user/{userId}")
    fun getLoansByUser(@PathVariable userId: Long): ResponseEntity<ApiResponse<String>> {
        return try {
            ResponseEntity.ok(
                ApiResponse.success("임시 응답", "사용자 대출 목록 조회 API 호출됨 (미구현) - UserId: $userId")
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "사용자 대출 목록 조회 실패")
            )
        }
    }

    // 연체 대출 목록 조회 (임시 버전)
    @GetMapping("/overdue")
    fun getOverdueLoans(): ResponseEntity<ApiResponse<String>> {
        return try {
            ResponseEntity.ok(
                ApiResponse.success("임시 응답", "연체 대출 목록 조회 API 호출됨 (미구현)")
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "연체 대출 목록 조회 실패")
            )
        }
    }

    // 특정 대출 상세 조회 (임시 버전)
    @GetMapping("/{loanId}")
    fun getLoanById(@PathVariable loanId: Long): ResponseEntity<ApiResponse<String>> {
        return try {
            ResponseEntity.ok(
                ApiResponse.success("임시 응답", "대출 정보 조회 API 호출됨 (미구현) - LoanId: $loanId")
            )
        } catch (e: Exception) {
            ResponseEntity.badRequest().body(
                ApiResponse.error(e.message ?: "대출 정보 조회 실패")
            )
        }
    }
} 