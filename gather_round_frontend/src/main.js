import { createApp } from 'vue'
import App from './App.vue'

import axios from 'axios'
import VueAxios from 'vue-axios'
import LoadScript from 'vue-plugin-load-script'
import VueCookies from 'vue-cookies'
import qs from 'qs'
import CyrillicToTranslit from 'cyrillic-to-translit-js'
import 'bootstrap/dist/css/bootstrap.css'

const app = createApp(App)

app.use(VueAxios, axios)
app.use(LoadScript)
app.use(VueCookies)

app.config.globalProperties.qs = qs
app.config.globalProperties.$CyrillicToTranslit = CyrillicToTranslit

app.mount('#app')
