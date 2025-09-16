import type { PostStatus } from './enums.js';

export interface Post {
    id: number;
    title: string;
    content: string;
    summary?: string;
    status: PostStatus;
    published_at?: string;
    created_at: string;
    updated_at: string;
    author: {
        id: number;
        username: string;
    };
}