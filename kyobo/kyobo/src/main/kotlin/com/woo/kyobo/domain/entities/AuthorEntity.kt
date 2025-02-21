package com.woo.kyobo.domain.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Id
import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.OneToMany

@Entity
@Table(name="authors")
data class AuthorEntity(
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "author_id_seq")
    val id: Long?,

    @Column(name="name")
    val name: String,

    @Column(name="age")
    val age: Int,

    @Column(name="description")
    val description: String,

    @Column(name="image")
    val image: String,

    @OneToMany(mappedBy = "author", cascade = [CascadeType.REMOVE])
    val bookEntities: List<BookEntity> = emptyList()
)