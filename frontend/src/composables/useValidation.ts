import {computed, reactive} from 'vue';

export interface ValidationRule {
    (value: any): boolean | string;
}

export interface FieldValidation {
    value: any;
    rules: ValidationRule[];
    errors: string[];
    touched: boolean;
    valid: boolean;
}

export interface FormValidation {
    [key: string]: FieldValidation;
}

export const useValidation = () => {
    const form = reactive<FormValidation>({});

    // Regras de validação comuns
    const rules = {
        required: (message = 'Este campo é obrigatório'): ValidationRule =>
            (value) => !!value || message,

        minLength: (min: number, message?: string): ValidationRule =>
            (value) => !value || value.length >= min || message || `Mínimo de ${min} caracteres`,

        maxLength: (max: number, message?: string): ValidationRule =>
            (value) => !value || value.length <= max || message || `Máximo de ${max} caracteres`,

        email: (message = 'E-mail deve ser válido'): ValidationRule =>
            (value) => !value || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value) || message,

        password: (message = 'Senha deve ter pelo menos 8 caracteres, uma letra maiúscula, uma minúscula e um número'): ValidationRule =>
            (value) => !value || /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{8,}$/.test(value) || message,

        username: (message = 'Nome de usuário deve ter 3-30 caracteres alfanuméricos'): ValidationRule =>
            (value) => !value || /^[a-zA-Z0-9_]{3,30}$/.test(value) || message,

        confirmPassword: (originalPassword: () => string, message = 'Senhas não coincidem'): ValidationRule =>
            (value) => !value || value === originalPassword() || message,

        noSpaces: (message = 'Este campo não pode conter espaços'): ValidationRule =>
            (value) => !value || !/\s/.test(value) || message,

        custom: (validator: (value: any) => boolean, message: string): ValidationRule =>
            (value) => validator(value) || message,
    };

    // Criar campo de validação
    const createField = (initialValue: any = '', validationRules: ValidationRule[] = []): FieldValidation => {
        return reactive({
            value: initialValue,
            rules: validationRules,
            errors: [] as string[],
            touched: false,
            valid: true,
        });
    };

    const addField = (name: string, initialValue: any = '', validationRules: ValidationRule[] = []) => {
        form[name] = createField(initialValue, validationRules);
        return form[name];
    };

    const validateField = (fieldName: string): boolean => {
        const field = form[fieldName];
        if (!field) return true;

        field.errors = [];
        field.touched = true;

        for (const rule of field.rules) {
            const result = rule(field.value);
            if (result !== true) {
                field.errors.push(typeof result === 'string' ? result : 'Campo inválido');
            }
        }

        field.valid = field.errors.length === 0;
        return field.valid;
    };

    const validateForm = (): boolean => {
        let isValid = true;

        for (const fieldName in form) {
            const fieldValid = validateField(fieldName);
            if (!fieldValid) isValid = false;
        }

        return isValid;
    };

    const clearValidation = (fieldName?: string) => {
        if (fieldName && form[fieldName]) {
            form[fieldName].errors = [];
            form[fieldName].touched = false;
            form[fieldName].valid = true;
        } else {
            Object.keys(form).forEach(key => {
                form[key].errors = [];
                form[key].touched = false;
                form[key].valid = true;
            });
        }
    };

    const resetForm = (newValues?: Record<string, any>) => {
        Object.keys(form).forEach(key => {
            form[key].value = newValues?.[key] || '';
            form[key].errors = [];
            form[key].touched = false;
            form[key].valid = true;
        });
    };

    // Computed para verificar se o formulário é válido
    const isFormValid = computed(() => {
        return Object.values(form).every(field => field.valid && (!field.rules.length || field.touched));
    });

    // Computed para verificar se algum campo foi tocado
    const isFormTouched = computed(() => {
        return Object.values(form).some(field => field.touched);
    });

    // Obter erros de um campo específico
    const getFieldErrors = (fieldName: string): string[] => {
        return form[fieldName]?.errors || [];
    };

    // Verificar se um campo tem erros
    const hasFieldErrors = (fieldName: string): boolean => {
        return form[fieldName]?.errors.length > 0 || false;
    };

    // Obter valor de um campo
    const getFieldValue = (fieldName: string): any => {
        return form[fieldName]?.value;
    };

    // Definir valor de um campo
    const setFieldValue = (fieldName: string, value: any) => {
        if (form[fieldName]) {
            form[fieldName].value = value;
            if (form[fieldName].touched) {
                validateField(fieldName);
            }
        }
    };

    // Marcar campo como tocado
    const touchField = (fieldName: string) => {
        if (form[fieldName]) {
            form[fieldName].touched = true;
        }
    };

    return {
        form,
        rules,
        addField,
        validateField,
        validateForm,
        clearValidation,
        resetForm,
        isFormValid,
        isFormTouched,
        getFieldErrors,
        hasFieldErrors,
        getFieldValue,
        setFieldValue,
        touchField,
    };
}