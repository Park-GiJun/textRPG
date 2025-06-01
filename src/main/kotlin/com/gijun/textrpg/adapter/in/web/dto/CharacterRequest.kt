package com.gijun.textrpg.adapter.`in`.web.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class CreateCharacterRequest(
    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(max = 50, message = "Name cannot exceed 50 characters")
    @field:Pattern(
        regexp = "^[^<>\"'&]*$",
        message = "Name contains invalid characters"
    )
    val name: String,
    
    @field:Positive(message = "Strength must be positive")
    val strength: Int? = null,
    
    @field:Positive(message = "Dexterity must be positive")
    val dexterity: Int? = null,
    
    @field:Positive(message = "Intelligence must be positive")
    val intelligence: Int? = null,
    
    @field:Positive(message = "Luck must be positive")
    val luck: Int? = null
)

data class UpdateCharacterRequest(
    @field:Size(max = 50, message = "Name cannot exceed 50 characters")
    @field:Pattern(
        regexp = "^[^<>\"'&]*$",
        message = "Name contains invalid characters"
    )
    val name: String? = null
)
