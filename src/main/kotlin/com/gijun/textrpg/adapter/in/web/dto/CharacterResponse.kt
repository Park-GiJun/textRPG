package com.gijun.textrpg.adapter.`in`.web.dto

data class CharacterResponse(
    val id: String,
    val name: String,
    val level: Int,
    val health: HealthResponse,
    val stats: StatsResponse,
    val createdAt: String,
    val updatedAt: String
)

data class HealthResponse(
    val current: Int,
    val max: Int,
    val percentage: Float
)

data class StatsResponse(
    val strength: Int,
    val dexterity: Int,
    val intelligence: Int,
    val luck: Int,
    val totalPower: Int
)
