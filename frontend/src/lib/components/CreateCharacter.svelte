<script>
  import { api } from '$lib/api';
  import { currentPage, showNotification } from '$lib/stores';

  let name = '';
  let strength = 10;
  let dexterity = 10;
  let intelligence = 10;
  let luck = 5;
  let createLoading = false;

  // 스탯 포인트 관리
  const maxTotalStats = 45;
  const minStatValue = 5;
  const maxStatValue = 20;

  $: totalStats = strength + dexterity + intelligence + luck;
  $: remainingPoints = maxTotalStats - totalStats;
  $: canIncrease = (stat) => stat < maxStatValue && remainingPoints > 0;
  $: canDecrease = (stat) => stat > minStatValue;

  function increaseStat(statName) {
    if (statName === 'strength' && canIncrease(strength)) strength++;
    else if (statName === 'dexterity' && canIncrease(dexterity)) dexterity++;
    else if (statName === 'intelligence' && canIncrease(intelligence)) intelligence++;
    else if (statName === 'luck' && canIncrease(luck)) luck++;
  }

  function decreaseStat(statName) {
    if (statName === 'strength' && canDecrease(strength)) strength--;
    else if (statName === 'dexterity' && canDecrease(dexterity)) dexterity--;
    else if (statName === 'intelligence' && canDecrease(intelligence)) intelligence--;
    else if (statName === 'luck' && canDecrease(luck)) luck--;
  }

  function resetStats() {
    strength = 10;
    dexterity = 10;
    intelligence = 10;
    luck = 5;
  }

  async function createCharacter() {
    if (!name.trim()) {
      showNotification('error', '캐릭터 이름을 입력해주세요.');
      return;
    }

    if (name.length > 50) {
      showNotification('error', '캐릭터 이름은 50자 이하여야 합니다.');
      return;
    }

    createLoading = true;
    try {
      await api.createCharacter({
        name: name.trim(),
        strength,
        dexterity,
        intelligence,
        luck
      });
      
      showNotification('success', `${name} 캐릭터가 생성되었습니다!`);
      currentPage.set('characters');
    } catch (error) {
      showNotification('error', error instanceof Error ? error.message : '캐릭터 생성에 실패했습니다.');
    } finally {
      createLoading = false;
    }
  }

  function goBack() {
    currentPage.set('characters');
  }

  function getCharacterClass() {
    const maxStat = Math.max(strength, dexterity, intelligence, luck);
    if (maxStat === strength) return '전사';
    if (maxStat === dexterity) return '궁수';
    if (maxStat === intelligence) return '마법사';
    if (maxStat === luck) return '도적';
    return '균형';
  }

  function getClassDescription() {
    const characterClass = getCharacterClass();
    switch (characterClass) {
      case '전사': return '높은 공격력과 방어력을 가진 근접 전투의 달인';
      case '궁수': return '빠른 속도와 정확한 원거리 공격이 특기';
      case '마법사': return '강력한 마법과 높은 마나를 보유한 지식의 추구자';
      case '도적': return '행운과 기회를 활용하는 교활한 전략가';
      default: return '모든 능력이 균형잡힌 만능형 캐릭터';
    }
  }
</script>

