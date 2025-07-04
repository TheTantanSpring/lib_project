package com.library.library.controller

import com.library.common.response.ApiResponse
import com.library.library.dto.LibraryResponseDto
import com.library.library.dto.LibrarySearchRequestDto
import com.library.library.service.LibraryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/libraries")
class LibraryController(
    private val libraryService: LibraryService
) {

    @GetMapping
    fun getAllLibraries(): ResponseEntity<ApiResponse<List<LibraryResponseDto>>> {
        val libraries = libraryService.getAllLibraries()
        return ResponseEntity.ok(
            ApiResponse.success(libraries, "도서관 목록 조회 성공")
        )
    }

    @GetMapping("/{id}")
    fun getLibraryById(@PathVariable id: Long): ResponseEntity<ApiResponse<LibraryResponseDto>> {
        val library = libraryService.getLibraryById(id)
        return if (library != null) {
            ResponseEntity.ok(
                ApiResponse.success(library, "도서관 정보 조회 성공")
            )
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/search")
    fun searchLibraries(
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) address: String?,
        @RequestParam(required = false) latitude: Double?,
        @RequestParam(required = false) longitude: Double?,
        @RequestParam(required = false, defaultValue = "5.0") radius: Double?
    ): ResponseEntity<ApiResponse<List<LibraryResponseDto>>> {
        val searchRequest = LibrarySearchRequestDto(
            name = name,
            address = address,
            latitude = latitude,
            longitude = longitude,
            radius = radius
        )
        
        val libraries = libraryService.searchLibraries(searchRequest)
        return ResponseEntity.ok(
            ApiResponse.success(libraries, "도서관 검색 성공")
        )
    }
} 