<template>
  <div class="container">
    <h2 class="homeTitle">
      组合商品总览<span><router-link to="bundle">组合商品管理</router-link></span>
    </h2>
    <div class="orderviewBox">
      <ul>
        <li>
          <span class="status"><i class="iconfont icon-open" />已上架</span>
          <span class="num">{{ setMealData.sold }}</span>
        </li>
        <li>
          <span class="status"><i class="iconfont icon-stop" />已下架</span>
          <span class="num">{{ setMealData.discontinued }}</span>
        </li>
        <li class="add">
          <router-link to="/bundle/add">
            <i />
            <p>新增组合商品</p>
          </router-link>
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator'
import { WorkspaceBundleOverview } from '@/api/workspace'
@Component({
  name: 'SetMeal',
})
export default class extends Vue {
  @Prop() private setMealData!: WorkspaceBundleOverview
}
</script>
