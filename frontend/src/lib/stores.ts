import { writable } from 'svelte/store';

// 사용자 인증 상태
export const user = writable(null);
export const isAuthenticated = writable(false);
export const isLoading = writable(false);

// 캐릭터 상태
export const characters = writable([]);
export const selectedCharacter = writable(null);
export const isCharactersLoading = writable(false);

// UI 상태
export const currentPage = writable('login');
export const notification = writable(null);

// 알림 표시 함수
export function showNotification(type, message) {
  notification.set({ type, message });
  setTimeout(() => {
    notification.set(null);
  }, 5000);
}
