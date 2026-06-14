package com.community.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.community.constant.MessageConstant;
import com.community.context.BaseContext;
import com.community.dto.*;
import com.community.entity.*;
import com.community.exception.AddressBookBusinessException;
import com.community.exception.OrderBusinessException;
import com.community.exception.CartItemBusinessException;
import com.community.mapper.*;
import com.community.result.PageResult;
import com.community.service.RetailOrderService;
import com.community.utils.HttpClientUtil;
import com.community.utils.WeChatPayUtil;
import com.community.vo.OrderPaymentVO;
import com.community.vo.OrderStatisticsVO;
import com.community.vo.OrderSubmitVO;
import com.community.vo.OrderVO;
import com.community.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RetailOrderServiceImpl implements RetailOrderService {

    @Autowired
    private RetailOrderMapper retailOrderMapper;
    @Autowired
    private RetailOrderItemMapper retailOrderItemMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private CartItemMapper cartItemMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;

    /**
     * 用户下单
     * @param retailOrderSubmitDTO
     * @return
     */
    @Transactional
    public OrderSubmitVO submitOrder(RetailOrderSubmitDTO retailOrderSubmitDTO) {

        //1. 处理各种业务异常（地址簿为空、购物车数据为空）
        AddressBook addressBook = addressBookMapper.getById(retailOrderSubmitDTO.getAddressBookId());
        if(addressBook == null){
            //抛出业务异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        //检查用户的收货地址是否超出配送范围
        //checkOutOfRange(addressBook.getCityName() + addressBook.getDistrictName() + addressBook.getDetail());

        //查询当前用户的购物车数据
        Long userId = BaseContext.getCurrentId();

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        List<CartItem> cartItemList = cartItemMapper.list(cartItem);

        if(cartItemList == null || cartItemList.size() == 0){
            //抛出业务异常
            throw new CartItemBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        //2. 向订单表插入1条数据
        RetailOrder retailOrder = new RetailOrder();
        BeanUtils.copyProperties(retailOrderSubmitDTO, retailOrder);
        retailOrder.setOrderTime(LocalDateTime.now());
        retailOrder.setPayStatus(RetailOrder.UN_PAID);
        retailOrder.setStatus(RetailOrder.PENDING_PAYMENT);
        retailOrder.setNumber(String.valueOf(System.currentTimeMillis()));
        retailOrder.setAddress(addressBook.getDetail());
        retailOrder.setPhone(addressBook.getPhone());
        retailOrder.setConsignee(addressBook.getConsignee());
        retailOrder.setUserId(userId);

        retailOrderMapper.insert(retailOrder);

        List<RetailOrderItem> retailOrderItemList = new ArrayList<>();
        //3. 向订单明细表插入n条数据
        for (CartItem cart : cartItemList) {
            RetailOrderItem retailOrderItem = new RetailOrderItem();//订单明细
            BeanUtils.copyProperties(cart, retailOrderItem);
            retailOrderItem.setOrderId(retailOrder.getId());//设置当前订单明细关联的订单id
            retailOrderItemList.add(retailOrderItem);
        }

        retailOrderItemMapper.insertBatch(retailOrderItemList);

        //4. 清空当前用户的购物车数据
        cartItemMapper.deleteByUserId(userId);

        //5. 封装VO返回结果
        OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder()
                .id(retailOrder.getId())
                .orderTime(retailOrder.getOrderTime())
                .orderNumber(retailOrder.getNumber())
                .orderAmount(retailOrder.getAmount())
                .build();

        return orderSubmitVO;
    }

    @Value("${community.shop.address}")
    private String shopAddress;

    @Value("${community.baidu.ak}")
    private String ak;

    /**
     * 检查客户的收货地址是否超出配送范围
     * @param address
     */
    private void checkOutOfRange(String address) {
        Map map = new HashMap();
        map.put("address",shopAddress);
        map.put("output","json");
        map.put("ak",ak);

        // 获取门店的经纬度坐标
        String shopCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        JSONObject jsonObject = JSON.parseObject(shopCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("门店地址解析失败");
        }

        //数据解析
        JSONObject location = jsonObject.getJSONObject("result").getJSONObject("location");
        String lat = location.getString("lat");
        String lng = location.getString("lng");
        // 门店经纬度坐标
        String shopLngLat = lat + "," + lng;

        map.put("address",address);
        //获取用户收货地址的经纬度坐标
        String userCoordinate = HttpClientUtil.doGet("https://api.map.baidu.com/geocoding/v3", map);

        jsonObject = JSON.parseObject(userCoordinate);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("收货地址解析失败");
        }

        //数据解析
        location = jsonObject.getJSONObject("result").getJSONObject("location");
        lat = location.getString("lat");
        lng = location.getString("lng");
        //用户收货地址经纬度坐标
        String userLngLat = lat + "," + lng;

        map.put("origin",shopLngLat);
        map.put("destination",userLngLat);
        map.put("steps_info","0");

        //路线规划
        String json = HttpClientUtil.doGet("https://api.map.baidu.com/directionlite/v1/driving", map);

        jsonObject = JSON.parseObject(json);
        if(!jsonObject.getString("status").equals("0")){
            throw new OrderBusinessException("配送路线规划失败");
        }

        //数据解析
        JSONObject result = jsonObject.getJSONObject("result");
        JSONArray jsonArray = (JSONArray) result.get("routes");
        Integer distance = (Integer) ((JSONObject) jsonArray.get(0)).get("distance");

        if(distance > 5000){
            //配送距离超过5000米
            throw new OrderBusinessException("超出配送范围");
        }
    }

    /**
     * 订单支付
     *
     * @param retailOrderPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(RetailOrderPaymentDTO retailOrderPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                retailOrderPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "社区团购即时零售订单", // 商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单号查询当前用户的订单
        RetailOrder retailOrderDB = retailOrderMapper.getByNumberAndUserId(outTradeNo, userId);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        RetailOrder retailOrder = RetailOrder.builder()
                .id(retailOrderDB.getId())
                .status(RetailOrder.TO_BE_CONFIRMED)
                .payStatus(RetailOrder.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        retailOrderMapper.update(retailOrder);

        //通过websocket向客户端浏览器推送消息 type orderId content
        Map map = new HashMap();
        map.put("type",1); // 1表示来单提醒 2表示客户催单
        map.put("orderId",retailOrderDB.getId());
        map.put("content","订单号：" + outTradeNo);

        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }

    /**
     * 用户端订单分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        // 设置分页
        PageHelper.startPage(pageNum, pageSize);

        RetailOrderPageQueryDTO retailOrderPageQueryDTO = new RetailOrderPageQueryDTO();
        retailOrderPageQueryDTO.setUserId(BaseContext.getCurrentId());
        retailOrderPageQueryDTO.setStatus(status);

        // 分页条件查询
        Page<RetailOrder> page = retailOrderMapper.pageQuery(retailOrderPageQueryDTO);

        List<OrderVO> list = new ArrayList();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (RetailOrder retailOrder : page) {
                Long orderId = retailOrder.getId();// 订单id

                // 查询订单明细
                List<RetailOrderItem> retailOrderItems = retailOrderItemMapper.getByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(retailOrder, orderVO);
                orderVO.setOrderDetailList(retailOrderItems);

                list.add(orderVO);
            }
        }
        return new PageResult(page.getTotal(), list);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    public OrderVO details(Long id) {
        // 根据id查询订单
        RetailOrder retailOrder = retailOrderMapper.getById(id);

        // 查询该订单对应的菜品/套餐明细
        List<RetailOrderItem> retailOrderItemList = retailOrderItemMapper.getByOrderId(retailOrder.getId());

        // 将该订单及其详情封装到OrderVO并返回
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(retailOrder, orderVO);
        orderVO.setOrderDetailList(retailOrderItemList);

        return orderVO;
    }

    /**
     * 用户取消订单
     *
     * @param id
     */
    public void userCancelById(Long id) throws Exception {
        // 根据id查询订单
        RetailOrder retailOrderDB = retailOrderMapper.getById(id);

        // 校验订单是否存在
        if (retailOrderDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 订单状态 1待付款 2待履约接单 3已接单 4履约中 5已完成 6已取消
        if (retailOrderDB.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        RetailOrder retailOrder = new RetailOrder();
        retailOrder.setId(retailOrderDB.getId());

        // 订单处于待履约接单状态下取消，需要进行退款
        if (retailOrderDB.getStatus().equals(RetailOrder.TO_BE_CONFIRMED)) {
            //调用微信支付退款接口
            weChatPayUtil.refund(
                    retailOrderDB.getNumber(), //商户订单号
                    retailOrderDB.getNumber(), //商户退款单号
                    new BigDecimal(0.01),//退款金额，单位 元
                    new BigDecimal(0.01));//原订单金额

            //支付状态修改为 退款
            retailOrder.setPayStatus(RetailOrder.REFUND);
        }

        // 更新订单状态、取消原因、取消时间
        retailOrder.setStatus(RetailOrder.CANCELLED);
        retailOrder.setCancelReason("用户取消");
        retailOrder.setCancelTime(LocalDateTime.now());
        retailOrderMapper.update(retailOrder);
    }

    /**
     * 再来一单
     *
     * @param id
     */
    public void repetition(Long id) {
        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询当前订单详情
        List<RetailOrderItem> retailOrderItemList = retailOrderItemMapper.getByOrderId(id);

        // 将订单详情对象转换为购物车对象
        List<CartItem> cartItemList = retailOrderItemList.stream().map(x -> {
            CartItem cartItem = new CartItem();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, cartItem, "id");
            cartItem.setUserId(userId);
            cartItem.setCreateTime(LocalDateTime.now());

            return cartItem;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        cartItemMapper.insertBatch(cartItemList);
    }

    /**
     * 订单搜索
     *
     * @param retailOrderPageQueryDTO
     * @return
     */
    public PageResult conditionSearch(RetailOrderPageQueryDTO retailOrderPageQueryDTO) {
        PageHelper.startPage(retailOrderPageQueryDTO.getPage(), retailOrderPageQueryDTO.getPageSize());

        Page<RetailOrder> page = retailOrderMapper.pageQuery(retailOrderPageQueryDTO);

        // 部分订单状态，需要额外返回订单菜品信息，将RetailOrder转化为OrderVO
        List<OrderVO> orderVOList = getOrderVOList(page);

        return new PageResult(page.getTotal(), orderVOList);
    }

    private List<OrderVO> getOrderVOList(Page<RetailOrder> page) {
        // 需要返回订单菜品信息，自定义OrderVO响应结果
        List<OrderVO> orderVOList = new ArrayList<>();

        List<RetailOrder> retailOrderList = page.getResult();
        if (!CollectionUtils.isEmpty(retailOrderList)) {
            for (RetailOrder retailOrder : retailOrderList) {
                // 将共同字段复制到OrderVO
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(retailOrder, orderVO);
                String orderProductes = getOrderProductesStr(retailOrder);

                // 将订单菜品信息封装到orderVO中，并添加到orderVOList
                orderVO.setOrderProductes(orderProductes);
                orderVOList.add(orderVO);
            }
        }
        return orderVOList;
    }

    /**
     * 根据订单id获取菜品信息字符串
     *
     * @param retailOrder
     * @return
     */
    private String getOrderProductesStr(RetailOrder retailOrder) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<RetailOrderItem> retailOrderItemList = retailOrderItemMapper.getByOrderId(retailOrder.getId());

        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> orderProductList = retailOrderItemList.stream().map(x -> {
            String orderProduct = x.getName() + "*" + x.getNumber() + ";";
            return orderProduct;
        }).collect(Collectors.toList());

        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", orderProductList);
    }

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    public OrderStatisticsVO statistics() {
        // 根据状态，分别查询出待履约接单、待履约配送、履约中的订单数量
        Integer toBeConfirmed = retailOrderMapper.countStatus(RetailOrder.TO_BE_CONFIRMED);
        Integer confirmed = retailOrderMapper.countStatus(RetailOrder.CONFIRMED);
        Integer deliveryInProgress = retailOrderMapper.countStatus(RetailOrder.DELIVERY_IN_PROGRESS);

        // 将查询出的数据封装到orderStatisticsVO中响应
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * 接单
     *
     * @param retailOrderConfirmDTO
     */
    public void confirm(RetailOrderConfirmDTO retailOrderConfirmDTO) {
        RetailOrder retailOrder = RetailOrder.builder()
                .id(retailOrderConfirmDTO.getId())
                .status(RetailOrder.CONFIRMED)
                .build();

        retailOrderMapper.update(retailOrder);
    }

    /**
     * 拒单
     *
     * @param retailOrderRejectionDTO
     */
    public void rejection(RetailOrderRejectionDTO retailOrderRejectionDTO) throws Exception {
        // 根据id查询订单
        RetailOrder retailOrderDB = retailOrderMapper.getById(retailOrderRejectionDTO.getId());

        // 订单只有存在且状态为2（待履约接单）才可以拒单
        if (retailOrderDB == null || !retailOrderDB.getStatus().equals(RetailOrder.TO_BE_CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        //支付状态
        Integer payStatus = retailOrderDB.getPayStatus();
        if (payStatus == RetailOrder.PAID) {
            //用户已支付，需要退款
            String refund = weChatPayUtil.refund(
                    retailOrderDB.getNumber(),
                    retailOrderDB.getNumber(),
                    new BigDecimal(0.01),
                    new BigDecimal(0.01));
            log.info("申请退款：{}", refund);
        }

        // 拒单需要退款，根据订单id更新订单状态、拒单原因、取消时间
        RetailOrder retailOrder = new RetailOrder();
        retailOrder.setId(retailOrderDB.getId());
        retailOrder.setStatus(RetailOrder.CANCELLED);
        retailOrder.setRejectionReason(retailOrderRejectionDTO.getRejectionReason());
        retailOrder.setCancelTime(LocalDateTime.now());

        retailOrderMapper.update(retailOrder);
    }

    /**
     * 取消订单
     *
     * @param retailOrderCancelDTO
     */
    public void cancel(RetailOrderCancelDTO retailOrderCancelDTO) throws Exception {
        // 根据id查询订单
        RetailOrder retailOrderDB = retailOrderMapper.getById(retailOrderCancelDTO.getId());

        //支付状态
        Integer payStatus = retailOrderDB.getPayStatus();
        if (payStatus == 1) {
            //用户已支付，需要退款
            String refund = weChatPayUtil.refund(
                    retailOrderDB.getNumber(),
                    retailOrderDB.getNumber(),
                    new BigDecimal(0.01),
                    new BigDecimal(0.01));
            log.info("申请退款：{}", refund);
        }

        // 管理端取消订单需要退款，根据订单id更新订单状态、取消原因、取消时间
        RetailOrder retailOrder = new RetailOrder();
        retailOrder.setId(retailOrderCancelDTO.getId());
        retailOrder.setStatus(RetailOrder.CANCELLED);
        retailOrder.setCancelReason(retailOrderCancelDTO.getCancelReason());
        retailOrder.setCancelTime(LocalDateTime.now());
        retailOrderMapper.update(retailOrder);
    }

    /**
     * 派送订单
     *
     * @param id
     */
    public void delivery(Long id) {
        // 根据id查询订单
        RetailOrder retailOrderDB = retailOrderMapper.getById(id);

        // 校验订单是否存在，并且状态为3
        if (retailOrderDB == null || !retailOrderDB.getStatus().equals(RetailOrder.CONFIRMED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        RetailOrder retailOrder = new RetailOrder();
        retailOrder.setId(retailOrderDB.getId());
        // 更新订单状态，状态转为履约中
        retailOrder.setStatus(RetailOrder.DELIVERY_IN_PROGRESS);

        retailOrderMapper.update(retailOrder);
    }

    /**
     * 完成订单
     *
     * @param id
     */
    public void complete(Long id) {
        // 根据id查询订单
        RetailOrder retailOrderDB = retailOrderMapper.getById(id);

        // 校验订单是否存在，并且状态为4
        if (retailOrderDB == null || !retailOrderDB.getStatus().equals(RetailOrder.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        RetailOrder retailOrder = new RetailOrder();
        retailOrder.setId(retailOrderDB.getId());
        // 更新订单状态,状态转为完成
        retailOrder.setStatus(RetailOrder.COMPLETED);
        retailOrder.setDeliveryTime(LocalDateTime.now());

        retailOrderMapper.update(retailOrder);
    }

    /**
     * 客户催单
     * @param id
     */
    public void reminder(Long id) {
        // 根据id查询订单
        RetailOrder retailOrderDB = retailOrderMapper.getById(id);

        // 校验订单是否存在
        if (retailOrderDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Map map = new HashMap();
        map.put("type",2); //1表示来单提醒 2表示客户催单
        map.put("orderId",id);
        map.put("content","订单号：" + retailOrderDB.getNumber());

        //通过websocket向客户端浏览器推送消息
        webSocketServer.sendToAllClient(JSON.toJSONString(map));
    }
}
