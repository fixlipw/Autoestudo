import apiClient from './apiClient';
import type { Comment } from '@/types/Comment';
import type { Page } from '@/types/api';

export type CommentCreationPayload = Pick<Comment, 'content'>;

export const getCommentsByPost = async (postId: number, page: number = 0, size: number = 10): Promise<Page<Comment>> => {
    const response = await apiClient.get<Page<Comment>>(`/posts/${postId}/comments`, {
        params: { page, size },
    });
    return response.data;
};

export const getCommentsByAuthor = async (authorId: number, page: number = 0, size: number = 10): Promise<Page<Comment>> => {
    const response = await apiClient.get<Page<Comment>>(`/users/${authorId}/comments`, {
        params: { page, size },
    });
    return response.data;
};

export const createComment = async (postId: number, commentData: CommentCreationPayload): Promise<Comment> => {
    const response = await apiClient.post<Comment>(`/posts/${postId}/comments`, commentData);
    return response.data;
};

export const deleteComment = async (id: number): Promise<void> => {
    await apiClient.delete(`/comments/${id}`);
};