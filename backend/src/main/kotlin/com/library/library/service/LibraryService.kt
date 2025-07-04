package com.library.library.service

import com.library.library.dto.LibraryResponseDto
import com.library.library.dto.LibrarySearchRequestDto
import com.library.library.entity.Library
import com.library.library.repository.LibraryRepository
import org.springframework.stereotype.Service
import kotlin.math.*

@Service
class LibraryService(
    private val libraryRepository: LibraryRepository
) {

    fun getAllLibraries(): List<LibraryResponseDto> {
        return libraryRepository.findAll()
            .map { LibraryResponseDto.from(it) }
    }

    fun getLibraryById(id: Long): LibraryResponseDto? {
        return libraryRepository.findById(id)
            .map { LibraryResponseDto.from(it) }
            .orElse(null)
    }

    fun searchLibraries(searchRequest: LibrarySearchRequestDto): List<LibraryResponseDto> {
        val libraries = when {
            // 위치 기반 검색
            searchRequest.latitude != null && searchRequest.longitude != null && searchRequest.radius != null -> {
                searchByLocation(searchRequest.latitude, searchRequest.longitude, searchRequest.radius)
            }
            // 이름으로 검색
            !searchRequest.name.isNullOrBlank() -> {
                libraryRepository.findByNameContainingIgnoreCase(searchRequest.name)
            }
            // 주소로 검색
            !searchRequest.address.isNullOrBlank() -> {
                libraryRepository.findByAddressContainingIgnoreCase(searchRequest.address)
            }
            // 검색 조건이 없으면 전체 목록
            else -> {
                libraryRepository.findAll()
            }
        }

        return libraries.map { LibraryResponseDto.from(it) }
    }

    private fun searchByLocation(latitude: Double, longitude: Double, radiusKm: Double): List<Library> {
        // 위도 1도 ≈ 111km, 경도 1도 ≈ 111km * cos(latitude)
        val latDegree = radiusKm / 111.0
        val lngDegree = radiusKm / (111.0 * cos(Math.toRadians(latitude)))

        val minLat = latitude - latDegree
        val maxLat = latitude + latDegree
        val minLng = longitude - lngDegree
        val maxLng = longitude + lngDegree

        return libraryRepository.findByLocationRange(minLat, maxLat, minLng, maxLng)
            .filter { library ->
                library.latitude != null && library.longitude != null &&
                calculateDistance(latitude, longitude, library.latitude, library.longitude) <= radiusKm
            }
    }

    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371.0 // km

        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)

        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
} 