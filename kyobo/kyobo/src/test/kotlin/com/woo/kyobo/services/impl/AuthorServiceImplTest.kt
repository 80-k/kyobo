package com.woo.kyobo.services.impl

import com.woo.kyobo.domain.AuthorUpdateRequest
import com.woo.kyobo.domain.entities.AuthorEntity
import com.woo.kyobo.repository.AuthorRepository
import com.woo.kyobo.testAuthorEntityA
import com.woo.kyobo.testAuthorEntityB
import com.woo.kyobo.testAuthorUpdateRequestA
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
class AuthorServiceImplTest @Autowired constructor (
    private val underTest: AuthorServiceImpl,
    private val authorRepository: AuthorRepository
){
   @Test
   fun `test Author to DB`(){
       val savedAuthor = underTest.create(testAuthorEntityA())
       Assertions.assertThat(savedAuthor.id).isNotNull()

       val recalledAuthor = authorRepository.findByIdOrNull(savedAuthor.id!!)
       Assertions.assertThat(recalledAuthor).isNotNull()

       Assertions.assertThat(recalledAuthor!!).isEqualTo(testAuthorEntityA(recalledAuthor.id))
   }

    @Test
    fun `test Empty List when NO authors in DB`(){
        val result = underTest.list()
        Assertions.assertThat(result).isEmpty()
    }

    @Test
    fun `test Author List from DB`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        val expected = listOf(savedAuthor)
//        val expected = emptyList<AuthorEntity>()
        val result = underTest.list()
        Assertions.assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `test returns null when no author in DB`(){
        val result = underTest.get(999)
        Assertions.assertThat(result).isNull()
    }

    @Test
    fun `test returns an author with ID from DB`(){
        val savedAuthor = authorRepository.save(testAuthorEntityA())
        val result = underTest.get(savedAuthor.id!!)
        Assertions.assertThat(result).isEqualTo(savedAuthor)
    }

    @Test
    fun `test throws an IllegalArgumentException while an author with id`(){
        assertThrows<IllegalArgumentException> {
            val existingAuthor = testAuthorEntityA(id = 999)
            underTest.create(existingAuthor)
        }
    }

    @Test
    fun `test that successfully full-update an author with ID in DB`(){
        val existingAuthor = authorRepository.save(testAuthorEntityA())
        val existingAuthorId = existingAuthor.id!!
        val updatedAuthor = existingAuthor.copy(id = existingAuthorId, age = 100)
        val result = underTest.fullUpdate(existingAuthorId, updatedAuthor)
        Assertions.assertThat(result).isEqualTo(updatedAuthor)

        val retrievedAuthor = authorRepository.findByIdOrNull(existingAuthorId)
        Assertions.assertThat(retrievedAuthor).isNotNull()
        Assertions.assertThat(retrievedAuthor).isEqualTo(updatedAuthor)
    }

    @Test
    fun `test that throws IllegalStateException while full-update since no author with ID in DB`(){
        assertThrows<IllegalStateException> {
            val nonExistingAuthorId = 999L
            val updatedAuthor = testAuthorEntityB(id = nonExistingAuthorId)
            underTest.fullUpdate(nonExistingAuthorId, updatedAuthor)
        }
    }

    @Test
    fun `test that throws IllegalStateException while partial-update since no author with ID in DB`(){
        assertThrows<IllegalStateException> {
            val nonExistingAuthorId = 999L
            val updatedRequest = testAuthorUpdateRequestA(id = nonExistingAuthorId)
            underTest.partialUpdate(nonExistingAuthorId, updatedRequest)
        }
    }

    private fun assertThatAuthorPartialUpdateIsUpdated(
        existingAuthor: AuthorEntity,
        expectedAuthor: AuthorEntity,
        authorUpdateRequest: AuthorUpdateRequest
    ){
        val savedExistingAuthor = authorRepository.save(existingAuthor)
        val existingAuthorId = savedExistingAuthor.id!!

        val updatedAuthor = underTest.partialUpdate(
            existingAuthorId,
            authorUpdateRequest
        )
        val expected = expectedAuthor.copy(id = existingAuthorId)
        Assertions.assertThat(updatedAuthor).isEqualTo(expected)

        val retrievedAuthor = authorRepository.findByIdOrNull(existingAuthorId)
        Assertions.assertThat(retrievedAuthor).isNotNull()
        Assertions.assertThat(retrievedAuthor).isEqualTo(expected)
    }

    @Test
    fun `test that successfully partial-update name of an author`(){
        val newName = "New Author Name"
        val existingAuthor = testAuthorEntityA()
        val expectedAuthor = existingAuthor.copy(name = newName)
        val authorUpdateRequest = AuthorUpdateRequest(name = newName)

        assertThatAuthorPartialUpdateIsUpdated(
            existingAuthor = existingAuthor,
            expectedAuthor = expectedAuthor,
            authorUpdateRequest = authorUpdateRequest
        )
    }

    @Test
    fun `test that successfully partial-update age of an author`(){
        val age = 100
        val existingAuthor = testAuthorEntityA()
        val expectedAuthor = existingAuthor.copy(age = age)
        val authorUpdateRequest = AuthorUpdateRequest(age = age)

        assertThatAuthorPartialUpdateIsUpdated(
            existingAuthor = existingAuthor,
            expectedAuthor = expectedAuthor,
            authorUpdateRequest = authorUpdateRequest
        )
    }

    @Test
    fun `test that successfully partial-update description of an author`(){
        val newDesc = "New Description"
        val existingAuthor = testAuthorEntityA()
        val expectedAuthor = existingAuthor.copy(description = newDesc)
        val authorUpdateRequest = AuthorUpdateRequest(description = newDesc)

        assertThatAuthorPartialUpdateIsUpdated(
            existingAuthor = existingAuthor,
            expectedAuthor = expectedAuthor,
            authorUpdateRequest = authorUpdateRequest
        )
    }

    @Test
    fun `test that successfully partial-update image of an author`(){
        val newImg = "New-image.jpeg"
        val existingAuthor = testAuthorEntityA()
        val expectedAuthor = existingAuthor.copy(image = newImg)
        val authorUpdateRequest = AuthorUpdateRequest(image = newImg)

        assertThatAuthorPartialUpdateIsUpdated(
            existingAuthor = existingAuthor,
            expectedAuthor = expectedAuthor,
            authorUpdateRequest = authorUpdateRequest
        )
    }

    @Test
    fun `test that is NOT able to delete a deleted author from DB`(){
        val existingAuthor = authorRepository.save(testAuthorEntityA())
        val existingAuthorId = existingAuthor.id!!
        underTest.delete(existingAuthorId)

        Assertions.assertThat(authorRepository.existsById(existingAuthorId)).isFalse()

    }

    @Test
    fun `test that is NOT able to delete an author with wrong id from DB`(){
        val wrongAuthorId = 999L
        underTest.delete(wrongAuthorId)

        Assertions.assertThat(authorRepository.existsById(wrongAuthorId)).isFalse()
    }
}