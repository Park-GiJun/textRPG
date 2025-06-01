<script>
  import { api } from '$lib/api';
  import { currentPage, showNotification } from '$lib/stores';

  let username = '';
  let email = '';
  let password = '';
  let confirmPassword = '';
  let registerLoading = false;

  async function handleRegister() {
    if (!username || !email || !password || !confirmPassword) {
      showNotification('error', '모든 필드를 입력해주세요.');
      return;
    }

    if (password !== confirmPassword) {
      showNotification('error', '비밀번호가 일치하지 않습니다.');
      return;
    }

    if (password.length < 6) {
      showNotification('error', '비밀번호는 최소 6자 이상이어야 합니다.');
      return;
    }

    registerLoading = true;
    try {
      await api.register({ username, email, password });
      showNotification('success', '회원가입이 완료되었습니다! 로그인해주세요.');
      currentPage.set('login');
    } catch (error) {
      showNotification('error', error instanceof Error ? error.message : '회원가입에 실패했습니다.');
    } finally {
      registerLoading = false;
    }
  }

  function goToLogin() {
    currentPage.set('login');
  }
</script>

<div class="flex flex-col items-center space-y-8 animate-fadeIn">
  <!-- 타이틀 -->
  <div class="text-center">
    <h1 class="rpg-title mb-4">TextRPG</h1>
    <p class="rpg-subtitle">새로운 모험가가 되어보세요</p>
  </div>

  <!-- 회원가입 폼 -->
  <div class="card w-full max-w-md">
    <h2 class="text-2xl font-fantasy text-rpg-gold mb-6 text-center">회원가입</h2>
    
    <form on:submit|preventDefault={handleRegister} class="space-y-4">
      <div>
        <label for="reg-username" class="block text-sm font-medium text-gray-300 mb-2">
          사용자명 *
        </label>
        <input 
          type="text" 
          id="reg-username"
          bind:value={username}
          class="input-field w-full"
          placeholder="3-50자의 사용자명"
          disabled={registerLoading}
          minlength="3"
          maxlength="50"
        />
      </div>

      <div>
        <label for="reg-email" class="block text-sm font-medium text-gray-300 mb-2">
          이메일 *
        </label>
        <input 
          type="email" 
          id="reg-email"
          bind:value={email}
          class="input-field w-full"
          placeholder="your@email.com"
          disabled={registerLoading}
        />
      </div>

      <div>
        <label for="reg-password" class="block text-sm font-medium text-gray-300 mb-2">
          비밀번호 *
        </label>
        <input 
          type="password" 
          id="reg-password"
          bind:value={password}
          class="input-field w-full"
          placeholder="최소 6자 이상"
          disabled={registerLoading}
          minlength="6"
        />
      </div>

      <div>
        <label for="reg-confirm-password" class="block text-sm font-medium text-gray-300 mb-2">
          비밀번호 확인 *
        </label>
        <input 
          type="password" 
          id="reg-confirm-password"
          bind:value={confirmPassword}
          class="input-field w-full"
          placeholder="비밀번호 재입력"
          disabled={registerLoading}
        />
      </div>

      <button 
        type="submit" 
        class="btn-primary w-full"
        disabled={registerLoading}
      >
        {#if registerLoading}
          <span class="animate-pulse-slow">가입 처리 중...</span>
        {:else}
          🛡️ 모험가 등록하기
        {/if}
      </button>
    </form>

    <div class="mt-6 text-center">
      <p class="text-gray-400 mb-3">이미 계정이 있으신가요?</p>
      <button 
        class="btn-secondary"
        on:click={goToLogin}
        disabled={registerLoading}
      >
        ⚔️ 로그인하기
      </button>
    </div>
  </div>

  <!-- 회원가입 안내 -->
  <div class="card w-full max-w-md bg-gray-900 border-blue-600">
    <h3 class="text-lg font-fantasy text-blue-400 mb-3">📖 안내사항</h3>
    <ul class="text-sm text-gray-300 space-y-2">
      <li>• 사용자명은 3-50자 사이여야 합니다</li>
      <li>• 비밀번호는 최소 6자 이상이어야 합니다</li>
      <li>• 이메일은 계정 복구에 사용됩니다</li>
      <li>• 회원가입 후 바로 게임을 시작할 수 있습니다</li>
    </ul>
  </div>
</div>
