package com.woo.kyobo.services.impl

import com.woo.kyobo.domain.AuthorUpdateRequest
import com.woo.kyobo.domain.entities.AuthorEntity
import com.woo.kyobo.repository.AuthorRepository
import com.woo.kyobo.services.AuthorService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthorServiceImpl(private val authorRepository: AuthorRepository): AuthorService {
    override fun create(authorEntity: AuthorEntity): AuthorEntity {
        require(null == authorEntity.id)
        return authorRepository.save(authorEntity)
    }

    override fun list(): List<AuthorEntity> {
        return authorRepository.findAll()
    }

    override fun get(id: Long): AuthorEntity? {
        return authorRepository.findByIdOrNull(id)
    }

    @Transactional
    override fun fullUpdate(id: Long, author: AuthorEntity): AuthorEntity {
        check(authorRepository.existsById(id))
        val normalisedAuthor = author.copy(id = id)
        return authorRepository.save(normalisedAuthor)
    }

    @Transactional
    override fun partialUpdate(id: Long, author: AuthorUpdateRequest): AuthorEntity {
        val existingAuthor = authorRepository.findByIdOrNull(id)
        checkNotNull(existingAuthor)

        val updatedAuthor = existingAuthor.copy(
            name = author.name ?: existingAuthor.name,
            age = author.age ?: existingAuthor.age,
            description = author.description ?: existingAuthor.description,
            image = author.image ?: existingAuthor.image,
        )

        return authorRepository.save(updatedAuthor)
    }

    override fun delete(id: Long) {
        authorRepository.deleteById(id)
    }
}