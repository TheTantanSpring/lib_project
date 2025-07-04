package com.library

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LibraryManagementApplication

fun main(args: Array<String>) {
    runApplication<LibraryManagementApplication>(*args)
} 