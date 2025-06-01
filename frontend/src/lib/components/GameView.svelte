<script>
  import { selectedCharacter, currentPage, showNotification } from '$lib/stores';

  let currentCharacter = null;
  let gameLog = [];
  let currentStory = "깊은 숲의 입구에 서 있습니다. 앞으로는 어둠이 깔린 길이, 왼쪽으로는 맑은 개울이 흐르고 있습니다.";
  
  selectedCharacter.subscribe(char => {
    currentCharacter = char;
    if (char) {
      addToLog(`${char.name}으로 모험을 시작합니다!`);
    }
  });

  function addToLog(message) {
    gameLog = [...gameLog, `[${new Date().toLocaleTimeString()}] ${message}`];
  }

  function goForward() {
    addToLog("앞으로 전진합니다...");
    currentStory = "숲 속으로 더 깊이 들어갔습니다. 나뭇가지 사이로 무언가 움직이는 것이 보입니다.";
    addToLog("무언가 이상한 기운이 느껴집니다.");
  }

  function goLeft() {
    addToLog("개울가로 향합니다...");
    currentStory = "맑은 개울가에 도착했습니다. 물이 매우 깨끗하고 차갑습니다. 체력을 회복할 수 있을 것 같습니다.";
    addToLog("개울물을 마시며 체력을 회복합니다.");
    if (currentCharacter) {
      addToLog(`체력이 회복되었습니다! (+10 HP)`);
    }
  }

  function checkInventory() {
    addToLog("인벤토리를 확인합니다...");
    addToLog("현재 소지품: 낡은 검, 가죽 갑옷, 빵 3개");
  }

  function castSpell() {
    if (!currentCharacter) return;
    
    if (currentCharacter.stats.intelligence < 10) {
      addToLog("지능이 부족하여 마법을 사용할 수 없습니다.");
      return;
    }
    
    addToLog("마법을 시전합니다... ✨");
    addToLog("밝은 빛이 주변을 비춥니다. 숨어있던 보물상자를 발견했습니다!");
    currentStory = "마법의 빛으로 숨겨진 보물상자를 발견했습니다. 상자는 오래된 것처럼 보이지만 아직 견고합니다.";
  }

  function rest() {
    addToLog("잠시 휴식을 취합니다...");
    addToLog("피로가 조금 회복되었습니다.");
    currentStory = "잠시 휴식을 취하며 주변을 관찰합니다. 이곳은 생각보다 평화로운 곳 같습니다.";
  }

  function returnToCharacterSelect() {
    selectedCharacter.set(null);
    currentPage.set('characters');
    showNotification('info', '캐릭터 선택 화면으로 돌아갑니다.');
  }

  function getStatColor(statName) {
    switch(statName) {
      case 'strength': return 'text-red-400';
      case 'dexterity': return 'text-green-400';
      case 'intelligence': return 'text-blue-400';
      case 'luck': return 'text-yellow-400';
      default: return 'text-gray-400';
    }
  }
</script>

