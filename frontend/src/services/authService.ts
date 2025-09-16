import apiClient from './apiClient';
import type { User } from '@/types/User';
import type { LoginPayload, RegisterPayload, TokenResponse } from '@/types/auth';

export const login = async (credentials: LoginPayload): Promise<TokenResponse> => {
    const response = await apiClient.post<TokenResponse>('/auth/login', credentials);
    return response.data;
};

export const register = async (userData: RegisterPayload): Promise<User> => {
    const response = await apiClient.post<User>('/auth/register', userData);
    return response.data;
};

export const refreshToken = async (refreshToken: string): Promise<TokenResponse> => {
    const response = await apiClient.post<TokenResponse>(
        '/auth/refresh',
        refreshToken,
        {
            headers: {
                'Content-Type': 'text/plain',
            },
        }
    );
    return response.data;
};