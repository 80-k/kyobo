package com.woo.kyobo.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.jayway.jsonpath.TypeRef
import com.ninjasquad.springmockk.MockkBean
import com.woo.kyobo.domain.dto.AuthorDto
import com.woo.kyobo.domain.entities.AuthorEntity
import com.woo.kyobo.services.AuthorService
import com.woo.kyobo.testAuthorDtoA
import com.woo.kyobo.testAuthorEntityA
import com.woo.kyobo.testAuthorUpdateRequestDtoA
import io.mockk.every
import io.mockk.verify
import org.hamcrest.core.IsEqual
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.*
import toAuthorEntity

private const val AUTHORS_BASE_URL = "/v1/authors"

// integrate test, mockMVC
@SpringBootTest
@AutoConfigureMockMvc
class AuthorsControllerTest @Autowired constructor(
    val mockMvc: MockMvc,
    @MockkBean val authorService: AuthorService
) {

    val objectMapper = ObjectMapper()

    @BeforeEach
    fun beforeEach(){
        every {
            authorService.create(any())
        } answers {
            firstArg()
        }
    }

    @Test
    fun `test Author`(){

        mockMvc.post(AUTHORS_BASE_URL){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                testAuthorDtoA()
            )
        }

        val expected = testAuthorDtoA().toAuthorEntity()

        verify { authorService.create(expected) }
    }

    @Test
    fun `test that create Author returns a HTTP 201 status on a author creation`(){
        mockMvc.post(AUTHORS_BASE_URL){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                testAuthorDtoA()
            )
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `test that IllegalArgumentException thrown with HTTP 400 while creating Author`(){
        every {
           authorService.create(any())
        } throws(IllegalArgumentException())

        mockMvc.post(AUTHORS_BASE_URL){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(
                testAuthorDtoA()
            )
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `test author is empty list with HTTP 200`(){
        every {
            authorService.list()
        }answers {
            emptyList()
        }

        mockMvc.get(AUTHORS_BASE_URL){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { json("[]") }
        }
    }

    @Test
    fun `test author list with HTTP 200`(){
        every {
            authorService.list()
        }answers {
            listOf(testAuthorEntityA(1))
        }

        mockMvc.get(AUTHORS_BASE_URL){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$[0].id", IsEqual.equalTo(1) ) }
            content { jsonPath("$[0].name", IsEqual.equalTo("John Doe") ) }
        }
    }

    @Test
    fun `test NO author with id in DB`(){
        every {
            authorService.get(any())
        } answers {
            null
        }

        mockMvc.get("${AUTHORS_BASE_URL}/10"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `test returns An author with id in DB`(){
        // 3. toLong() 함수 사용
        val authorId = 999L

        every {
            authorService.get(any())
        } answers {
            testAuthorEntityA(id = authorId)
        }

        mockMvc.get("${AUTHORS_BASE_URL}/${authorId}"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.id", IsEqual.equalTo(authorId.toInt())) }
            content { jsonPath("$.name", IsEqual.equalTo("John Doe") ) }
        }
    }

    @Test
    fun `test that full-update an author with HTTP 200`(){
        every {
           authorService.fullUpdate(any(), any())
        } answers {
            secondArg<AuthorEntity>()
        }

        val authorId = 999L

        mockMvc.put("${AUTHORS_BASE_URL}/${authorId}"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testAuthorDtoA(id = authorId))
        }.andExpect {
            status { isOk() }
            content { jsonPath("$.id", IsEqual.equalTo(authorId.toInt())) }
            content { jsonPath("$.name", IsEqual.equalTo("John Doe")) }
        }
    }

    @Test
    fun `test throws IllegalStateException with HTTP 400 while full-updating`(){
        every {
            authorService.fullUpdate(any(), any())
        } throws ( IllegalStateException() )

        val authorId: Long = 999

        mockMvc.put("${AUTHORS_BASE_URL}/${authorId}"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testAuthorDtoA(id = authorId))
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `test that IllegalStateException with HTTP 400 while partial-updating an author`(){
        val authorId: Long = 999

        every {
            authorService.partialUpdate(any(), any())
        } throws   (
            IllegalStateException()
        )

        mockMvc.patch("${AUTHORS_BASE_URL}/${authorId}"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testAuthorUpdateRequestDtoA(id = authorId))
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `test partial-update an author with HTTP 200`(){
        val authorId: Long = 999
        every {
            authorService.partialUpdate(any(), any())
        } answers {
            testAuthorEntityA(id = authorId)
        }

        mockMvc.patch("${AUTHORS_BASE_URL}/${authorId}"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(testAuthorUpdateRequestDtoA(id = authorId))
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `test to successfully deleting an author with HTTP 204`(){
        val authorId: Long = 999
        every {
            authorService.delete(any())
        } answers {}

        mockMvc.delete("${AUTHORS_BASE_URL}/${authorId}"){
            contentType = MediaType.APPLICATION_JSON
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
    }


}