{#if currentCharacter}
<div class="animate-fadeIn h-screen flex flex-col">
  <!-- 헤더 -->
  <div class="bg-gray-900 border-b border-gray-700 p-4">
    <div class="flex justify-between items-center">
      <div class="flex items-center space-x-4">
        <button 
          class="btn-secondary text-sm"
          on:click={returnToCharacterSelect}
        >
          ← 캐릭터 선택
        </button>
        <div>
          <h1 class="text-2xl font-fantasy text-rpg-gold">{currentCharacter.name}의 모험</h1>
          <p class="text-gray-400">레벨 {currentCharacter.level} 모험가</p>
        </div>
      </div>
      
      <!-- 캐릭터 상태 -->
      <div class="flex items-center space-x-6">
        <!-- 체력 -->
        <div class="text-center">
          <div class="text-sm text-gray-400">체력</div>
          <div class="text-lg font-bold text-red-400">
            {currentCharacter.health.current}/{currentCharacter.health.max}
          </div>
          <div class="w-20 stat-bar">
            <div 
              class="stat-fill bg-red-500" 
              style="width: {currentCharacter.health.percentage}%"
            ></div>
          </div>
        </div>
        
        <!-- 간단 스탯 -->
        <div class="grid grid-cols-4 gap-4 text-center text-sm">
          <div>
            <div class="text-gray-400">힘</div>
            <div class="text-red-400 font-bold">{currentCharacter.stats.strength}</div>
          </div>
          <div>
            <div class="text-gray-400">민첩</div>
            <div class="text-green-400 font-bold">{currentCharacter.stats.dexterity}</div>
          </div>
          <div>
            <div class="text-gray-400">지능</div>
            <div class="text-blue-400 font-bold">{currentCharacter.stats.intelligence}</div>
          </div>
          <div>
            <div class="text-gray-400">행운</div>
            <div class="text-yellow-400 font-bold">{currentCharacter.stats.luck}</div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 메인 게임 영역 -->
  <div class="flex-1 grid grid-cols-1 lg:grid-cols-3 gap-4 p-4 overflow-hidden">
    <!-- 스토리 및 액션 영역 -->
    <div class="lg:col-span-2 space-y-4">
      <!-- 현재 상황 -->
      <div class="card h-48">
        <h3 class="text-xl font-fantasy text-rpg-gold mb-4">현재 상황</h3>
        <div class="bg-gray-900 p-4 rounded text-gray-300 leading-relaxed">
          {currentStory}
        </div>
      </div>

      <!-- 액션 버튼들 -->
      <div class="card">
        <h3 class="text-xl font-fantasy text-rpg-gold mb-4">행동 선택</h3>
        <div class="grid grid-cols-2 md:grid-cols-3 gap-3">
          <button 
            class="btn-primary py-2 px-3 text-sm"
            on:click={goForward}
          >
            🚶‍♂️ 앞으로 가기
          </button>
          <button 
            class="btn-primary py-2 px-3 text-sm"
            on:click={goLeft}
          >
            🌊 개울가로 가기
          </button>
          <button 
            class="btn-secondary py-2 px-3 text-sm"
            on:click={checkInventory}
          >
            🎒 인벤토리
          </button>
          <button 
            class="btn-secondary py-2 px-3 text-sm"
            on:click={castSpell}
            disabled={currentCharacter.stats.intelligence < 10}
          >
            ✨ 마법 시전
          </button>
          <button 
            class="btn-secondary py-2 px-3 text-sm"
            on:click={rest}
          >
            😴 휴식하기
          </button>
          <button 
            class="btn-secondary py-2 px-3 text-sm"
            on:click={() => addToLog("주변을 살펴봅니다...")}
          >
            👀 둘러보기
          </button>
        </div>
      </div>
    </div>

    <!-- 게임 로그 -->
    <div class="card">
      <h3 class="text-xl font-fantasy text-rpg-gold mb-4">모험 일지</h3>
      <div class="bg-gray-900 p-3 rounded h-96 overflow-y-auto">
        {#each gameLog as logEntry}
          <div class="text-sm text-gray-300 mb-2 leading-relaxed">
            {logEntry}
          </div>
        {/each}
        {#if gameLog.length === 0}
          <div class="text-gray-500 text-center mt-8">
            모험의 기록이 여기에 표시됩니다...
          </div>
        {/if}
      </div>
    </div>
  </div>

  <!-- 하단 상태바 -->
  <div class="bg-gray-900 border-t border-gray-700 p-3">
    <div class="flex justify-between items-center text-sm">
      <div class="flex items-center space-x-4">
        <span class="text-gray-400">위치:</span>
        <span class="text-rpg-gold">신비의 숲</span>
      </div>
      <div class="flex items-center space-x-4">
        <span class="text-gray-400">경험치:</span>
        <span class="text-blue-400">0 / 100</span>
        <span class="text-gray-400">골드:</span>
        <span class="text-yellow-400">50</span>
      </div>
    </div>
  </div>
</div>
{:else}
<div class="flex items-center justify-center min-h-screen">
  <div class="text-center">
    <div class="text-6xl text-gray-600 mb-4">⚠️</div>
    <h2 class="text-2xl font-fantasy text-gray-400 mb-4">캐릭터가 선택되지 않았습니다</h2>
    <p class="text-gray-500 mb-6">게임을 시작하려면 캐릭터를 선택해주세요.</p>
    <button 
      class="btn-primary"
      on:click={() => currentPage.set('characters')}
    >
      캐릭터 선택하기
    </button>
  </div>
</div>
{/if}

<style>
  /* 로그 스크롤 커스터마이징 */
  .card ::-webkit-scrollbar {
    width: 4px;
  }
  
  .card ::-webkit-scrollbar-track {
    background: #374151;
  }
  
  .card ::-webkit-scrollbar-thumb {
    background: #6b7280;
    border-radius: 2px;
  }
  
  .card ::-webkit-scrollbar-thumb:hover {
    background: #9ca3af;
  }
</style>