<div class="animate-fadeIn max-w-4xl mx-auto">
  <!-- 헤더 -->
  <div class="flex items-center mb-8">
    <button 
      class="btn-secondary mr-4"
      on:click={goBack}
      disabled={createLoading}
    >
      ← 뒤로가기
    </button>
    <div>
      <h1 class="text-4xl font-fantasy text-rpg-gold mb-2">캐릭터 생성</h1>
      <p class="text-gray-300">새로운 모험가의 특성을 설정하세요</p>
    </div>
  </div>

  <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
    <!-- 왼쪽: 캐릭터 정보 입력 -->
    <div class="space-y-6">
      <!-- 이름 입력 -->
      <div class="card">
        <h3 class="text-xl font-fantasy text-rpg-gold mb-4">기본 정보</h3>
        <div>
          <label for="character-name" class="block text-sm font-medium text-gray-300 mb-2">
            캐릭터 이름 *
          </label>
          <input 
            type="text" 
            id="character-name"
            bind:value={name}
            class="input-field w-full"
            placeholder="모험가의 이름을 입력하세요"
            disabled={createLoading}
            maxlength="50"
          />
          <p class="text-xs text-gray-500 mt-1">{name.length}/50자</p>
        </div>
      </div>

      <!-- 스탯 설정 -->
      <div class="card">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-xl font-fantasy text-rpg-gold">능력치 배분</h3>
          <button 
            class="btn-secondary text-sm px-3 py-1"
            on:click={resetStats}
            disabled={createLoading}
          >
            초기화
          </button>
        </div>
        
        <div class="mb-4 p-3 bg-gray-900 rounded">
          <div class="flex justify-between text-sm">
            <span class="text-gray-400">사용 포인트:</span>
            <span class={remainingPoints < 0 ? 'text-red-400' : 'text-rpg-gold'}>
              {totalStats} / {maxTotalStats}
            </span>
          </div>
          <div class="flex justify-between text-sm">
            <span class="text-gray-400">남은 포인트:</span>
            <span class={remainingPoints < 0 ? 'text-red-400' : 'text-green-400'}>
              {remainingPoints}
            </span>
          </div>
        </div>

        <div class="space-y-4">
          <!-- 힘 -->
          <div class="flex items-center justify-between">
            <div class="flex-1">
              <div class="flex items-center space-x-2">
                <span class="text-red-400 text-lg">⚔️</span>
                <span class="text-white font-medium">힘</span>
              </div>
              <p class="text-xs text-gray-500">물리 공격력과 근접 데미지</p>
            </div>
            <div class="flex items-center space-x-2">
              <button 
                class="w-8 h-8 bg-gray-700 hover:bg-gray-600 rounded flex items-center justify-center text-white"
                on:click={() => decreaseStat('strength')}
                disabled={!canDecrease(strength) || createLoading}
              >
                -
              </button>
              <span class="w-8 text-center text-lg font-bold text-red-400">{strength}</span>
              <button 
                class="w-8 h-8 bg-gray-700 hover:bg-gray-600 rounded flex items-center justify-center text-white"
                on:click={() => increaseStat('strength')}
                disabled={!canIncrease(strength) || createLoading}
              >
                +
              </button>
            </div>
          </div>

          <!-- 민첩 -->
          <div class="flex items-center justify-between">
            <div class="flex-1">
              <div class="flex items-center space-x-2">
                <span class="text-green-400 text-lg">🏹</span>
                <span class="text-white font-medium">민첩</span>
              </div>
              <p class="text-xs text-gray-500">명중률, 회피율, 원거리 데미지</p>
            </div>
            <div class="flex items-center space-x-2">
              <button 
                class="w-8 h-8 bg-gray-700 hover:bg-gray-600 rounded flex items-center justify-center text-white"
                on:click={() => decreaseStat('dexterity')}
                disabled={!canDecrease(dexterity) || createLoading}
              >
                -
              </button>
              <span class="w-8 text-center text-lg font-bold text-green-400">{dexterity}</span>
              <button 
                class="w-8 h-8 bg-gray-700 hover:bg-gray-600 rounded flex items-center justify-center text-white"
                on:click={() => increaseStat('dexterity')}
                disabled={!canIncrease(dexterity) || createLoading}
              >
                +
              </button>
            </div>
          </div>

          <!-- 지능 -->
          <div class="flex items-center justify-between">
            <div class="flex-1">
              <div class="flex items-center space-x-2">
                <span class="text-blue-400 text-lg">🔮</span>
                <span class="text-white font-medium">지능</span>
              </div>
              <p class="text-xs text-gray-500">마법 공격력과 마나량</p>
            </div>
            <div class="flex items-center space-x-2">
              <button 
                class="w-8 h-8 bg-gray-700 hover:bg-gray-600 rounded flex items-center justify-center text-white"
                on:click={() => decreaseStat('intelligence')}
                disabled={!canDecrease(intelligence) || createLoading}
              >
                -
              </button>
              <span class="w-8 text-center text-lg font-bold text-blue-400">{intelligence}</span>
              <button 
                class="w-8 h-8 bg-gray-700 hover:bg-gray-600 rounded flex items-center justify-center text-white"
                on:click={() => increaseStat('intelligence')}
                disabled={!canIncrease(intelligence) || createLoading}
              >
                +
              </button>
            </div>
          </div>

          <!-- 행운 -->
          <div class="flex items-center justify-between">
            <div class="flex-1">
              <div class="flex items-center space-x-2">
                <span class="text-yellow-400 text-lg">🍀</span>
                <span class="text-white font-medium">행운</span>
              </div>
              <p class="text-xs text-gray-500">크리티컬, 아이템 드롭률</p>
            </div>
            <div class="flex items-center space-x-2">
              <button 
                class="w-8 h-8 bg-gray-700 hover:bg-gray-600 rounded flex items-center justify-center text-white"
                on:click={() => decreaseStat('luck')}
                disabled={!canDecrease(luck) || createLoading}
              >
                -
              </button>
              <span class="w-8 text-center text-lg font-bold text-yellow-400">{luck}</span>
              <button 
                class="w-8 h-8 bg-gray-700 hover:bg-gray-600 rounded flex items-center justify-center text-white"
                on:click={() => increaseStat('luck')}
                disabled={!canIncrease(luck) || createLoading}
              >
                +
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 오른쪽: 미리보기 -->
    <div class="space-y-6">
      <!-- 캐릭터 미리보기 -->
      <div class="card">
        <h3 class="text-xl font-fantasy text-rpg-gold mb-4">캐릭터 미리보기</h3>
        
        <div class="text-center mb-6">
          <div class="text-6xl mb-3">
            {#if getCharacterClass() === '전사'}
              ⚔️
            {:else if getCharacterClass() === '궁수'}
              🏹
            {:else if getCharacterClass() === '마법사'}
              🔮
            {:else if getCharacterClass() === '도적'}
              🗡️
            {:else}
              🛡️
            {/if}
          </div>
          <h4 class="text-2xl font-fantasy text-white mb-1">
            {name || '이름 없는 모험가'}
          </h4>
          <p class="text-lg text-rpg-gold">{getCharacterClass()}</p>
        </div>

        <div class="bg-gray-900 p-4 rounded mb-4">
          <p class="text-sm text-gray-300 text-center">
            {getClassDescription()}
          </p>
        </div>

        <!-- 예상 스탯 -->
        <div class="space-y-2">
          <div class="flex justify-between">
            <span class="text-gray-400">총 전투력:</span>
            <span class="text-rpg-gold font-bold">{totalStats}</span>
          </div>
          <div class="flex justify-between">
            <span class="text-gray-400">예상 체력:</span>
            <span class="text-green-400">{100 + Math.floor(intelligence / 2)}</span>
          </div>
        </div>
      </div>

      <!-- 생성 버튼 -->
      <div class="card">
        <button 
          class="btn-primary w-full text-lg py-4"
          on:click={createCharacter}
          disabled={createLoading || !name.trim() || remainingPoints < 0}
        >
          {#if createLoading}
            <span class="animate-pulse-slow">캐릭터 생성 중...</span>
          {:else}
            🛡️ 캐릭터 생성하기
          {/if}
        </button>
        
        {#if remainingPoints < 0}
          <p class="text-red-400 text-sm text-center mt-2">
            ⚠️ 스탯 포인트가 초과되었습니다
          </p>
        {:else if !name.trim()}
          <p class="text-yellow-400 text-sm text-center mt-2">
            ⚠️ 캐릭터 이름을 입력해주세요
          </p>
        {/if}
      </div>
    </div>
  </div>
</div>
