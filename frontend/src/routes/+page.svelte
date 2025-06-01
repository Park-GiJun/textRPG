<script>
  import { currentPage } from '$lib/stores';
  import LoginForm from '$lib/components/LoginForm.svelte';
  import RegisterForm from '$lib/components/RegisterForm.svelte';
  import CharacterList from '$lib/components/CharacterList.svelte';
  import CreateCharacter from '$lib/components/CreateCharacter.svelte';
  import GameView from '$lib/components/GameView.svelte';
</script>

<main class="min-h-screen flex items-center justify-center p-4">
  <div class="w-full max-w-6xl">
    {#if $currentPage === 'login'}
      <LoginForm />
    {:else if $currentPage === 'register'}
      <RegisterForm />
    {:else if $currentPage === 'characters'}
      <CharacterList />
    {:else if $currentPage === 'create-character'}
      <CreateCharacter />
    {:else if $currentPage === 'game'}
      <GameView />
    {/if}
  </div>
</main>
