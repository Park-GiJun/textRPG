<script>
  import '../app.css';
  import { onMount } from 'svelte';
  import { api } from '$lib/api';
  import { user, isAuthenticated, currentPage, notification } from '$lib/stores';
  import Toast from '$lib/components/Toast.svelte';

  onMount(async () => {
    // 로그인 상태 확인
    if (api.isAuthenticated()) {
      try {
        const userData = await api.getCurrentUser();
        user.set(userData);
        isAuthenticated.set(true);
        currentPage.set('characters');
      } catch (error) {
        // 토큰이 유효하지 않으면 로그아웃
        api.logout();
        isAuthenticated.set(false);
        currentPage.set('login');
      }
    }
  });
</script>

<div class="min-h-screen bg-rpg-dark">
  <slot />
  {#if $notification}
    <Toast 
      type={$notification.type} 
      message={$notification.message} 
      onClose={() => notification.set(null)} 
    />
  {/if}
</div>

<style>
  :global(body) {
    font-family: 'Orbitron', monospace;
  }
</style>
