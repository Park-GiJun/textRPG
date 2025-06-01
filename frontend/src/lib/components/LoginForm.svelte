<script>
  import { api } from '$lib/api';
  import { user, isAuthenticated, currentPage, isLoading, showNotification } from '$lib/stores';

  let username = '';
  let password = '';
  let loginLoading = false;

  async function handleLogin() {
    if (!username || !password) {
      showNotification('error', '사용자명과 비밀번호를 입력해주세요.');
      return;
    }

    loginLoading = true;
    try {
      const authResult = await api.login({ username, password });
      user.set(authResult);
      isAuthenticated.set(true);
      currentPage.set('characters');
      showNotification('success', `환영합니다, ${authResult.username}님!`);
    } catch (error) {
      showNotification('error', error instanceof Error ? error.message : '로그인에 실패했습니다.');
    } finally {
      loginLoading = false;
    }
  }

  function goToRegister() {
    currentPage.set('register');
  }
</script>

<div class="flex flex-col items-center space-y-8 animate-fadeIn">
  <!-- 타이틀 -->
  <div class="text-center">
    <h1 class="rpg-title mb-4">TextRPG</h1>
    <p class="rpg-subtitle">모험이 당신을 기다립니다</p>
  </div>

  <!-- 로그인 폼 -->
  <div class="card w-full max-w-md">
    <h2 class="text-2xl font-fantasy text-rpg-gold mb-6 text-center">로그인</h2>
    
    <form on:submit|preventDefault={handleLogin} class="space-y-4">
      <div>
        <label for="username" class="block text-sm font-medium text-gray-300 mb-2">
          사용자명
        </label>
        <input 
          type="text" 
          id="username"
          bind:value={username}
          class="input-field w-full"
          placeholder="사용자명을 입력하세요"
          disabled={loginLoading}
        />
      </div>

      <div>
        <label for="password" class="block text-sm font-medium text-gray-300 mb-2">
          비밀번호
        </label>
        <input 
          type="password" 
          id="password"
          bind:value={password}
          class="input-field w-full"
          placeholder="비밀번호를 입력하세요"
          disabled={loginLoading}
        />
      </div>

      <button 
        type="submit" 
        class="btn-primary w-full"
        disabled={loginLoading}
      >
        {#if loginLoading}
          <span class="animate-pulse-slow">로그인 중...</span>
        {:else}
          ⚔️ 모험 시작하기
        {/if}
      </button>
    </form>

    <div class="mt-6 text-center">
      <p class="text-gray-400 mb-3">아직 계정이 없으신가요?</p>
      <button 
        class="btn-secondary"
        on:click={goToRegister}
        disabled={loginLoading}
      >
        🛡️ 새로운 모험가 등록
      </button>
    </div>
  </div>

  <!-- 데모 계정 안내 -->
  <div class="card w-full max-w-md bg-gray-900 border-yellow-600">
    <h3 class="text-lg font-fantasy text-rpg-gold mb-3">🎮 체험해보기</h3>
    <p class="text-sm text-gray-300 mb-3">테스트 계정으로 바로 시작할 수 있습니다:</p>
    <div class="bg-gray-800 p-3 rounded text-sm font-mono">
      <div>사용자명: <span class="text-rpg-gold">admin</span></div>
      <div>비밀번호: <span class="text-rpg-gold">admin123</span></div>
    </div>
  </div>
</div>
