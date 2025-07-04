package com.library.library.repository

import com.library.library.entity.Library
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LibraryRepository : JpaRepository<Library, Long> {
    
    // 도서관 이름으로 검색
    fun findByNameContainingIgnoreCase(name: String): List<Library>
    
    // 주소로 검색
    fun findByAddressContainingIgnoreCase(address: String): List<Library>
    
    // 위치 기반 검색 (위도, 경도 범위)
    @Query("""
        SELECT l FROM Library l 
        WHERE l.latitude BETWEEN :minLat AND :maxLat 
        AND l.longitude BETWEEN :minLng AND :maxLng
    """)
    fun findByLocationRange(
        @Param("minLat") minLatitude: Double,
        @Param("maxLat") maxLatitude: Double,
        @Param("minLng") minLongitude: Double,
        @Param("maxLng") maxLongitude: Double
    ): List<Library>
} 