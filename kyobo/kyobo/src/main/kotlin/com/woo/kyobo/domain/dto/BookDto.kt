package com.woo.kyobo.domain.dto

import com.woo.kyobo.domain.entities.AuthorEntity

data class BookDto (
    val isbn: String,
    val title: String,
    val description: String,
    val image: String,
    val author: AuthorDto
)