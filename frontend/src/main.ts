import {createApp} from 'vue'
import App from './App.vue'
import {registerPlugins} from './plugins';
import {useAuthStore} from "@/stores/auth";

let app: ReturnType<typeof createApp> | undefined

app = createApp(App)
registerPlugins(app)

const authStore = useAuthStore()
authStore.rehydrateAuth().then(() => {
    app.mount('#app')
})