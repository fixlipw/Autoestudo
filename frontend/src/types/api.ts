export interface Page<T> {
    content: T[];
    page: {
        size: number;
        number: number;
        total_elements: number;
        total_pages: number;
    };
}