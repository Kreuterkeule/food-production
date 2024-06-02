import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import NotFound from '../views/NotFound.vue';

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue'),
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('../views/RegisterView.vue'),
  },
  {
    path: '/createRecipe',
    name: 'createRecipe',
    component: () => import('../views/AddRecipe.vue'),
  },
  {
    path: '/recipe/:id',
    name: 'recipe',
    component: () => import('../views/RecipeView.vue'),
  },
  {
    path: '/recipe/:id/:catchAll(.*)', // so that recipe names can be in the URL
    name: 'recipe',
    component: () => import('../views/RecipeView.vue'),
  },
  {
    path: '/editRecipe/:id',
    name: 'editRecipe',
    component: () => import('../views/AddRecipe.vue'),
  },
  {
    path: '/all',
    name: 'all',
    component: () => import('../views/ArchiveView.vue'),
  },
  {
    path: '/tag/:name',
    name: 'tag',
    component: () => import('../views/ArchiveView.vue'),
  },
  {
    path: '/ingredient/:name',
    name: 'ingredient',
    component: () => import('../views/ArchiveView.vue'),
  },
  {
    path: '/user/:name',
    name: 'user',
    component: () => import('../views/ArchiveView.vue'),
  },
  {
    path: '/users',
    name: 'users',
    component: () => import('../views/UsersView.vue'),
  },
  {
    path: '/search',
    name: 'search',
    component: () => import('../views/SearchView.vue'),
  },
  { path: '/:catchAll(.*)', component: NotFound },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
