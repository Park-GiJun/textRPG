// 간단한 타입 정의
export type NotificationType = 'success' | 'error' | 'info';
export type PageType = 'login' | 'register' | 'characters' | 'create-character' | 'game';
export type StatType = 'strength' | 'dexterity' | 'intelligence' | 'luck';

// 기본 인터페이스들
export interface User {
  id: string;
  username: string;
  email: string;
  roles: string[];
}

export interface Character {
  id: string;
  name: string;
  level: number;
  health: {
    current: number;
    max: number;
    percentage: number;
  };
  stats: {
    strength: number;
    dexterity: number;
    intelligence: number;
    luck: number;
    totalPower: number;
  };
  createdAt: string;
  updatedAt: string;
}
