### Filosofia da Arquitetura

O objetivo é manter uma clara **separação de responsabilidades (Separation of Concerns)**:

1.  **Views (Páginas)**: Contêineres de alto nível que correspondem a rotas. Apenas montam os componentes e orquestram a lógica da página.
2.  **Components**: Peças reutilizáveis da UI. Podem ser "inteligentes" (com lógica) ou "burros" (apenas de apresentação).
3.  **Services (API)**: Camada responsável por toda a comunicação com o backend. Nenhum componente ou view deve fazer chamadas de API diretamente.
4.  **Store (Pinia)**: Gerenciador de estado global. Ideal para dados que são compartilhados por toda a aplicação, como informações do usuário autenticado e estado da UI (ex: notificações).
5.  **Router**: Mapeia as URLs para as Views e controla a navegação, incluindo guardas de rota para autenticação.
6.  **Types**: Definições de tipos e interfaces do TypeScript para garantir a consistência dos dados em toda a aplicação.

-----

### Estrutura de Pastas Detalhada

```
/src/
├── assets/              # Imagens, fontes, e arquivos de estilo globais
├── components/          # Componentes Vue reutilizáveis
│   ├── comments/        # Componentes específicos para comentários
│   │   ├── CommentForm.vue
│   │   └── CommentList.vue
│   ├── posts/           # Componentes específicos para posts
│   │   ├── PostCard.vue
│   │   └── PostForm.vue
│   ├── ui/              # Componentes de UI genéricos e "burros"
│   │   ├── AppAlert.vue
│   │   └── ConfirmDialog.vue
│   └── layout/          # Componentes principais de layout
│       ├── TheNavbar.vue
│       ├── TheFooter.vue
│       └── AppSidebar.vue
├── layouts/             # Layouts de página (ex: padrão, admin, autenticação)
│   ├── DefaultLayout.vue
│   └── AuthLayout.vue
├── plugins/             # Configuração de plugins (Vuetify, Axios)
│   ├── axios.ts
│   └── vuetify.ts
├── router/              # Configuração do Vue Router
│   └── index.ts
├── services/            # Camada de comunicação com a API
│   ├── apiClient.ts     # Instância configurada do Axios (com interceptors)
│   ├── authService.ts
│   ├── postService.ts
│   ├── commentService.ts
│   └── userService.ts
├── stores/              # Módulos de estado do Pinia
│   ├── auth.ts          # Estado de autenticação, usuário, token
│   └── ui.ts            # Estado da UI (loading, notificações, dialogs)
├── types/               # Interfaces e tipos do TypeScript
│   ├── api.ts           # Tipos de resposta da API (ex: Page<T>)
│   ├── Post.ts
│   ├── User.ts
│   └── Comment.ts
├── utils/               # Funções utilitárias
│   └── formatDate.ts
├── views/ (ou pages/)   # Componentes de página, ligados às rotas
│   ├── HomeView.vue
│   ├── posts/
│   │   ├── PostDetailView.vue
│   │   └── PostEditView.vue
│   ├── auth/
│   │   ├── LoginView.vue
│   │   └── RegisterView.vue
│   └── admin/
│       └── UserManagementView.vue
└── main.ts              # Ponto de entrada da aplicação
```

-----

### Detalhamento dos Módulos Principais

#### 1\. `services/apiClient.ts`

Este é o coração da sua comunicação com o backend. Use o Axios para criar uma instância centralizada que gerencia a URL base e os tokens de autenticação.

```typescript
// src/services/apiClient.ts
import axios from 'axios';
import { useAuthStore } from '@/stores/auth';

const apiClient = axios.create({
    baseURL: 'http://localhost:8080/api', // URL do seu backend
});

// Interceptor para adicionar o token JWT a cada requisição
apiClient.interceptors.request.use(config => {
    const authStore = useAuthStore();
    if (authStore.token) {
        config.headers.Authorization = `Bearer ${authStore.token}`;
    }
    return config;
});

// Opcional: Interceptor para lidar com refresh token em caso de 401
// ...

export default apiClient;
```

#### 2\. `services/postService.ts`

Um exemplo de serviço, que consome o `apiClient` para interagir com os endpoints de posts.

