package com.woo.kyobo

import com.woo.kyobo.domain.AuthorSummary
import com.woo.kyobo.domain.AuthorUpdateRequest
import com.woo.kyobo.domain.BookSummary
import com.woo.kyobo.domain.dto.AuthorDto
import com.woo.kyobo.domain.dto.AuthorSummaryDto
import com.woo.kyobo.domain.dto.AuthorUpdateRequestDto
import com.woo.kyobo.domain.dto.BookSummaryDto
import com.woo.kyobo.domain.entities.AuthorEntity
import com.woo.kyobo.domain.entities.BookEntity

fun testAuthorDtoA(id: Long? = null) = AuthorDto(
    id =id,
    name = "John Doe",
    age=30,
    description = "some description",
    image = "author-image.jpeg"
)

fun testAuthorEntityA(id: Long? = null) = AuthorEntity(
    id=id,
    name = "John Doe",
    age=30,
    description = "some description",
    image = "author-image.jpeg"
)

fun testAuthorEntityB(id: Long? = null) = AuthorEntity(
    id=id,
    name = "Don Joe",
    age=65,
    description = "some description",
    image = "author-image.jpeg"
)

fun testAuthorUpdateRequestDtoA(id: Long? = null) = AuthorUpdateRequestDto(
    id=id,
    name = "Don Joe",
    age=65,
    description = "some description",
    image = "author-image.jpeg"
)

fun testAuthorUpdateRequestA(id: Long? = null) = AuthorUpdateRequest(
    id=id,
    name = "Don Joe",
    age=65,
    description = "some description",
    image = "author-image.jpeg"
)

fun testBookEntityA(isbn: String, author: AuthorEntity) = BookEntity(
   isbn = isbn,
    title = "Test Book Title",
    description = "Test Description",
    image = "book-image.jpeg",
    author = author
)

fun testAuthorSummaryDtoA(id: Long) = AuthorEntity(
    id = id,
    name = "Don Joe",
    age=65,
    description = "some description",
    image = "author-image.jpeg"
)

fun testBookSummaryA(isbn: String, author: AuthorSummary) = BookSummary(
    isbn = isbn,
    title = "Test Book Summary Title",
    description = "Test Description",
    image = "book-image.jpeg",
    author = author
)

fun testBookSummaryDtoA(isbn: String, author:AuthorSummaryDto) = BookSummaryDto(
    isbn = isbn,
    title = "Test Book Title",
    description = "Test Description",
    image = "book-image.jpeg",
    author = author
)