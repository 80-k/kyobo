package com.woo.kyobo.repository

import com.woo.kyobo.domain.entities.AuthorEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository: JpaRepository<AuthorEntity, Long?> {
}