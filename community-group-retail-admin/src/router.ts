import Vue from 'vue';
import Router from 'vue-router';
import Layout from '@/layout/index.vue';
// import {
//   getToken,
//   setToken,
//   removeToken,
//   getStoreId,
//   setStoreId,
//   removeStoreId,
//   setUserInfo,
//   getUserInfo,
//   removeUserInfo
// } from "@/utils/cookies";
// import store from "@/store";

Vue.use(Router);

const router = new Router({
  scrollBehavior: (to, from, savedPosition) => {
    if (savedPosition) {
      return savedPosition;
    }
    return { x: 0, y: 0 };
  },
  base: process.env.BASE_URL,
  routes: [
    {
      path: '/login',
      component: () => import('@/views/login/index.vue'),
      meta: { title: '社区团购即时零售平台', hidden: true, notNeedAuth: true }
    },
    {
      path: '/404',
      component: () => import('@/views/404.vue'),
      meta: { title: '社区团购即时零售平台', hidden: true, notNeedAuth: true }
    },
    {
      path: '/',
      component: Layout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          component: () => import('@/views/dashboard/index.vue'),
          name: 'Dashboard',
          meta: {
            title: '运营工作台',
            icon: 'dashboard',
            affix: true,
            permission: 'dashboard:workspace:view'
          }
        },
        {
          path: '/statistics',
          component: () => import('@/views/statistics/index.vue'),
          meta: {
            title: '交易分析',
            icon: 'icon-statistics',
            permission: 'report:statistics:view'
          }
        },
        {
          path: '/ai-operation',
          component: () => import('@/views/aiOperation/index.vue'),
          meta: {
            title: 'AI运营',
            icon: 'icon-statistics',
            permission: 'ai:operation:view'
          }
        },
        {
          path: 'order',
          component: () => import('@/views/orderDetails/index.vue'),
          meta: {
            title: '订单管理',
            icon: 'icon-order',
            permission: 'order:manage:view'
          }
        },
        {
          path: 'bundle',
          component: () => import('@/views/bundle/index.vue'),
          meta: {
            title: '组合商品管理',
            icon: 'icon-combo',
            permission: 'bundle:manage:view'
          }
        },
        {
          path: 'product',
          component: () => import('@/views/product/index.vue'),
          meta: {
            title: '商品管理',
            icon: 'icon-dish',
            permission: 'product:manage:view'
          }
        },
        {
          path: '/product/add',
          component: () => import('@/views/product/addDishtype.vue'),
          meta: {
            title: '新增商品',
            hidden: true,
            permission: 'product:item:add'
          }
        },

        {
          path: 'category',
          component: () => import('@/views/product-category/index.vue'),
          meta: {
            title: '商品分类管理',
            icon: 'icon-category',
            permission: 'product:category:view'
          }
        },
        {
          path: 'employee',
          component: () => import('@/views/employee/index.vue'),
          meta: {
            title: '员工管理',
            icon: 'icon-employee',
            permission: 'employee:user:view'
          }
        },

        {
          path: '/employee/add',
          component: () => import('@/views/employee/addEmployee.vue'),
          meta: {
            title: '添加/修改员工',
            hidden: true,
            permission: 'employee:user:add'
          }
        },

        {
          path: '/bundle/add',
          component: () => import('@/views/bundle/addSetmeal.vue'),
          meta: {
            title: '新增组合商品',
            hidden: true,
            permission: 'bundle:item:add'
          }
        }
      ]
    },
    {
      path: '*',
      redirect: '/404',
      meta: { hidden: true }
    }
  ]
});

export default router;
