package com.gijun.textrpg.adapter.`in`.messaging

import com.gijun.textrpg.domain.character.CharacterEvent
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class CharacterEventListener {
    
    private val logger = LoggerFactory.getLogger(CharacterEventListener::class.java)
    
    @KafkaListener(topics = ["character-events"], groupId = "character-event-group")
    fun handleCharacterEvent(event: CharacterEvent) {
        when (event) {
            is CharacterEvent.CharacterCreated -> handleCharacterCreated(event)
            is CharacterEvent.CharacterNameChanged -> handleCharacterNameChanged(event)
            is CharacterEvent.CharacterDied -> handleCharacterDied(event)
            is CharacterEvent.CharacterDeleted -> handleCharacterDeleted(event)
        }
    }
    
    private fun handleCharacterCreated(event: CharacterEvent.CharacterCreated) {
        logger.info("📢 새 캐릭터 생성됨: {} (ID: {})", event.name, event.characterId)
        
        // 여기서 추가적인 비즈니스 로직 수행 가능
        // 예: 환영 이메일 발송, 통계 업데이트, 다른 서비스 알림 등
        
        // 예시: 새 캐릭터 환영 로직
        logCharacterWelcome(event.name, event.characterId)
    }
    
    private fun handleCharacterNameChanged(event: CharacterEvent.CharacterNameChanged) {
        logger.info("📝 캐릭터 이름 변경: '{}' → '{}' (ID: {})", 
            event.oldName, event.newName, event.characterId)
        
        // 예: 이름 변경 히스토리 저장, 친구 목록 업데이트 등
    }
    
    private fun handleCharacterDied(event: CharacterEvent.CharacterDied) {
        logger.info("💀 캐릭터 사망: ID {}", event.characterId)
        
        // 예: 사망 통계 업데이트, 부활 아이템 제안 등
    }
    
    private fun handleCharacterDeleted(event: CharacterEvent.CharacterDeleted) {
        logger.info("🗑️ 캐릭터 삭제됨: ID {}", event.characterId)
        
        // 예: 관련 데이터 정리, 통계 업데이트 등
        cleanupCharacterData(event.characterId)
    }
    
    private fun logCharacterWelcome(name: String, characterId: String) {
        logger.info("🎉 환영합니다, {}님! 모험을 시작해보세요! (Character ID: {})", name, characterId)
        
        // 실제로는 여기서 다른 서비스 호출, 데이터베이스 업데이트 등을 수행
    }
    
    private fun cleanupCharacterData(characterId: String) {
        logger.debug("🧹 캐릭터 관련 데이터 정리 중: {}", characterId)
        
        // 실제로는 여기서 관련 테이블 정리, 파일 삭제 등을 수행
    }
}