```typescript
// src/services/postService.ts
import apiClient from './apiClient';
import type { Post } from '@/types/Post';
import type { Page } from '@/types/api';

export const getPublishedPosts = (page: number, size: number): Promise<Page<Post>> => {
    return apiClient.get('/posts/published', { params: { page, size } });
};

export const getPostById = (id: number): Promise<Post> => {
    return apiClient.get(`/posts/${id}`);
};

export const createPost = (postData: Partial<Post>): Promise<Post> => {
    return apiClient.post('/posts', postData);
};
// ... outros métodos (update, delete, etc.)
```

#### 3\. `types/Post.ts`

As interfaces TypeScript devem espelhar as entidades Java para garantir consistência.

```typescript
// src/types/Post.ts
import type { PostStatus } from './PostStatus'; // você pode criar um enum para isso

export interface Post {
    id: number;
    title: string;
    content: string;
    summary?: string;
    status: PostStatus;
    publishedAt?: string; // As datas vêm como string no JSON
    createdAt: string;
    updatedAt: string;
    author: { // Aninhado para evitar referências circulares complexas
        id: number;
        username: string;
    };
    commentsCount: number;
}
```

Isso corresponde à sua entidade `Post.java`.

#### 4\. `stores/auth.ts`

Use o Pinia para gerenciar o estado de autenticação. Ele persistirá os dados do usuário e o token (ex: no `localStorage`) e fornecerá getters e ações para toda a aplicação.

```typescript
// src/stores/auth.ts
import { defineStore } from 'pinia';
import type { User } from '@/types/User';
import * as authService from '@/services/authService';

export const useAuthStore = defineStore('auth', {
    state: () => ({
        token: localStorage.getItem('token') || null,
        user: JSON.parse(localStorage.getItem('user') || 'null') as User | null,
    }),
    getters: {
        isAuthenticated: (state) => !!state.token && !!state.user,
        isAdmin: (state) => state.user?.role === 'ADMIN',
    },
    actions: {
        async login(credentials: any) {
            const { accessToken, refreshToken } = await authService.login(credentials);
            // Aqui você buscaria os dados do usuário
            // e os salvaria no estado e no localStorage
            this.token = accessToken;
            localStorage.setItem('token', accessToken);
            // ...
        },
        logout() {
            this.token = null;
            this.user = null;
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            // Redirecionar para a página de login
        },
    },
});
```

#### 5\. `router/index.ts`

Configure as rotas e use os "navigation guards" para proteger as páginas que exigem autenticação ou permissões específicas.

```typescript
// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import HomeView from '@/views/HomeView.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        { path: '/', name: 'home', component: HomeView },
        {
            path: '/posts/new',
            name: 'post-create',
            component: () => import('@/views/posts/PostEditView.vue'),
            meta: { requiresAuth: true }, // Rota protegida
        },
        {
            path: '/admin/users',
            name: 'admin-users',
            component: () => import('@/views/admin/UserManagementView.vue'),
            meta: { requiresAuth: true, requiresAdmin: true }, // Rota de Admin
        },
        // ... outras rotas
    ],
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();

    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next({ name: 'login' }); // Redireciona para o login
    } else if (to.meta.requiresAdmin && !authStore.isAdmin) {
        next({ name: 'home' }); // Redireciona para home se não for admin
    } else {
        next(); // Permite a navegação
    }
});

export default router;
```

### Fluxo de Trabalho (Exemplo: Criar um Post)

1.  **Usuário clica em "Novo Post"**: A aplicação navega para a rota `/posts/new`.
2.  **Guarda de Rota**: O `router.beforeEach` verifica se `isAuthenticated` é `true`. Se não, redireciona para `/login`.
3.  **Carregamento da View**: A `PostEditView.vue` é renderizada. Ela utiliza o componente `PostForm.vue`.
4.  **Preenchimento e Envio**: O usuário preenche o formulário em `PostForm.vue` e clica em "Salvar".
5.  **Ação do Componente**: `PostEditView.vue` chama a função `createPost` do `postService.ts`, passando os dados do formulário.
6.  **Chamada de API**: `postService` usa o `apiClient` para fazer a requisição `POST /api/posts`. O interceptor do `apiClient` adiciona automaticamente o token de autenticação.
7.  **Feedback ao Usuário**: Após o sucesso da requisição, a `PostEditView.vue` usa a `uiStore` (ex: `uiStore.showSuccessAlert('Post criado!')`) e redireciona o usuário para a página do novo post.

Seguindo essa estrutura, você terá uma base de código organizada, fácil de manter e testar, que se integra perfeitamente com a arquitetura do seu backend.