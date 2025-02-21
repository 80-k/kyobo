package com.woo.kyobo.services

import com.woo.kyobo.domain.BookSummary
import com.woo.kyobo.domain.BookUpdateRequest
import com.woo.kyobo.domain.dto.BookSummaryDto
import com.woo.kyobo.domain.entities.BookEntity

interface BookService {
    fun get(isbn: String): BookEntity?

    fun partialUpdate(isbn: String, bookupdateRequest: BookUpdateRequest): BookEntity

    fun createUpdate(
        isbn: String,
        bookSummary: BookSummary
    ): Pair<BookEntity, Boolean>

    fun list(authorId: Long? = null): List<BookEntity>

    fun delete(isbn: String)
}