package com.library.common.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/test")
class TestController {

    @GetMapping("/health")
    fun health(): Map<String, Any> {
        return mapOf(
            "status" to "OK",
            "message" to "Library Management System is running!",
            "timestamp" to System.currentTimeMillis()
        )
    }

    @GetMapping("/database")
    fun databaseTest(): Map<String, Any> {
        return mapOf(
            "status" to "Database connection test",
            "message" to "Database connection will be tested here"
        )
    }
} 