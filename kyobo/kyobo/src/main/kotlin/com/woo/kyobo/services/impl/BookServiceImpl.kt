package com.woo.kyobo.services.impl

import com.woo.kyobo.domain.BookSummary
import com.woo.kyobo.domain.BookUpdateRequest
import com.woo.kyobo.domain.entities.BookEntity
import com.woo.kyobo.repository.AuthorRepository
import com.woo.kyobo.repository.BookRepository
import com.woo.kyobo.services.BookService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import toBookEntity

@Service
class BookServiceImpl (
    val bookRepository: BookRepository,
    val authorRepository: AuthorRepository
): BookService {
    override fun get(isbn: String): BookEntity? {
        return bookRepository.findByIdOrNull(isbn)
    }

    override fun partialUpdate(isbn: String, bookupdateRequest: BookUpdateRequest): BookEntity {
       val existingBook = bookRepository.findByIdOrNull(isbn)
        checkNotNull(existingBook)
        val updatedBook = existingBook.copy(
            title = bookupdateRequest.title ?: existingBook.title,
            description = bookupdateRequest.title ?: existingBook.description,
            image = bookupdateRequest.title ?: existingBook.image,
        )

        return bookRepository.save(updatedBook)
    }


    override fun list(authorId: Long?): List<BookEntity> {
        return authorId?.let {
            bookRepository.findByAuthorId(it)
        } ?: bookRepository.findAll()
    }

    override fun delete(isbn: String) {
        return bookRepository.deleteById(isbn)
    }

    @Transactional
    override fun createUpdate(isbn: String, bookSummary: BookSummary): Pair<BookEntity, Boolean> {
        val normalisedBook = bookSummary.copy(isbn = isbn)
        val isExists = bookRepository.existsById(normalisedBook.isbn)

        val author = authorRepository.findByIdOrNull(normalisedBook.author.id)
        checkNotNull(author)

        val savedBook = bookRepository.save(normalisedBook.toBookEntity(author))
        return Pair(savedBook, !isExists)
    }
}