package com.library.library

import com.fasterxml.jackson.databind.ObjectMapper
import com.library.library.dto.LibraryResponseDto
import com.library.library.dto.LibrarySearchRequestDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
@DisplayName("도서관 도메인 통합 테스트")
class LibraryIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("모든 도서관 조회 API 테스트")
    fun getAllLibraries() {
        // when & then
        mockMvc.perform(get("/api/libraries"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("도서관 목록 조회 성공"))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(5)) // 샘플 데이터 5개
            .andExpect(jsonPath("$.data[0].name").value("서울시립도서관"))
            .andExpect(jsonPath("$.data[0].address").value("서울특별시 강남구 테헤란로 123"))
            .andExpect(jsonPath("$.data[0].phone").value("02-1234-5678"))
            .andExpect(jsonPath("$.data[1].name").value("강남구립도서관"))
    }

    @Test
    @DisplayName("도서관 ID로 조회 API 테스트")
    fun getLibraryById() {
        // when & then - 존재하는 도서관 조회
        mockMvc.perform(get("/api/libraries/1"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("도서관 정보 조회 성공"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("서울시립도서관"))
            .andExpect(jsonPath("$.data.address").value("서울특별시 강남구 테헤란로 123"))
            .andExpect(jsonPath("$.data.latitude").value(37.5665))
            .andExpect(jsonPath("$.data.longitude").value(126.9780))
    }

    @Test
    @DisplayName("존재하지 않는 도서관 조회 API 테스트")
    fun getLibraryByIdNotFound() {
        // when & then - 존재하지 않는 도서관 조회
        mockMvc.perform(get("/api/libraries/999"))
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("도서관 이름으로 검색 API 테스트")
    fun searchLibrariesByName() {
        // when & then
        mockMvc.perform(get("/api/libraries/search")
            .param("name", "강남"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("도서관 검색 성공"))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].name").value("강남구립도서관"))
    }

    @Test
    @DisplayName("도서관 주소로 검색 API 테스트")
    fun searchLibrariesByAddress() {
        // when & then
        mockMvc.perform(get("/api/libraries/search")
            .param("address", "서초"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(2)) // 서초구립도서관, 국립중앙도서관
    }

    @Test
    @DisplayName("도서관 위치 기반 검색 API 테스트")
    fun searchLibrariesByLocation() {
        // given - 서울시립도서관 위치 기준 (37.5665, 126.9780)
        val latitude = 37.5665
        val longitude = 126.9780
        val radius = 10.0 // 10km 반경

        // when & then
        mockMvc.perform(get("/api/libraries/search")
            .param("latitude", latitude.toString())
            .param("longitude", longitude.toString())
            .param("radius", radius.toString()))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            // 반경 내 도서관들이 조회되어야 함
    }

    @Test
    @DisplayName("빈 검색 조건으로 검색 API 테스트")
    fun searchLibrariesWithEmptyParams() {
        // when & then - 빈 검색 조건으로 검색하면 모든 도서관 반환
        mockMvc.perform(get("/api/libraries/search"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(5))
    }

    @Test
    @DisplayName("복합 검색 조건 API 테스트")
    fun searchLibrariesWithMultipleParams() {
        // when & then
        mockMvc.perform(get("/api/libraries/search")
            .param("name", "구립")
            .param("address", "서울"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            // "구립"이 포함되고 "서울"이 주소에 포함된 도서관들
    }

    @Test
    @DisplayName("잘못된 매개변수로 검색 API 테스트")
    fun searchLibrariesWithInvalidParams() {
        // when & then - 잘못된 latitude/longitude 값
        mockMvc.perform(get("/api/libraries/search")
            .param("latitude", "invalid")
            .param("longitude", "126.9780"))
            .andExpect(status().isBadRequest)
    }

    @Test
    @DisplayName("API 응답 구조 검증 테스트")
    fun validateApiResponseStructure() {
        // when & then
        mockMvc.perform(get("/api/libraries"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").exists())
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.data").exists())
            .andExpect(jsonPath("$.timestamp").exists())
            // 각 도서관 객체의 필수 필드 검증
            .andExpect(jsonPath("$.data[0].id").exists())
            .andExpect(jsonPath("$.data[0].name").exists())
            .andExpect(jsonPath("$.data[0].address").exists())
            .andExpect(jsonPath("$.data[0].phone").exists())
            .andExpect(jsonPath("$.data[0].latitude").exists())
            .andExpect(jsonPath("$.data[0].longitude").exists())
            .andExpect(jsonPath("$.data[0].openingHours").exists())
            .andExpect(jsonPath("$.data[0].description").exists())
            .andExpect(jsonPath("$.data[0].createdAt").exists())
            .andExpect(jsonPath("$.data[0].updatedAt").exists())
    }

    @Test
    @DisplayName("데이터 유효성 검증 테스트")
    fun validateDataIntegrity() {
        // when & then
        mockMvc.perform(get("/api/libraries/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").isString)
            .andExpect(jsonPath("$.data.address").isString)
            .andExpect(jsonPath("$.data.latitude").isNumber)
            .andExpect(jsonPath("$.data.longitude").isNumber)
            // 위도는 -90 ~ 90 범위
            .andExpect(jsonPath("$.data.latitude").value(org.hamcrest.Matchers.allOf(
                org.hamcrest.Matchers.greaterThanOrEqualTo(-90.0),
                org.hamcrest.Matchers.lessThanOrEqualTo(90.0)
            )))
            // 경도는 -180 ~ 180 범위
            .andExpect(jsonPath("$.data.longitude").value(org.hamcrest.Matchers.allOf(
                org.hamcrest.Matchers.greaterThanOrEqualTo(-180.0),
                org.hamcrest.Matchers.lessThanOrEqualTo(180.0)
            )))
    }

    @Test
    @DisplayName("성능 테스트 - 모든 도서관 조회")
    fun performanceTestGetAllLibraries() {
        val startTime = System.currentTimeMillis()
        
        // when
        mockMvc.perform(get("/api/libraries"))
            .andExpect(status().isOk)
        
        val endTime = System.currentTimeMillis()
        val responseTime = endTime - startTime
        
        // then - 응답 시간이 1초 이내
        assert(responseTime < 1000) { "응답 시간이 1초를 초과했습니다: ${responseTime}ms" }
    }

    @Test
    @DisplayName("동시성 테스트 - 여러 사용자가 동시에 조회")
    fun concurrencyTestGetLibraries() {
        // given
        val threads = mutableListOf<Thread>()
        val results = mutableListOf<Boolean>()

        // when - 10개의 스레드로 동시 요청
        repeat(10) { index ->
            val thread = Thread {
                try {
                    mockMvc.perform(get("/api/libraries"))
                        .andExpect(status().isOk)
                        .andExpect(jsonPath("$.success").value(true))
                    results.add(true)
                } catch (e: Exception) {
                    results.add(false)
                }
            }
            threads.add(thread)
            thread.start()
        }

        // 모든 스레드 종료 대기
        threads.forEach { it.join() }

        // then - 모든 요청이 성공해야 함
        assert(results.size == 10) { "일부 요청이 완료되지 않았습니다" }
        assert(results.all { it }) { "일부 요청이 실패했습니다" }
    }
} 