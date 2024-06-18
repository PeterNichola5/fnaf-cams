import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import HostView from '../views/HostView.vue';
import GuardView from '../views/GuardView.vue';
import SourceView from '../views/SourceView.vue';
import IntroView from '../views/IntroView.vue';
import DefaultView from '@/views/DefaultView.vue';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'root',
      component: DefaultView
    },
    {
      path: '/connected',
      name: 'source',
      component: HomeView
    },
    {
      path: '/host',
      name: 'host',
      component: HostView
    },
    {
      path: '/guard',
      name: 'guard',
      component: GuardView
    },
    {
      path: '/cam',
      name: 'cam',
      component: SourceView
    },
    {
      path:'/intro',
      name: 'intro',
      component: IntroView
    }
  ]
})

export default router
