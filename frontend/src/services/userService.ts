import apiClient from './apiClient';
import type { User } from '@/types/User';
import type { Page } from '@/types/api';
import type { UserStatus} from '@/types/enums';

export type UserUpdatePayload = Partial<Pick<User, 'username' | 'email' | 'first_name' | 'last_name' | 'bio' | 'role'>>;
export type PasswordUpdatePayload = [string, string];

export const getUserById = async (userId: number): Promise<User> => {
    const response = await apiClient.get<User>(`/users/${userId}`);
    return response.data;
};

export const getAllUsers = async (page: number = 0, size: number = 10): Promise<Page<User>> => {
    const response = await apiClient.get<Page<User>>('/users', { params: { page, size } });
    return response.data;
};

export const updateUser = async (id: number, userData: UserUpdatePayload): Promise<User> => {
    const response = await apiClient.put<User>(`/users/${id}`, userData);
    return response.data;
};

export const deleteUser = async (id: number): Promise<void> => {
    await apiClient.delete(`/users/${id}`);
};

export const updateUserStatus = async (id: number, status: UserStatus): Promise<User> => {
    const response = await apiClient.patch<User>(`/users/${id}/status`, null, {
        params: { status },
    });
    return response.data;
};

export const updateUserPassword = async (id: number, passwords: PasswordUpdatePayload): Promise<void> => {
    await apiClient.patch(`/users/${id}/password`, passwords);
};

export const checkUserExists = async (params: { username?: string; email?: string }): Promise<boolean> => {
    const response = await apiClient.get<boolean>('/users/validation/exists', { params });
    return response.data;
};