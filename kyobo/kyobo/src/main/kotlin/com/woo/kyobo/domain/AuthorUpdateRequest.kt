package com.woo.kyobo.domain

data class AuthorUpdateRequest(
    val id: Long? = null,
    val name: String? = null,
    val age: Int? = null,
    val description: String? = null,
    val image: String? = null
)
