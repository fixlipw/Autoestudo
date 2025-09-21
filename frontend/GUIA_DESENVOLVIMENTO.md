# Guia de Desenvolvimento - Vue.js + TypeScript + Vuetify

Este guia contém componentes, padrões e boas práticas extraídos do projeto para uso em desenvolvimentos futuros.

## 📋 Índice

1. [Stack Tecnológica](#stack-tecnológica)
2. [Estrutura de Pastas](#estrutura-de-pastas)
3. [Componentes Reutilizáveis](#componentes-reutilizáveis)
4. [Padrões de Desenvolvimento](#padrões-de-desenvolvimento)
5. [Gerenciamento de Estado](#gerenciamento-de-estado)
6. [Sistema de Autenticação](#sistema-de-autenticação)
7. [Validação de Formulários](#validação-de-formulários)
8. [Interceptadores HTTP](#interceptadores-http)
9. [Tipos TypeScript](#tipos-typescript)
10. [Roteamento](#roteamento)
11. [Boas Práticas](#boas-práticas)

## 🛠️ Stack Tecnológica

### Dependências Principais
```json
{
  "vue": "^3.5.18",
  "vue-router": "^4.5.1",
  "pinia": "^3.0.3",
  "vuetify": "^3.10.0",
  "axios": "^1.12.0",
  "jwt-decode": "^4.0.0",
  "vee-validate": "^4.15.1",
  "maska": "^3.2.0",
  "typescript": "~5.8.3",
  "vite": "^7.1.2"
}
```

### Ferramentas de Desenvolvimento
- **Vite**: Build tool e dev server
- **TypeScript**: Tipagem estática
- **ESLint**: Linting de código
- **Sass**: Pré-processador CSS

## 📁 Estrutura de Pastas

```
src/
├── api/                 # Configurações de API
├── assets/             # Recursos estáticos
├── components/         # Componentes reutilizáveis
│   ├── comments/       # Componentes de comentários
│   ├── layout/         # Componentes de layout
│   ├── posts/          # Componentes de posts
│   ├── ui/             # Componentes de UI genéricos
│   └── user/           # Componentes de usuário
├── composables/        # Composables Vue
├── layouts/            # Layouts de página
├── plugins/            # Plugins Vue
├── router/             # Configuração de rotas
├── services/           # Serviços de API
├── stores/             # Stores Pinia
├── styles/             # Estilos globais
├── types/              # Definições TypeScript
├── utils/              # Utilitários
└── views/              # Páginas/Views
    ├── admin/          # Views administrativas
    ├── auth/           # Views de autenticação
    ├── posts/          # Views de posts
    └── user/           # Views de usuário
```

## 🔧 Componentes Reutilizáveis

### 1. Componente de Alerta (AppAlert.vue)

```vue
<template>
  <v-snackbar
    v-model="alert.show"
    :color="alert.type"
    :timeout="4000"
    location="top right"
    multi-line
  >
    {{ alert.message }}
    <template v-slot:actions>
      <v-btn
        icon="mdi-close"
        variant="text"
        @click="hideAlert"
      ></v-btn>
    </template>
  </v-snackbar>
</template>

<script setup lang="ts">
import { useUiStore } from '@/stores/ui';
import { computed } from 'vue';

const uiStore = useUiStore();
const alert = computed(() => uiStore.alert);
const hideAlert = uiStore.hideAlert;
</script>
```

**Uso:**
- Notificações globais da aplicação
- Integrado com store UI
- Auto-dismiss configurável

### 2. Dialog de Confirmação (ConfirmDialog.vue)

**Estrutura Base:**
```vue
<template>
  <v-dialog v-model="show" max-width="400">
    <v-card>
      <v-card-title>{{ title }}</v-card-title>
      <v-card-text>{{ message }}</v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn @click="cancel">Cancelar</v-btn>
        <v-btn color="primary" @click="confirm">Confirmar</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
```

## 🎯 Padrões de Desenvolvimento

### 1. Composição API com TypeScript

```typescript
// Padrão de composable
export const useValidation = () => {
  const form = reactive<FormValidation>({});
  
  const rules = {
    required: (message = 'Este campo é obrigatório'): ValidationRule =>
      (value) => !!value || message,
    
    email: (message = 'E-mail deve ser válido'): ValidationRule =>
      (value) => !value || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) || message,
  };
  
  return { form, rules };
};
```

### 2. Padrão de Service Layer

```typescript
// services/apiClient.ts
const apiClient = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
});

// Interceptador para tokens
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

### 3. Padrão de Error Handling

```typescript
// Tratamento de erros com refresh token
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    if (error.response?.status === 401 && !originalRequest._retry) {
      // Lógica de refresh token
      return handleTokenRefresh(originalRequest);
    }
    return Promise.reject(error);
  }
);
```

## 🗄️ Gerenciamento de Estado

### Store de Autenticação (Pinia)

```typescript
export const useAuthStore = defineStore('auth', {
  state: () => ({
    accessToken: localStorage.getItem('accessToken') || null,
    refreshToken: localStorage.getItem('refreshToken') || null,
    user: JSON.parse(localStorage.getItem('user') || 'null') as User | null,
  }),

  getters: {
    isAuthenticated: (state): boolean => !!state.accessToken && !!state.user,
    isAdmin: (state): boolean => state.user?.role === UserRole.ADMIN,
    isActive: (state): boolean => state.user?.status === 'ACTIVE',
  },

  actions: {
    async login(credentials: LoginPayload) {
      // Lógica de login
    },
    
    async rehydrateAuth() {
      // Restaurar estado da autenticação
    }
  }
});
```

### Store de UI

```typescript
export const useUiStore = defineStore('ui', {
  state: () => ({
    alert: {
      show: false,
      message: '',
      type: 'info' as 'success' | 'error' | 'warning' | 'info'
    }
  }),

  actions: {
    showAlert(message: string, type: AlertType = 'info') {
      this.alert = { show: true, message, type };
    },
    
    hideAlert() {
      this.alert.show = false;
    }
  }
});
```

## 🔐 Sistema de Autenticação

### 1. Inicialização da App

```typescript
// main.ts
const app = createApp(App);
registerPlugins(app);

const authStore = useAuthStore();
authStore.rehydrateAuth().then(() => {
  app.mount('#app');
});
```

### 2. Guards de Rota

```typescript
// router/index.ts
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login');
  } else if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next('/');
  } else {
    next();
  }
});
```

## ✅ Validação de Formulários

### Sistema de Validação Customizado

```typescript
export const useValidation = () => {
  const rules = {
    required: (message = 'Este campo é obrigatório'): ValidationRule =>
      (value) => !!value || message,

    minLength: (min: number, message?: string): ValidationRule =>
      (value) => !value || value.length >= min || message || `Mínimo de ${min} caracteres`,

    email: (message = 'E-mail deve ser válido'): ValidationRule =>
      (value) => !value || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) || message,

    password: (message = 'Senha deve ter pelo menos 8 caracteres...'): ValidationRule =>
      (value) => !value || /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,}$/.test(value) || message,

    confirmPassword: (originalPassword: () => string, message = 'Senhas não coincidem'): ValidationRule =>
      (value) => !value || value === originalPassword() || message,
  };

  return { rules };
};
```

## 🌐 Interceptadores HTTP

### Gerenciamento de Token com Refresh

```typescript
let isRefreshing = false;
let failedQueue: Array<{ resolve: Function; reject: Function }> = [];

const processQueue = (error: any = null) => {
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) reject(error);
    else resolve();
  });
  failedQueue = [];
};

