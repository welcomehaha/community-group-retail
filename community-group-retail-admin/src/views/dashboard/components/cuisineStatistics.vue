<template>
  <div class="container">
    <h2 class="homeTitle">
      商品总览<span><router-link to="product">商品管理</router-link></span>
    </h2>
    <div class="orderviewBox">
      <ul>
        <li>
          <span class="status"><i class="iconfont icon-open" />已上架</span>
          <span class="num">{{ dishesData.sold }}</span>
        </li>
        <li>
          <span class="status"><i class="iconfont icon-stop" />已下架</span>
          <span class="num">{{ dishesData.discontinued }}</span>
        </li>
        <li class="add">
          <router-link to="/product/add">
            <i />
            <p>新增商品</p>
          </router-link>
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator'
import { WorkspaceProductOverview } from '@/api/workspace'
@Component({
  name: 'cuisineview',
})
export default class extends Vue {
  @Prop() private dishesData!: WorkspaceProductOverview
}
</script>
