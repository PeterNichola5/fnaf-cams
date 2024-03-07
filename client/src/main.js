import './assets/main.css'

import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { createStore } from './store'

const app = createApp(App)
const store = createStore();

app.use(router)
app.use(store)
app.mount('#app')
