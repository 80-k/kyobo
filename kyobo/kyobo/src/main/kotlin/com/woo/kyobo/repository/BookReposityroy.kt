package com.woo.kyobo.repository

import com.woo.kyobo.domain.entities.BookEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface BookRepository: JpaRepository<BookEntity, String> {
//    fun findByAuthorEntityId(id: Long): List<BookEntity>
    // 엔티티에 정의된 author 필드의 id를 찾도록 수정
    fun findByAuthorId(id: Long): List<BookEntity>
}