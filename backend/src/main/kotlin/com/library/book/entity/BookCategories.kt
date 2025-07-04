package com.library.book.entity

/**
 * 도서 카테고리 상수 및 유틸리티 클래스
 * 
 * 확장 고려사항:
 * 1. 표준 카테고리를 상수로 정의하여 추후 Enum 전환 시 매핑 용이
 * 2. 검증 로직을 중앙화하여 일관성 유지
 * 3. 계층 구조 지원 가능하도록 설계
 * 4. 다국어 지원 가능하도록 구조화
 */
object BookCategories {
    
    // 표준 카테고리 상수 (추후 Enum 전환 시 매핑 용이)
    const val COMPUTER_IT = "컴퓨터/IT"
    const val LITERATURE = "문학"
    const val SCIENCE = "과학"
    const val PHILOSOPHY = "철학"
    const val HISTORY = "역사"
    const val ARTS = "예술"
    const val LANGUAGE = "언어"
    const val SOCIAL_SCIENCE = "사회과학"
    const val NATURAL_SCIENCE = "자연과학"
    const val TECHNOLOGY = "기술과학"
    const val RELIGION = "종교"
    const val GENERAL = "총류"
    
    // 표준 카테고리 목록 (검증 및 제안용)
    val STANDARD_CATEGORIES = listOf(
        COMPUTER_IT,
        LITERATURE,
        SCIENCE,
        PHILOSOPHY,
        HISTORY,
        ARTS,
        LANGUAGE,
        SOCIAL_SCIENCE,
        NATURAL_SCIENCE,
        TECHNOLOGY,
        RELIGION,
        GENERAL
    )
    
    // 카테고리 설명 매핑 (추후 다국어 지원 기반)
    private val CATEGORY_DESCRIPTIONS = mapOf(
        COMPUTER_IT to "프로그래밍, 소프트웨어, 하드웨어, 네트워크 관련 도서",
        LITERATURE to "소설, 시, 수필, 희곡 등 문학 작품",
        SCIENCE to "과학 전반에 관한 도서",
        PHILOSOPHY to "철학, 심리학, 윤리학 관련 도서",
        HISTORY to "역사, 지리, 전기 관련 도서",
        ARTS to "음악, 미술, 사진, 건축, 조각 관련 도서",
        LANGUAGE to "언어학, 어학 관련 도서",
        SOCIAL_SCIENCE to "정치학, 법학, 경제학, 사회학, 교육학 관련 도서",
        NATURAL_SCIENCE to "수학, 물리학, 화학, 생물학, 지구과학 관련 도서",
        TECHNOLOGY to "의학, 공학, 건축학, 기계공학, 전기공학 관련 도서",
        RELIGION to "종교, 기독교, 불교, 기타 종교 관련 도서",
        GENERAL to "백과사전, 논문집, 도서관학, 정보학 관련 도서"
    )
    
    // 카테고리 계층 구조 (추후 확장 시 활용)
    private val CATEGORY_HIERARCHY = mapOf(
        COMPUTER_IT to listOf("프로그래밍", "데이터베이스", "네트워크", "AI/ML", "웹개발"),
        LITERATURE to listOf("한국문학", "외국문학", "고전문학", "현대문학"),
        SCIENCE to listOf("물리학", "화학", "생물학", "지구과학", "천문학"),
        PHILOSOPHY to listOf("서양철학", "동양철학", "심리학", "윤리학"),
        HISTORY to listOf("한국사", "세계사", "근현대사", "고대사"),
        ARTS to listOf("음악", "미술", "사진", "영화", "연극"),
        LANGUAGE to listOf("한국어", "영어", "중국어", "일본어", "기타언어"),
        SOCIAL_SCIENCE to listOf("정치학", "경제학", "사회학", "교육학", "법학"),
        NATURAL_SCIENCE to listOf("수학", "물리학", "화학", "생물학", "지구과학"),
        TECHNOLOGY to listOf("의학", "공학", "건축학", "기계공학", "전기공학"),
        RELIGION to listOf("기독교", "불교", "이슬람교", "힌두교", "기타종교"),
        GENERAL to listOf("백과사전", "논문집", "도서관학", "정보학", "기타")
    )
    
    /**
     * 카테고리 유효성 검증
     * 확장 고려: 추후 DB 기반 검증으로 변경 가능
     */
    fun isValidCategory(category: String?): Boolean {
        return category != null && category.isNotBlank()
    }
    
    /**
     * 표준 카테고리 여부 확인
     * 확장 고려: 추후 Enum 또는 DB 기반으로 변경 가능
     */
    fun isStandardCategory(category: String): Boolean {
        return STANDARD_CATEGORIES.contains(category)
    }
    
    /**
     * 카테고리 정규화 (공백 제거, 대소문자 통일)
     * 확장 고려: 추후 더 복잡한 정규화 로직 추가 가능
     */
    fun normalizeCategory(category: String): String {
        return category.trim()
    }
    
    /**
     * 카테고리 제안 (유사한 카테고리 찾기)
     * 확장 고려: 추후 AI 기반 제안 시스템으로 확장 가능
     */
    fun suggestCategory(input: String): List<String> {
        val normalizedInput = input.lowercase().trim()
        return STANDARD_CATEGORIES.filter { 
            it.lowercase().contains(normalizedInput) 
        }.take(5)
    }
    
    /**
     * 카테고리 설명 조회
     * 확장 고려: 추후 다국어 지원 시 locale 매개변수 추가
     */
    fun getCategoryDescription(category: String): String? {
        return CATEGORY_DESCRIPTIONS[category]
    }
    
    /**
     * 하위 카테고리 목록 조회
     * 확장 고려: 추후 계층 구조 DB 테이블로 확장 가능
     */
    fun getSubCategories(category: String): List<String> {
        return CATEGORY_HIERARCHY[category] ?: emptyList()
    }
    
    /**
     * 카테고리 통계 정보
     * 확장 고려: 추후 캐싱 및 실시간 통계로 확장 가능
     */
    data class CategoryStats(
        val name: String,
        val description: String?,
        val bookCount: Long = 0,
        val subCategories: List<String> = emptyList(),
        val isStandard: Boolean = false
    )
    
    /**
     * 카테고리 정보 조회
     * 확장 고려: 추후 Repository 패턴으로 확장 가능
     */
    fun getCategoryInfo(category: String): CategoryStats {
        return CategoryStats(
            name = category,
            description = getCategoryDescription(category),
            subCategories = getSubCategories(category),
            isStandard = isStandardCategory(category)
        )
    }
} 