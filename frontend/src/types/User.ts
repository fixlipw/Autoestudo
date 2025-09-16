import type { UserRole, UserStatus } from './enums.js';

export interface User {
    id: number;
    username: string;
    email: string;
    first_name?: string;
    last_name?: string;
    password?: string;
    bio?: string;
    role: UserRole
    status: UserStatus
    created_at: string;
    updated_at: string;

    postsCount?: number;
    publishedPostsCount?: number;
    commentsCount?: number;
    fullName?: string;
}