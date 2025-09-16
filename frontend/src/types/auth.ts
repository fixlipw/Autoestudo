import type { User } from './User';

export type LoginPayload = Pick<User, 'username' | 'password'>;

export type RegisterPayload = Omit<User, 'id' | 'created_at' | 'updated_at' | 'postsCount' | 'publishedPostsCount' | 'commentsCount' | 'fullName'>;

export interface TokenResponse {
    accessToken: string;
    refreshToken: string;
    user: {
        id: number;
        username: string;
        email: string;
        first_name?: string;
        last_name?: string;
        bio?: string;
        role: User['role'];
        status: User['status'];
        created_at: string;
        updated_at: string;

        postsCount?: number;
        publishedPostsCount?: number;
        commentsCount?: number;
        fullName?: string;
    }
}