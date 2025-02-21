package com.woo.kyobo.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.woo.kyobo.domain.BookSummary
import com.woo.kyobo.domain.BookUpdateRequest
import com.woo.kyobo.domain.entities.BookEntity
import com.woo.kyobo.services.AuthorService
import com.woo.kyobo.services.BookService
import com.woo.kyobo.testAuthorEntityA
import com.woo.kyobo.testAuthorSummaryDtoA
import com.woo.kyobo.testBookEntityA
import com.woo.kyobo.testBookSummaryDtoA
import io.mockk.every
import io.mockk.verify
import org.hamcrest.core.IsEqual
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.web.servlet.*
import org.springframework.test.web.servlet.result.StatusResultMatchersDsl
import toAuthorSummaryDto
import toBookSummary
import toBookSummaryDto
import toBookUpdateRequestDto
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
class BooksControllerTest @Autowired constructor(
    private val mockMvc: MockMvc,
    @MockkBean val bookService: BookService,
    @MockkBean val authorService: AuthorService
){
    val objectMapper = ObjectMapper()

    private fun assertThatUserCreatedUpdated(isCreated: Boolean, statusCodeAssertion: StatusResultMatchersDsl.() -> Unit){
        val isbn = "987-654"
        val author = testAuthorEntityA(id = 1)
        val savedBook = testBookEntityA(isbn, author)

        val authorSummaryDto = testAuthorSummaryDtoA(id = 1).toAuthorSummaryDto()
        val bookSummaryDto = testBookSummaryDtoA(isbn = isbn, authorSummaryDto)

        every {
            bookService.createUpdate(isbn, any())
        } answers {
            Pair(savedBook, isCreated)
        }

        mockMvc.put("/v1/books/${isbn}"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bookSummaryDto)
        }.andExpect {
            status { statusCodeAssertion() }
        }
    }

    @Test
    fun `test that readManyBooks returns a list of books`(){
        val author = testAuthorEntityA(id = 999L)
        val isbnA = "123"
        val bookA = testBookEntityA(isbn = isbnA, author = author)
        val isbnB = "456"
        val bookB = testBookEntityA(isbn = isbnB, author = author)

        val listBook= mutableListOf(bookA, bookB)

        every {
            bookService.list()
        } returns (
            listBook
        )

        mockMvc.get("/v1/books"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$[0].isbn", IsEqual.equalTo(bookA.isbn)) }
            content { jsonPath("$[1].isbn", IsEqual.equalTo(bookB.isbn)) }
        }
    }

    @Test
    fun `test creating a book with HTTP 201 by create or update`(){
        assertThatUserCreatedUpdated(true){ isCreated() }
    }

    @Test
    fun `test to fail creating a book with HTTP 500 by create or update, since author has NO ID`(){
        val isbn = "987-654"
        val author = testAuthorEntityA()
        val savedBook = testBookEntityA(isbn, author)

        val authorSummaryDto = testAuthorSummaryDtoA(id = 1).toAuthorSummaryDto()
        val bookSummaryDto = testBookSummaryDtoA(isbn = isbn, authorSummaryDto)

        every {
            bookService.createUpdate(isbn, any())
        } answers {
            Pair(savedBook, true)
        }

        mockMvc.put("/v1/books/${isbn}"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bookSummaryDto)
        }.andExpect {
            status { isInternalServerError() }
        }
    }

    @Test
    fun `test returns empty list when NO match with author ID`(){
        every {
            bookService.list(authorId = any())
        } answers {
            emptyList()
        }

        mockMvc.get("/v1/books?author=999"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { json("[]") }
        }
    }

    @Test
    fun `test returns list of books, which matches author ID`() {
        // Given
        val authorId = 999L
        val testAuthor = testAuthorEntityA(id = authorId)

        val isbnA = "1234"
        val isbnB = "5678"
        val bookA = testBookEntityA(isbn = isbnA, author = testAuthor)
        val bookB = testBookEntityA(isbn = isbnB, author = testAuthor)
        val books = listOf(bookA, bookB)

        // Mock book listing
        every {
            bookService.list(authorId = authorId)
        } returns books

        // When & Then
        mockMvc.get("/v1/books?author=$authorId") {
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$[0].isbn", IsEqual.equalTo(isbnA)) }
            content { jsonPath("$[1].isbn", IsEqual.equalTo(bookB.isbn)) }
        }

        // Verify
        verify {
            bookService.list(authorId = authorId)
        }
    }

    @Test
    fun `test on readOneBook returns HTTP 404 when NO book found with a ISBN`(){
        every { bookService.get(isbn = any()) } returns null

        mockMvc.get("/v1/books/999"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

        verify { bookService.get(isbn = any()) }
    }

    @Test
    fun `test on readOneBook returns a book of ISBN with HTTP 200`(){
        val isbn = "1234"
        val authorId = 999L

        every { bookService.get(isbn = any()) } answers {
            testBookEntityA(isbn, testAuthorEntityA(id = authorId))
        }

        mockMvc.get("/v1/books/999"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.isbn", IsEqual.equalTo(isbn)) }
            content { jsonPath("$.author.id", IsEqual.equalTo(authorId.toInt())) }
        }

        verify { bookService.get(isbn = any()) }
    }

    @Test
    fun `test partial-update book with invalid ISBN returns HTTP 400 on IllegalStateException`(){
        val isbn = "1234"
        val bookUpdateRequest = BookUpdateRequest(
            title = "Test Partial Title Update"
        )
        every { bookService.partialUpdate(isbn, bookUpdateRequest) } throws IllegalStateException()

        mockMvc.patch("/v1/books/${isbn}"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bookUpdateRequest.toBookUpdateRequestDto())
        }.andExpect {
            status { isBadRequest() }
        }

        verify { bookService.partialUpdate(isbn, bookUpdateRequest) }
    }

    @Test
    fun `test partial-update book with ISBN returns updated book with HTTP 200`(){
        val isbn = "1234"
        val author = testAuthorEntityA(id = 1)
        val existingBook = testBookEntityA(isbn = isbn, author = author)
        val bookUpdateRequest = BookUpdateRequest(
            title = "Test Partial Title Update"
        )
        val updatedBook = existingBook.copy(
            title = bookUpdateRequest.title ?: existingBook.title,
            description = bookUpdateRequest.description ?: existingBook.description,
            image = bookUpdateRequest.image ?: existingBook.image
        )

        every { bookService.partialUpdate(isbn, bookUpdateRequest) } answers {
            updatedBook
        }

        mockMvc.patch("/v1/books/${isbn}"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(bookUpdateRequest.toBookUpdateRequestDto())
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.isbn", IsEqual.equalTo(isbn)) }
            content { jsonPath("$.title", IsEqual.equalTo(bookUpdateRequest.title)) }
            content { jsonPath("$.description", IsEqual.equalTo(existingBook.description)) }
            content { jsonPath("$.image", IsEqual.equalTo(existingBook.image)) }
        }

        verify { bookService.partialUpdate(isbn, bookUpdateRequest) }
    }

    @Test
    fun `test delete a book existing in DB`(){
        val isbn = "1234"
        every { bookService.delete(isbn = isbn) } returns Unit

        mockMvc.delete("/v1/books/${isbn}"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }

        verify { bookService.delete(isbn) }

    }
}