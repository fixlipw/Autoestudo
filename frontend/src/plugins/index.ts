import { loadFonts } from './webfontloader'
import vuetify from './vuetify'
import pinia from '../stores'
import router from '../router'

import type { App } from 'vue'

export function registerPlugins (app: App) {
    loadFonts().then(r => r)
    app
        .use(vuetify)
        .use(router)
        .use(pinia)
}