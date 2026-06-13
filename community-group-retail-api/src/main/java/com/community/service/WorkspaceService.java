package com.community.service;

import com.community.vo.BusinessDataVO;
import com.community.vo.ProductOverViewVO;
import com.community.vo.OrderOverViewVO;
import com.community.vo.ProductBundleOverViewVO;
import java.time.LocalDateTime;

public interface WorkspaceService {

    /**
     * 根据时间段统计营业数据
     * @param begin
     * @param end
     * @return
     */
    BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end);

    /**
     * 查询订单管理数据
     * @return
     */
    OrderOverViewVO getOrderOverView();

    /**
     * 查询菜品总览
     * @return
     */
    ProductOverViewVO getProductOverView();

    /**
     * 查询套餐总览
     * @return
     */
    ProductBundleOverViewVO getProductBundleOverView();

}
