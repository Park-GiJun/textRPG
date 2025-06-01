import Cookies from 'js-cookie';

const API_BASE_URL = 'http://localhost:8080/api/v1';

// API 클래스
class ApiService {
  getAuthHeaders() {
    const token = Cookies.get('auth_token');
    return {
      'Content-Type': 'application/json',
      ...(token && { Authorization: `Bearer ${token}` })
    };
  }

  async handleResponse(response) {
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
    }
    return response.json();
  }

  // 인증 API
  async register(data) {
    const response = await fetch(`${API_BASE_URL}/auth/register`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(data)
    });
    return this.handleResponse(response);
  }

  async login(data) {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(data)
    });
    const result = await this.handleResponse(response);
    
    // 토큰을 쿠키에 저장
    if (result.token) {
      const expirationDate = new Date(Date.now() + (result.expiresIn || 86400000));
      Cookies.set('auth_token', result.token, { expires: expirationDate });
    }
    
    return result;
  }

  async getCurrentUser() {
    const response = await fetch(`${API_BASE_URL}/auth/me`, {
      headers: this.getAuthHeaders()
    });
    return this.handleResponse(response);
  }

  logout() {
    Cookies.remove('auth_token');
  }

  isAuthenticated() {
    return !!Cookies.get('auth_token');
  }

  // 캐릭터 API
  async createCharacter(data) {
    const response = await fetch(`${API_BASE_URL}/characters`, {
      method: 'POST',
      headers: this.getAuthHeaders(),
      body: JSON.stringify(data)
    });
    return this.handleResponse(response);
  }

  async getMyCharacters() {
    const response = await fetch(`${API_BASE_URL}/characters/my`, {
      headers: this.getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`);
    }

    // 스트림 응답을 처리
    const reader = response.body?.getReader();
    const decoder = new TextDecoder();
    const characters = [];
    
    if (reader) {
      while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        
        const chunk = decoder.decode(value);
        const lines = chunk.split('\n').filter(line => line.trim());
        
        for (const line of lines) {
          try {
            const character = JSON.parse(line);
            characters.push(character);
          } catch (e) {
            // JSON 파싱 오류 무시
          }
        }
      }
    }
    
    return characters;
  }

  async getCharacter(id) {
    const response = await fetch(`${API_BASE_URL}/characters/${id}`, {
      headers: this.getAuthHeaders()
    });
    return this.handleResponse(response);
  }

  async deleteCharacter(id) {
    const response = await fetch(`${API_BASE_URL}/characters/${id}`, {
      method: 'DELETE',
      headers: this.getAuthHeaders()
    });
    
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
    }
  }
}

export const api = new ApiService();
