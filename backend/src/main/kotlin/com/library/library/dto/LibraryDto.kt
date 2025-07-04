package com.library.library.dto

import com.library.library.entity.Library
import java.time.LocalDateTime

data class LibraryResponseDto(
    val id: Long,
    val name: String,
    val address: String,
    val phone: String?,
    val latitude: Double?,
    val longitude: Double?,
    val openingHours: String?,
    val description: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun from(library: Library): LibraryResponseDto {
            return LibraryResponseDto(
                id = library.id,
                name = library.name,
                address = library.address,
                phone = library.phone,
                latitude = library.latitude,
                longitude = library.longitude,
                openingHours = library.openingHours,
                description = library.description,
                createdAt = library.createdAt,
                updatedAt = library.updatedAt
            )
        }
    }
}

data class LibrarySearchRequestDto(
    val name: String? = null,
    val address: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val radius: Double? = null // km 단위
) 