import { defineStore } from 'pinia';
import { jwtDecode } from 'jwt-decode';
import * as authService from '../services/authService';
import { getUserById } from '@/services/userService';
import { useUiStore } from './ui';
import type { User } from '@/types/User';
import type {LoginPayload, RegisterPayload, TokenResponse} from '@/types/auth';
import router from '../router';
import {UserRole} from "@/types/enums";

interface JwtPayload {
    sub: string;
    iat: number;
    exp: number;
}

export const useAuthStore = defineStore('auth', {
    state: () => ({
        accessToken: localStorage.getItem('accessToken') || null as string | null,
        refreshToken: localStorage.getItem('refreshToken') || null as string | null,
        user: JSON.parse(localStorage.getItem('user') || 'null') as User | null,
    }),

    getters: {
        isAuthenticated: (state): boolean => !!state.accessToken && !!state.user,
        isAdmin: (state): boolean => state.user?.role === UserRole.ADMIN,
        isActive: (state): boolean => state.user?.status === 'ACTIVE',
        isTokenExpired: (state): boolean => {
            if (!state.accessToken) return true;
            try {
                const { exp } = jwtDecode<JwtPayload>(state.accessToken);
                return Date.now() >= exp * 1000;
            } catch {
                return true;
            }
        },
    },

    actions: {
        _setAuthData(accessToken: string, refreshToken: string, user: User) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.user = user;

            localStorage.setItem('accessToken', accessToken);
            localStorage.setItem('refreshToken', refreshToken);
            localStorage.setItem('user', JSON.stringify(user));
        },

        _clearAuthData() {
            this.accessToken = null;
            this.refreshToken = null;
            this.user = null;

            localStorage.removeItem('accessToken');
            localStorage.removeItem('refreshToken');
            localStorage.removeItem('user');
        },

        async handleLogin(credentials: LoginPayload) {
            const uiStore = useUiStore();
            uiStore.setLoading(true);
            try {
                const tokenData: TokenResponse = await authService.login(credentials);
                const userData: User = {
                    id: tokenData.user.id,
                    username: tokenData.user.username,
                    email: tokenData.user.email,
                    first_name: tokenData.user.first_name,
                    last_name: tokenData.user.last_name,
                    bio: tokenData.user.bio,
                    role: tokenData.user.role,
                    status: tokenData.user.status,
                    created_at: tokenData.user.created_at,
                    updated_at: tokenData.user.updated_at,

                    postsCount: tokenData.user.postsCount,
                    publishedPostsCount: tokenData.user.publishedPostsCount,
                    commentsCount: tokenData.user.commentsCount,

                    fullName: tokenData.user.fullName,
                };

                this._setAuthData(tokenData.accessToken, tokenData.refreshToken, userData);
                await router.push({ name: 'home' });
                uiStore.showAlert({
                    message: `Bem-vindo, ${userData.first_name || userData.username}!`,
                    type: 'success',
                    timeout: 3000
                });
            } catch (error: any) {
                console.error('Falha no login:', error);
                this._clearAuthData();
                const message = error.response?.data?.message || 'Usuário ou senha inválidos.';
                uiStore.showAlert({ message, type: 'error' });
                throw error;
            } finally {
                uiStore.setLoading(false);
            }
        },

        async handleRegister(userData: RegisterPayload) {
            const uiStore = useUiStore();
            uiStore.setLoading(true);
            try {
                await authService.register(userData);
                await router.push({ name: 'login' });
            } catch (error: any) {
                console.error('Falha no registro:', error);
                const message = error.response?.data?.message || 'Erro ao criar conta. Verifique os dados.';
                uiStore.showAlert({ message, type: 'error' });
                throw error;
            } finally {
                uiStore.setLoading(false);
            }
        },

        async handleLogout() {
            const uiStore = useUiStore();
            this._clearAuthData();
            await router.push({ name: 'login' });
            uiStore.showAlert({ message: 'Você saiu da sua conta.', type: 'info' });
        },

        async rehydrateAuth() {
            if (this.accessToken && !this.user) {
                try {
                    const { sub: userId } = jwtDecode<JwtPayload>(this.accessToken);
                    const userData = await getUserById(Number(userId));
                    this.user = userData;
                    localStorage.setItem('user', JSON.stringify(userData));
                } catch (error) {
                    console.error('Sessão inválida, limpando dados:', error);
                    this._clearAuthData();
                }
            } else if (this.isTokenExpired && this.refreshToken) {
                try {
                    const response = await authService.refreshToken(this.refreshToken);
                    this._setAuthData(response.accessToken, response.refreshToken, response.user as User);
                } catch (error) {
                    console.error('Erro ao renovar token:', error);
                    this._clearAuthData();
                }
            }
        },
    },
});