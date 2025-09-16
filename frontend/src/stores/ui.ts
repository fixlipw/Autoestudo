import { defineStore } from 'pinia';

interface AlertState {
    show: boolean;
    message: string;
    type: 'success' | 'info' | 'warning' | 'error';
}

interface ConfirmDialogState {
    show: boolean;
    title: string;
    message: string;
    resolve: (value: boolean) => void;
}

export const useUiStore = defineStore('ui', {
    state: () => ({
        isLoading: false,
        alert: {
            show: false,
            message: '',
            type: 'info',
        } as AlertState,
        confirmDialog: {
            show: false,
            title: '',
            message: '',
            resolve: () => {},
        } as ConfirmDialogState,
    }),

    actions: {
        setLoading(isLoading: boolean) {
            this.isLoading = isLoading;
        },

        showAlert({ message, type = 'info', timeout = 4000 }: { message: string; type?: AlertState['type']; timeout?: number }) {
            this.alert.show = true;
            this.alert.message = message;
            this.alert.type = type;
            if (timeout > 0) {
                setTimeout(() => {
                    this.hideAlert();
                }, timeout);
            }
        },

        hideAlert() {
            this.alert.show = false;
        },

        showConfirmDialog({ title, message }: { title: string; message: string }): Promise<boolean> {
            this.confirmDialog.title = title;
            this.confirmDialog.message = message;
            this.confirmDialog.show = true;

            return new Promise((resolve) => {
                this.confirmDialog.resolve = resolve;
            });
        },

        resolveConfirmDialog(value: boolean) {
            this.confirmDialog.resolve(value);
            this.confirmDialog.show = false;
        },
    },
});
