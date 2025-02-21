package com.woo.kyobo.services.impl

import com.woo.kyobo.domain.AuthorUpdateRequest
import com.woo.kyobo.domain.BookUpdateRequest
import com.woo.kyobo.domain.entities.AuthorEntity
import com.woo.kyobo.repository.AuthorRepository
import com.woo.kyobo.repository.BookRepository
import com.woo.kyobo.testAuthorEntityA
import com.woo.kyobo.testAuthorEntityB
import com.woo.kyobo.testAuthorUpdateRequestA
import com.woo.kyobo.testBookEntityA
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.transaction.annotation.Transactional
import kotlin.math.exp

@SpringBootTest
@Transactional
class BookServiceImplTest @Autowired constructor (
    private val underTest: BookServiceImpl,
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository
){
   @Test
   fun `test list function, empty list since NO book in DB`(){
       val savedBook = underTest.list()
       Assertions.assertThat(savedBook).isEmpty()
   }

   @Test
   fun `test list function, list is NOT empty since books in DB`(){
       val savedAuthor = authorRepository.save(testAuthorEntityA())
       Assertions.assertThat(savedAuthor).isNotNull()
       val isbnA = "1234"
       val savedBookA = bookRepository.save(testBookEntityA(isbn = isbnA, author = savedAuthor))
       Assertions.assertThat(savedBookA).isNotNull()
       val isbnB = "5678"
       val savedBookB = bookRepository.save(testBookEntityA(isbn = isbnB, author = savedAuthor))
       Assertions.assertThat(savedBookB).isNotNull()
       val savedAllBook = listOf(savedBookA, savedBookB)

       val allBook = underTest.list()

       Assertions.assertThat(allBook).hasSize(2)
       Assertions.assertThat(allBook).isEqualTo(savedAllBook)
       Assertions.assertThat(allBook[0]).isEqualTo(savedBookA)
       Assertions.assertThat(allBook[1]).isEqualTo(savedBookB)
   }

    @Test
    fun `test returns empty list when author ID does NOT match any books`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        Assertions.assertThat(savedAuthor).isNotNull()

        val savedBook = bookRepository.save(testBookEntityA("1234", savedAuthor))
        Assertions.assertThat(savedBook).isNotNull()

        val result = underTest.list(authorId = savedAuthor.id!! + 1)
        Assertions.assertThat(result).hasSize(0)
    }

    @Test
    fun `test returns list of book, which matches with author ID`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        Assertions.assertThat(savedAuthor).isNotNull()

        val savedBook = bookRepository.save(testBookEntityA("1234", savedAuthor))
        Assertions.assertThat(savedBook).isNotNull()

        val result = underTest.list(authorId = savedAuthor.id)
        Assertions.assertThat(result).hasSize(1)
        Assertions.assertThat(result[0]).isEqualTo(savedBook)
    }

    @Test
    fun `test returns null when NO book with ISBN in DB`(){
        val result = underTest.get("1234")
        Assertions.assertThat(result).isNull()
    }

    @Test
    fun `test returns a book with ISBN from DB`(){
        val isbn = "1234"
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        Assertions.assertThat(savedAuthor).isNotNull()

        val savedBook = bookRepository.save(testBookEntityA(isbn, savedAuthor))
        Assertions.assertThat(savedBook).isNotNull()

        val result = underTest.get(isbn)
        Assertions.assertThat(result).isEqualTo(savedBook)
    }

    @Test
    fun `test returns IllegalStateException when you try to partial-update book NOT in DB`(){
        val isbn = "1234"
        val bookUpdateRequest = BookUpdateRequest(
            title = "Test Partial Updated Title"
        )
        assertThrows<IllegalStateException> {
            underTest.partialUpdate(isbn = isbn, bookupdateRequest = bookUpdateRequest)
        }
    }

    @Test
    fun `test to partially update title of book with ISBN`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        Assertions.assertThat(savedAuthor).isNotNull()

        val isbn = "1234"
        val title = "Test Partial Updated Title"
        val savedBook = bookRepository.save(testBookEntityA(isbn = isbn, author = savedAuthor))
        Assertions.assertThat(savedBook).isNotNull()

        val bookUpdateRequest = BookUpdateRequest(
            title = title
        )
        val updatedBook = underTest.partialUpdate(isbn = isbn, bookupdateRequest = bookUpdateRequest)

        Assertions.assertThat(updatedBook.isbn).isEqualTo(isbn)
        Assertions.assertThat(updatedBook.title).isEqualTo(title)
    }

    @Test
    fun `test delete a book existing in DB`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        Assertions.assertThat(savedAuthor).isNotNull()

        val isbn = "1234"
        val savedBook = bookRepository.save(testBookEntityA(isbn = isbn, author = savedAuthor))
        Assertions.assertThat(savedBook).isNotNull()

        underTest.delete(isbn = isbn)

        val result = bookRepository.findByIdOrNull(isbn)
        Assertions.assertThat(result).isNull()
    }

    @Test
    fun `test delete a book NOT existing in DB`(){
        val isbn = "1234"
        val existingBook = bookRepository.findByIdOrNull(isbn)
        Assertions.assertThat(existingBook).isNull()

        underTest.delete(isbn = isbn)

        val result = bookRepository.findByIdOrNull(isbn)
        Assertions.assertThat(result).isNull()
    }
}