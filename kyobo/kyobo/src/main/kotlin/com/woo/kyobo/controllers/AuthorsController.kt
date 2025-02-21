package com.woo.kyobo.controllers

import com.woo.kyobo.domain.AuthorUpdateRequest
import com.woo.kyobo.domain.dto.AuthorDto
import com.woo.kyobo.domain.dto.AuthorUpdateRequestDto
import com.woo.kyobo.services.AuthorService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import toAuthorDto
import toAuthorEntity
import toAuthorUpdateRequest

@RestController
@RequestMapping(path = ["/v1/authors"])
class AuthorsController (private val authorService: AuthorService){

    @PostMapping
    fun createAuthor(@RequestBody author: AuthorDto): ResponseEntity<AuthorDto>{
        return try {
            val createdAuthor = authorService.create(author.toAuthorEntity()).toAuthorDto()
            ResponseEntity(createdAuthor, HttpStatus.CREATED)
        } catch (ex: IllegalArgumentException){
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping
    fun readManyAuthor(): ResponseEntity<List<AuthorDto>>{
        return ResponseEntity(authorService.list().map { it.toAuthorDto() }, HttpStatus.OK)
    }

    @GetMapping(path = ["/{id}"])
    fun readOneAuthor(
        @PathVariable("id") id: Long
    ): ResponseEntity<AuthorDto>{
        val foundAuthor = authorService.get(id)?.toAuthorDto()
        return foundAuthor?.let {
            ResponseEntity(it, HttpStatus.OK)
        } ?: ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @PutMapping(path = ["/{id}"])
    fun fullUpdateAuthor(
       @PathVariable("id")  id: Long,
       @RequestBody body: AuthorDto
    ): ResponseEntity<AuthorDto>{
        return try {
            val updatedAuthor = authorService.fullUpdate(id, body.toAuthorEntity())
            ResponseEntity(updatedAuthor.toAuthorDto(), HttpStatus.OK)
        }
        catch (ex: IllegalStateException){
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @PatchMapping(path = ["/{id}"])
    fun partialUpdateAuthor(
        @PathVariable("id")  id: Long,
        @RequestBody body: AuthorUpdateRequestDto
    ): ResponseEntity<AuthorDto>{
        return try {
            val updatedAuthor = authorService.partialUpdate(id, body.toAuthorUpdateRequest())
            ResponseEntity(updatedAuthor.toAuthorDto(), HttpStatus.OK)
        }
        catch (ex: IllegalStateException){
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteAuthor(
        @PathVariable("id")  id: Long,
    ): ResponseEntity<Unit>{
        return try {
            authorService.delete(id)
            return ResponseEntity(HttpStatus.NO_CONTENT)
        }
        catch (ex: IllegalStateException){
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }
}