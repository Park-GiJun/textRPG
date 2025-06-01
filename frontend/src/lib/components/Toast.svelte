<script>
  export let type = 'info';
  export let message = '';
  export let onClose;

  function getStyles(type) {
    switch(type) {
      case 'success':
        return { bg: 'bg-green-600', icon: '✓' };
      case 'error':
        return { bg: 'bg-red-600', icon: '✗' };
      default:
        return { bg: 'bg-blue-600', icon: 'ℹ' };
    }
  }

  $: styles = getStyles(type);
</script>

<div class="fixed top-4 right-4 z-50">
  <div class="{styles.bg} text-white px-6 py-4 rounded-lg shadow-lg flex items-center space-x-3 max-w-md animate-fadeIn">
    <span class="text-xl font-bold">{styles.icon}</span>
    <span class="flex-1">{message}</span>
    <button 
      class="text-white hover:text-gray-300 font-bold text-xl"
      on:click={() => onClose && onClose()}
    >
      ×
    </button>
  </div>
</div>
