/**
 * plugins/vuetify.ts
 *
 * Framework documentation: https://vuetifyjs.com`
 */

// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import { pt } from 'vuetify/locale'

import { createVuetify } from 'vuetify'
import { VDataTable, VDataTableServer } from 'vuetify/components/VDataTable'
import { VBtn } from 'vuetify/components'

const myCustomLightTheme = {
    dark: false,
    colors: {
        background: '#F5F5F5',
        surface: '#FFFFFF',
        primary: '#1976D2',
        secondary: '#424242',
        error: '#D32F2F',
        danger: '#E53935',
        info: '#0288D1',
        success: '#388E3C',
        warning: '#FFA000',
    },
}

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
export default createVuetify({
    components: {
        VDataTable,
        VDataTableServer,
        VBtn,
    },
    theme: {
        defaultTheme: 'myCustomLightTheme',
        themes: {
            myCustomLightTheme,
        },
    },
    locale: {
        locale: 'pt',
        fallback: 'pt',
        messages: { pt },
    },
    aliases: {
        VBtnEdit: VBtn,
        VBtnDelete: VBtn,
        VBtnDetails: VBtn,
        VBtnItens: VBtn,
        VBtnDownload: VBtn
    },
    defaults: {
        VBtnEdit: {
            color: 'info',
            variant: 'text',
            icon: "mdi-pencil",
        },
        VBtnDelete: {
            color: 'error',
            variant: 'text',
            icon: "mdi-delete",
        },
        VBtnDetails: {
            color: '#609B74',
            variant: 'text',
            icon: "mdi-text-search",
        },
        VBtnDownload: {
            color: 'blue-darken-2',
            variant:'text',
            icon: "mdi-download"
        },
        VBtnItens: {
            color: '#609B74',
            variant: 'text',
            icon: "mdi-format-list-bulleted",
        },
    },
})