import { createRouter, createWebHistory } from 'vue-router';
import SrcStartView from '../views/SrcStartView.vue';
import SrcGameView from '../views/SrcGameView.vue';
import HostStartView from '../views/HostStartView.vue';
import HostGameView from '../views/HostGameView.vue';
import IntroView from '../views/IntroView.vue';
import DefaultView from '../views/DefaultView.vue';

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
      component: SrcStartView
    },
    {
      path: '/host',
      name: 'host',
      component: HostStartView
    },
    {
      path: '/guard',
      name: 'guard',
      component: HostGameView
    },
    {
      path: '/cam',
      name: 'cam',
      component: SrcGameView
    },
    {
      path:'/intro',
      name: 'intro',
      component: IntroView
    }
  ]
})

export default router
