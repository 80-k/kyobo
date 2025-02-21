package com.woo.kyobo.services

import com.woo.kyobo.domain.AuthorUpdateRequest
import com.woo.kyobo.domain.entities.AuthorEntity

interface AuthorService {
    fun create(authorEntity: AuthorEntity): AuthorEntity

    fun list(): List<AuthorEntity>

    fun get(id: Long): AuthorEntity?

    fun fullUpdate(id: Long, author: AuthorEntity): AuthorEntity

    fun partialUpdate(id: Long, author: AuthorUpdateRequest): AuthorEntity

    fun delete(id: Long)
}