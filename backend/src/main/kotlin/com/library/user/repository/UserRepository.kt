package com.library.user.repository

import com.library.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    
    // 사용자명으로 조회
    fun findByUsername(username: String): User?
    
    // 이메일로 조회
    fun findByEmail(email: String): User?
    
    // 사용자명 존재 여부 확인
    fun existsByUsername(username: String): Boolean
    
    // 이메일 존재 여부 확인
    fun existsByEmail(email: String): Boolean
} 