import apiClient from './apiClient';
import type { Post } from '@/types/Post';
import type { Page } from '@/types/api';
import type { PostStatus } from '@/types/enums';

export type PostCreationPayload = Pick<Post, 'title' | 'content'>;
export type PostUpdatePayload = Pick<Post, 'title' | 'content'>;

export const getAllPosts = async (page: number = 0, size: number = 10): Promise<Page<Post>> => {
    const response = await apiClient.get<Page<Post>>('/posts', {
        params: { page, size },
    });
    return response.data;
}

export const getPublishedPosts = async (page: number = 0, size: number = 10): Promise<Page<Post>> => {
    const response = await apiClient.get<Page<Post>>('/posts/published', {
        params: { page, size },
    });
    return response.data;
};

export const getPostById = async (id: number): Promise<Post> => {
    const response = await apiClient.get<Post>(`/posts/${id}`);
    return response.data;
};

export const getPostsByAuthor = async (authorId: number, page: number = 0, size: number = 10): Promise<Page<Post>> => {
    const response = await apiClient.get<Page<Post>>(`/posts/author/${authorId}`, {
        params: { page, size },
    });
    return response.data;
};

export const createPost = async (postData: PostCreationPayload): Promise<Post> => {
    const response = await apiClient.post<Post>('/posts', postData);
    return response.data;
};

export const updatePost = async (id: number, postData: PostUpdatePayload): Promise<Post> => {
    const response = await apiClient.put<Post>(`/posts/${id}`, postData);
    return response.data;
};

export const updatePostStatus = async (id: number, status: PostStatus): Promise<Post> => {
    const response = await apiClient.patch<Post>(`/posts/${id}/status`, null, {
        params: { status },
    });
    return response.data;
};

export const deletePost = async (id: number): Promise<void> => {
    await apiClient.delete(`/posts/${id}`);
};