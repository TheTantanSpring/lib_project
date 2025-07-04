package com.library.library.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "libraries")
data class Library(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val address: String,

    val phone: String? = null,

    val latitude: Double? = null,

    val longitude: Double? = null,

    @Column(name = "opening_hours", columnDefinition = "TEXT")
    val openingHours: String? = null,

    @Column(columnDefinition = "TEXT")
    val description: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
) 