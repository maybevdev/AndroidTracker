package com.example.progr3ssapp

import kotlinx.serialization.Serializable

@Serializable
object Login

@Serializable
object MainGraph   // groups the screens that show the bottom bar

@Serializable
object Home

@Serializable
data class Profile(
    val userId: String = "guest",
    val userName: String = "Guest"
)