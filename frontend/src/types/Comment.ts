import type { User } from './User.js';
import type { Post } from './Post.js';

export interface Comment {
    id: number;
    content: string;
    active: boolean;
    created_at: string;
    updated_at: string;

    author: Pick<User, 'id' | 'username' | 'fullName'>;
    post: Pick<Post, 'id' | 'title'>;
}