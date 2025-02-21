package com.woo.kyobo.controllers

import com.woo.kyobo.domain.BookUpdateRequest
import com.woo.kyobo.domain.dto.BookSummaryDto
import com.woo.kyobo.domain.dto.BookUpdateRequestDto
import com.woo.kyobo.exceptions.InvalidAuthorException
import com.woo.kyobo.services.BookService
import jakarta.websocket.server.PathParam
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import toBookSummary
import toBookSummaryDto
import toBookUpdateRequest

@RestController
@RequestMapping(path = ["/v1/books"])
class BooksController (
    val bookService: BookService
){
    @PutMapping(path = ["/{isbn}"])
    fun createFullUpdateBook(
        @PathVariable("isbn") isbn: String,
        @RequestBody book: BookSummaryDto
    ): ResponseEntity<BookSummaryDto>{
        return try {
            val (savedBook, isCreated) = bookService.createUpdate(isbn, book.toBookSummary())
            val responseCode = if(isCreated) HttpStatus.CREATED else HttpStatus.OK

            ResponseEntity(savedBook.toBookSummaryDto(), responseCode)
        }
        catch (e: InvalidAuthorException){
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        catch (e: IllegalStateException){
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping(path = ["/{isbn}"])
    fun deleteBook(
        @PathVariable("isbn") isbn: String,
    ): ResponseEntity<Unit>{
        bookService.delete(isbn = isbn)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping(path = ["/{isbn}"])
    fun readOneBook(
        @PathVariable("isbn") isbn: String,
    ): ResponseEntity<BookSummaryDto>{
        return bookService.get(isbn)?.let { ResponseEntity(it.toBookSummaryDto(), HttpStatus.OK)} ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @GetMapping()
    fun readManyBooks(
        @RequestParam("author") authorId: Long?
    ): List<BookSummaryDto>{
        return bookService.list(authorId = authorId).map { it.toBookSummaryDto() }
    }

    @PatchMapping(path = ["/{isbn}"])
    fun partialUpdateBook(
        @PathVariable("isbn") isbn: String,
        @RequestBody bookUpdateRequestDto: BookUpdateRequestDto
    ): ResponseEntity<BookSummaryDto>{
        try {
            val updatedBook = bookService.partialUpdate(isbn = isbn, bookupdateRequest = bookUpdateRequestDto.toBookUpdateRequest())
            return ResponseEntity(updatedBook.toBookSummaryDto(), HttpStatus.OK)
        } catch (e: IllegalStateException){
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }
}