package com.woo.kyobo.domain.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity()
@Table(name = "books")
data class BookEntity (
    @Id
    @Column(name="isbn")
    var isbn: String,

    @Column(name="title")
    var title: String,

    @Column(name = "description", length = 1000)  // 길이를 충분히 늘림
    var description: String,

    @Column(name="image")
    var image: String,

    @ManyToOne(cascade = [CascadeType.DETACH])
    @JoinColumn(name = "author_id")
    var author: AuthorEntity
) {}