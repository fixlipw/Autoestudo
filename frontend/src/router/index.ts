import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: () => import('@/views/HomeView.vue'),
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('@/views/auth/LoginView.vue'),
        },
        {
            path: '/register',
            name: 'register',
            component: () => import('@/views/auth/RegisterView.vue'),
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
            meta: { requiresAdmin: true },
        },
        {
            path: '/posts/:id',
            name: 'post-detail',
            component: () => import('@/views/posts/PostDetailView.vue'),
        },
        {
            path: '/posts/new',
            name: 'post-create',
            component: () => import('@/views/posts/PostEditView.vue'),
            meta: { requiresAuth: true },
        },
        {
            path: '/posts/edit/:id',
            name: 'post-edit',
            component: () => import('@/views/posts/PostEditView.vue'),
            meta: { requiresAuth: true }
        },
        {
            path: '/users/:id',
            name: 'user-profile',
            component: () => import('@/views/user/ProfileView.vue'),
            meta: { requiresAuth: true }
        }
    ],
});

router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();

    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next({ name: 'login', query: { redirectReason: 'auth' } });
    } else if (to.meta.requiresAdmin && !authStore.isAdmin) {
        next({ name: 'home' });
    } else {
        next();
    }
});

export default router;