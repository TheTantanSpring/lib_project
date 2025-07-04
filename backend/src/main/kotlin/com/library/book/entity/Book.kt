package com.library.book.entity

import com.library.library.entity.Library
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "library_id", nullable = false)
    val library: Library,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val author: String,

    @Column(length = 20)
    val isbn: String? = null,

    val publisher: String? = null,

    @Column(name = "publication_year")
    val publicationYear: Int? = null,

    val category: String? = null,

    @Column(name = "total_copies", nullable = false)
    val totalCopies: Int = 1,

    @Column(name = "available_copies", nullable = false)
    val availableCopies: Int = 1,

    @Enumerated(EnumType.STRING)
    val status: BookStatus = BookStatus.AVAILABLE,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class BookStatus {
    AVAILABLE,
    UNAVAILABLE,
    MAINTENANCE
} 