// Interceptador de resposta para refresh automático
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };

    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then(() => apiClient(originalRequest));
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        await authService.refreshToken();
        processQueue();
        return apiClient(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError);
        authService.logout();
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);
```

## 📝 Tipos TypeScript

### Enums para Consistência

```typescript
export enum PostStatus {
  DRAFT = 'DRAFT',
  PUBLISHED = 'PUBLISHED',
  ARCHIVED = 'ARCHIVED',
}

export enum UserRole {
  USER = 'USER',
  ADMIN = 'ADMIN',
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
  PENDING = 'PENDING',
}
```

### Interfaces de Domínio

```typescript
export interface User {
  id: number;
  username: string;
  email: string;
  first_name?: string;
  last_name?: string;
  bio?: string;
  role: UserRole;
  status: UserStatus;
  created_at: string;
  updated_at: string;
  postsCount?: number;
  publishedPostsCount?: number;
  commentsCount?: number;
  fullName?: string;
}

export interface LoginPayload {
  username: string;
  password: string;
}

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}
```

## 🛣️ Roteamento

### Estrutura de Rotas

```typescript
const routes = [
  {
    path: '/',
    name: 'home',
    component: () => import('@/views/HomeView.vue'),
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/user/ProfileDetailView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/admin/users',
    name: 'admin-users',
    component: () => import('@/views/admin/UserManagementView.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
  },
];
```

## 📚 Boas Práticas

### 1. Organização de Código

- **Separação por domínio**: Organize componentes por funcionalidade
- **Single Responsibility**: Cada componente/função deve ter uma responsabilidade
- **Composables reutilizáveis**: Extraia lógica comum em composables
- **Types centralizados**: Mantenha todas as tipagens em `/types`

### 2. Performance

- **Lazy Loading**: Use importações dinâmicas para rotas
- **Computed Properties**: Use computed para valores derivados
- **Tree Shaking**: Importe apenas o que necessário
- **Code Splitting**: Divida o código por rotas/funcionalidades

### 3. Segurança

- **Token Refresh Automático**: Implemente refresh automático de tokens
- **Route Guards**: Proteja rotas com guards de autenticação
- **Input Validation**: Valide inputs tanto no cliente quanto no servidor
- **Error Boundaries**: Trate erros de forma elegante

### 4. UX/UI

- **Loading States**: Sempre indique quando algo está carregando
- **Error Messages**: Forneça mensagens de erro claras
- **Feedback Visual**: Use snackbars/toasts para feedback
- **Responsividade**: Use breakpoints do Vuetify

### 5. TypeScript

- **Strict Mode**: Use modo strict do TypeScript
- **Interface Segregation**: Crie interfaces específicas
- **Utility Types**: Use utility types quando apropriado
- **Type Guards**: Implemente type guards para runtime checks

### 6. Testes

- **Unit Tests**: Teste componentes isoladamente
- **Integration Tests**: Teste fluxos completos
- **E2E Tests**: Teste cenários de usuário
- **Mock Services**: Mock serviços externos

### 7. Gerenciamento de Estado

- **Estado Local vs Global**: Use estado local quando possível
- **Normalização**: Normalize dados complexos
- **Imutabilidade**: Mantenha estado imutável
- **Persistência**: Persista apenas dados essenciais

### 8. CSS/Styling

- **CSS Modules**: Use scoped styles
- **Design System**: Siga um design system consistente
- **Variables**: Use variáveis CSS para cores/espaçamentos
- **Mobile First**: Desenvolva mobile-first

### 9. API Integration

- **Error Handling**: Trate todos os tipos de erro
- **Retry Logic**: Implemente retry para falhas temporárias
- **Caching**: Cache dados quando apropriado
- **Optimistic Updates**: Use updates otimistas quando possível

### 10. Build & Deploy

- **Environment Variables**: Use variáveis de ambiente
- **Source Maps**: Configure source maps para debugging
- **Bundle Analysis**: Analise o tamanho do bundle
- **CI/CD**: Configure pipelines de CI/CD

## 🔧 Configurações Essenciais

### Vite Config

```typescript
export default defineConfig({
  plugins: [vue(), vuetify()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
});
```

### TypeScript Config

```json
{
  "compilerOptions": {
    "strict": true,
    "noImplicitAny": true,
    "strictNullChecks": true,
    "noImplicitReturns": true,
    "noImplicitThis": true
  }
}
```

---

**📝 Nota**: Este guia foi extraído de um projeto real e deve ser adaptado conforme necessário para cada novo projeto. Mantenha este documento atualizado conforme novos padrões forem implementados.
