package com.gijun.textrpg.application.service

// Character Service Exceptions
sealed class CharacterServiceException(message: String) : RuntimeException(message)

class CharacterNotFoundException(message: String) : CharacterServiceException(message)
class CharacterAlreadyExistsException(message: String) : CharacterServiceException(message)
class InvalidCharacterDataException(message: String) : CharacterServiceException(message)
