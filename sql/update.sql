CREATE DATABASE IF NOT EXISTS `community_retail`;
USE `community_retail`;

DROP TABLE IF EXISTS `addr_book`;
CREATE TABLE `addr_book` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uid` bigint NOT NULL COMMENT '用户ID',
  `consignee` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人',
  `sex` varchar(2) COLLATE utf8_bin DEFAULT NULL COMMENT '性别',
  `phone` varchar(11) COLLATE utf8_bin NOT NULL COMMENT '手机号',
  `prov_code` varchar(12) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '省级区划编号',
  `prov_name` varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '省级名称',
  `city_code` varchar(12) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '市级区划编号',
  `city_name` varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '市级名称',
  `dist_code` varchar(12) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '区级区划编号',
  `dist_name` varchar(32) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '区级名称',
  `detail` varchar(200) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '详细地址',
  `label` varchar(100) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '地址标签',
  `is_def` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认地址 0否 1是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='收货地址表';

-- 收货地址演示数据，便于前端地址簿与下单页展示
INSERT INTO `addr_book` VALUES (1,1,'张三','1','13800000001','110000','北京市','110100','北京市','110105','朝阳区','望京街道SOHO T3 18层','公司',1);
INSERT INTO `addr_book` VALUES (2,2,'李四','1','13800000002','310000','上海市','310100','上海市','310115','浦东新区','张江高科博云路2号','家',1);
INSERT INTO `addr_book` VALUES (3,3,'王五','0','13800000003','440000','广东省','440100','广州市','440106','天河区','珠江新城花城大道88号','家',1);

DROP TABLE IF EXISTS `cat`;
CREATE TABLE `cat` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` int DEFAULT NULL COMMENT '分类类型 1商品分类 2组合商品分类',
  `name` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '分类名称',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序值',
  `status` int DEFAULT NULL COMMENT '分类状态 0:禁用，1:启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_uid` bigint DEFAULT NULL COMMENT '创建人',
  `update_uid` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_cat_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='商品分类表';

INSERT INTO `cat` VALUES (11,1,'新鲜蔬菜',1,1,'2022-06-09 22:09:18','2022-06-09 22:09:18',1,1);
INSERT INTO `cat` VALUES (12,1,'时令水果',2,1,'2022-06-09 22:09:32','2022-06-09 22:18:53',1,1);
INSERT INTO `cat` VALUES (13,2,'家庭优选组合',1,1,'2022-06-09 22:11:38','2022-06-10 11:04:40',1,1);
INSERT INTO `cat` VALUES (15,2,'居家场景组合',2,1,'2022-06-09 22:14:10','2022-06-10 11:04:48',1,1);
INSERT INTO `cat` VALUES (16,1,'肉禽蛋品',3,1,'2022-06-09 22:15:37','2022-08-31 14:27:25',1,1);
INSERT INTO `cat` VALUES (17,1,'粮油调味',4,1,'2022-06-09 22:16:14','2022-08-31 14:39:44',1,1);
INSERT INTO `cat` VALUES (18,1,'乳品烘焙',5,1,'2022-06-09 22:17:42','2022-06-09 22:17:42',1,1);
INSERT INTO `cat` VALUES (19,1,'休闲零食',6,1,'2022-06-09 22:18:12','2022-06-09 22:18:28',1,1);
INSERT INTO `cat` VALUES (20,1,'酒水饮料',7,1,'2022-06-09 22:22:29','2022-06-09 22:23:45',1,1);
INSERT INTO `cat` VALUES (21,1,'日用百货',8,1,'2022-06-10 10:51:47','2022-06-10 10:51:47',1,1);

DROP TABLE IF EXISTS `prod`;
CREATE TABLE `prod` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '商品名称',
  `cat_id` bigint NOT NULL COMMENT '商品分类ID',
  `price` decimal(10,2) DEFAULT NULL COMMENT '商品售价',
  `img` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '商品图片',
  `descr` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '商品描述',
  `status` int DEFAULT '1' COMMENT '售卖状态 0停售 1上架',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_uid` bigint DEFAULT NULL COMMENT '创建人',
  `update_uid` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_prod_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='商品表';

INSERT INTO `prod` VALUES (46,'西红柿',11,4.80,'https://dummyimage.com/600x600/f7f7f7/333333&text=Tomato','新鲜采摘，适合炒菜和凉拌',1,'2022-06-09 22:40:47','2022-06-09 22:40:47',1,1);
INSERT INTO `prod` VALUES (47,'黄瓜',11,3.90,'https://dummyimage.com/600x600/f7f7f7/333333&text=Cucumber','清脆爽口，适合凉拌与沙拉',1,'2022-06-10 09:18:49','2022-06-10 09:18:49',1,1);
INSERT INTO `prod` VALUES (48,'土豆',11,4.20,'https://dummyimage.com/600x600/f7f7f7/333333&text=Potato','家庭常备食材，炖煮煎炒皆宜',1,'2022-06-10 09:22:54','2022-06-10 09:22:54',1,1);
INSERT INTO `prod` VALUES (49,'上海青',11,5.60,'https://dummyimage.com/600x600/f7f7f7/333333&text=Bok+Choy','叶片鲜嫩，适合快炒',1,'2022-06-10 09:30:17','2022-06-10 09:30:17',1,1);
INSERT INTO `prod` VALUES (50,'青椒',11,4.50,'https://dummyimage.com/600x600/f7f7f7/333333&text=Pepper','色泽新鲜，适合多种家常菜',1,'2022-06-10 09:34:28','2022-06-10 09:34:28',1,1);
INSERT INTO `prod` VALUES (51,'苹果',12,6.80,'https://dummyimage.com/600x600/f7f7f7/333333&text=Apple','脆甜多汁，家庭常购水果',1,'2022-06-10 09:40:51','2022-06-10 09:40:51',1,1);
INSERT INTO `prod` VALUES (52,'香蕉',12,5.50,'https://dummyimage.com/600x600/f7f7f7/333333&text=Banana','即食方便，适合早餐加餐',1,'2022-06-10 09:46:02','2022-06-10 09:46:02',1,1);
INSERT INTO `prod` VALUES (53,'橙子',12,7.20,'https://dummyimage.com/600x600/f7f7f7/333333&text=Orange','果香浓郁，酸甜平衡',1,'2022-06-10 09:48:37','2022-06-10 09:48:37',1,1);
INSERT INTO `prod` VALUES (54,'葡萄',12,9.90,'https://dummyimage.com/600x600/f7f7f7/333333&text=Grape','颗粒饱满，适合家庭分享',1,'2022-06-10 09:51:46','2022-06-10 09:51:46',1,1);
INSERT INTO `prod` VALUES (55,'鸡蛋',16,8.80,'https://dummyimage.com/600x600/f7f7f7/333333&text=Egg','每日鲜鸡蛋，早餐烹饪常备',1,'2022-06-10 09:53:37','2022-06-10 09:53:37',1,1);
INSERT INTO `prod` VALUES (56,'鸡胸肉',16,13.90,'https://dummyimage.com/600x600/f7f7f7/333333&text=Chicken','低脂高蛋白，健身家庭常购',1,'2022-06-10 09:55:44','2022-06-10 09:55:44',1,1);
INSERT INTO `prod` VALUES (57,'五花肉',16,16.80,'https://dummyimage.com/600x600/f7f7f7/333333&text=Pork','肥瘦均衡，适合炖煮煎炒',1,'2022-06-10 09:58:35','2022-06-10 09:58:35',1,1);
INSERT INTO `prod` VALUES (58,'鲜虾',16,22.80,'https://dummyimage.com/600x600/f7f7f7/333333&text=Shrimp','肉质紧实，适合清炒和白灼',1,'2022-06-10 10:12:28','2022-06-10 10:12:28',1,1);
INSERT INTO `prod` VALUES (59,'食用油',17,18.90,'https://dummyimage.com/600x600/f7f7f7/333333&text=Oil','家庭烹饪常用基础调味油',1,'2022-06-10 10:24:03','2022-06-10 10:24:03',1,1);
INSERT INTO `prod` VALUES (60,'生抽',17,6.50,'https://dummyimage.com/600x600/f7f7f7/333333&text=Soy+Sauce','家常提鲜调味，适合多种菜式',1,'2022-06-10 10:26:03','2022-06-10 10:26:03',1,1);
INSERT INTO `prod` VALUES (61,'纯牛奶',18,12.80,'https://dummyimage.com/600x600/f7f7f7/333333&text=Milk','早餐搭配优选，奶香浓郁',1,'2022-06-10 10:28:54','2022-06-10 10:28:54',1,1);
INSERT INTO `prod` VALUES (62,'酸奶',18,10.80,'https://dummyimage.com/600x600/f7f7f7/333333&text=Yogurt','低温冷藏，口感细腻',1,'2022-06-10 10:33:05','2022-06-10 10:33:05',1,1);
INSERT INTO `prod` VALUES (63,'吐司',18,9.90,'https://dummyimage.com/600x600/f7f7f7/333333&text=Toast','早餐速食常备，口感松软',1,'2022-06-10 10:35:40','2022-06-10 10:35:40',1,1);
INSERT INTO `prod` VALUES (64,'方便面',19,5.20,'https://dummyimage.com/600x600/f7f7f7/333333&text=Noodles','夜宵与加班补给常备速食',1,'2022-06-10 10:37:52','2022-06-10 10:37:52',1,1);
INSERT INTO `prod` VALUES (65,'薯片',19,7.80,'https://dummyimage.com/600x600/f7f7f7/333333&text=Chips','聚会休闲零食，香脆解馋',1,'2022-06-10 10:41:08','2022-06-10 10:41:08',1,1);
INSERT INTO `prod` VALUES (66,'矿泉水',20,2.50,'https://dummyimage.com/600x600/f7f7f7/333333&text=Water','日常补水必备，整箱更实惠',1,'2022-06-10 10:42:42','2022-06-10 10:42:42',1,1);
INSERT INTO `prod` VALUES (67,'橙汁',20,8.50,'https://dummyimage.com/600x600/f7f7f7/333333&text=Juice','果汁饮品，适合全家分享',1,'2022-06-10 10:43:56','2022-06-10 10:43:56',1,1);
INSERT INTO `prod` VALUES (68,'抽纸',21,11.90,'https://dummyimage.com/600x600/f7f7f7/333333&text=Tissue','家用抽纸，多场景便捷取用',1,'2022-06-10 10:54:25','2022-06-10 10:54:25',1,1);
INSERT INTO `prod` VALUES (69,'洗洁精',21,9.60,'https://dummyimage.com/600x600/f7f7f7/333333&text=Detergent','厨房清洁高频用品，去油更快',1,'2022-06-10 10:55:02','2022-06-10 10:55:02',1,1);
INSERT INTO `prod` VALUES (70,'垃圾袋',21,8.90,'https://dummyimage.com/600x600/f7f7f7/333333&text=Trash+Bag','家庭清洁常备用品，加厚不易破',1,'2022-06-10 10:56:02','2022-06-10 10:56:02',1,1);
INSERT INTO `prod` VALUES (71,'生菜',11,4.90,'https://dummyimage.com/600x600/f7f7f7/333333&text=Lettuce','新鲜脆嫩，适合火锅和沙拉',1,'2022-06-10 10:57:02','2022-06-10 10:57:02',1,1);
INSERT INTO `prod` VALUES (72,'金针菇',11,5.80,'https://dummyimage.com/600x600/f7f7f7/333333&text=Mushroom','火锅常备菌菇，口感爽滑',1,'2022-06-10 10:58:02','2022-06-10 10:58:02',1,1);
INSERT INTO `prod` VALUES (73,'豆腐',17,3.60,'https://dummyimage.com/600x600/f7f7f7/333333&text=Tofu','豆香浓郁，适合炖煮火锅',1,'2022-06-10 10:59:02','2022-06-10 10:59:02',1,1);
INSERT INTO `prod` VALUES (74,'肉卷',16,18.80,'https://dummyimage.com/600x600/f7f7f7/333333&text=Meat+Roll','火锅涮煮优选，肥瘦均衡',1,'2022-06-10 11:00:02','2022-06-10 11:00:02',1,1);

DROP TABLE IF EXISTS `prod_spec`;
CREATE TABLE `prod_spec` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `prod_id` bigint NOT NULL COMMENT '商品ID',
  `name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '规格或口味名称',
  `value` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '规格或口味可选值列表',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='商品规格表';

INSERT INTO `prod_spec` VALUES (40,61,'规格','["250ml*12盒","1L*6盒"]');
INSERT INTO `prod_spec` VALUES (41,62,'规格','["100g*8杯","180g*6杯"]');
INSERT INTO `prod_spec` VALUES (42,63,'规格','["450g","900g"]');
INSERT INTO `prod_spec` VALUES (45,64,'口味','["红烧牛肉","老坛酸菜","香辣牛肉"]');
INSERT INTO `prod_spec` VALUES (46,65,'规格','["70g","104g"]');
INSERT INTO `prod_spec` VALUES (47,66,'规格','["550ml","1.5L"]');
INSERT INTO `prod_spec` VALUES (48,67,'规格','["300ml","1L"]');
INSERT INTO `prod_spec` VALUES (49,68,'规格','["3层100抽*3包","4层80抽*6包"]');
INSERT INTO `prod_spec` VALUES (50,69,'规格','["500g","1kg"]');
INSERT INTO `prod_spec` VALUES (51,70,'规格','["45cm*50cm*60只","50cm*60cm*40只"]');
INSERT INTO `prod_spec` VALUES (52,71,'规格','["300g","500g"]');
INSERT INTO `prod_spec` VALUES (53,72,'规格','["200g","400g"]');
INSERT INTO `prod_spec` VALUES (54,73,'规格','["350g","500g"]');
INSERT INTO `prod_spec` VALUES (55,74,'规格','["250g","500g"]');

DROP TABLE IF EXISTS `staff`;
CREATE TABLE `staff` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '员工姓名',
  `username` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '登录账号',
  `password` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '登录密码',
  `phone` varchar(11) COLLATE utf8_bin NOT NULL COMMENT '手机号',
  `sex` varchar(2) COLLATE utf8_bin NOT NULL COMMENT '性别',
  `id_number` varchar(18) COLLATE utf8_bin NOT NULL COMMENT '身份证号',
  `status` int NOT NULL DEFAULT '1' COMMENT '账号状态 0禁用 1启用',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_uid` bigint DEFAULT NULL COMMENT '创建人',
  `update_uid` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='平台运营人员表';

INSERT INTO `staff` VALUES (1,'管理员','admin','123456','13812312312','1','110101199001010047',1,'2022-02-15 15:51:20','2022-02-17 09:16:20',10,1);

DROP TABLE IF EXISTS `ord_item`;
CREATE TABLE `ord_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '商品名称',
  `image` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '商品图片',
  `ord_id` bigint NOT NULL COMMENT '订单ID',
  `prod_id` bigint DEFAULT NULL COMMENT '商品ID',
  `pack_id` bigint DEFAULT NULL COMMENT '组合商品ID',
  `spec` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '规格或口味描述',
  `num` int NOT NULL DEFAULT '1' COMMENT '数量',
  `amount` decimal(10,2) NOT NULL COMMENT '明细金额',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='订单明细表';

-- 订单明细演示数据，覆盖单品单与组合单展示
INSERT INTO `ord_item` VALUES (1,'西红柿','https://dummyimage.com/600x600/f7f7f7/333333&text=Tomato',1,46,NULL,'',2,9.60);
INSERT INTO `ord_item` VALUES (2,'鸡蛋','https://dummyimage.com/600x600/f7f7f7/333333&text=Egg',1,55,NULL,'',1,8.80);
INSERT INTO `ord_item` VALUES (3,'家庭早餐组合','https://dummyimage.com/600x600/f7f7f7/333333&text=Breakfast+Combo',2,NULL,30,'',1,29.90);
INSERT INTO `ord_item` VALUES (4,'橙汁','https://dummyimage.com/600x600/f7f7f7/333333&text=Juice',3,67,NULL,'300ml',3,25.50);

DROP TABLE IF EXISTS `ord`;
CREATE TABLE `ord` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `no` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '订单号',
  `status` int NOT NULL DEFAULT '1' COMMENT '订单状态 1待付款 2待接单 3已接单 4配送中 5已完成 6已取消 7退款中',
  `uid` bigint NOT NULL COMMENT '下单用户ID',
  `addr_id` bigint NOT NULL COMMENT '收货地址ID',
  `ord_time` datetime NOT NULL COMMENT '下单时间',
  `checkout_time` datetime DEFAULT NULL COMMENT '支付完成时间',
  `pay_method` int NOT NULL DEFAULT '1' COMMENT '支付方式 1微信 2支付宝',
  `pay_status` tinyint NOT NULL DEFAULT '0' COMMENT '支付状态 0未支付 1已支付 2已退款',
  `amount` decimal(10,2) NOT NULL COMMENT '订单实付金额',
  `remark` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '用户订单备注',
  `phone` varchar(11) COLLATE utf8_bin DEFAULT NULL COMMENT '收货联系电话',
  `address` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '收货地址快照',
  `user_name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '下单用户名',
  `consignee` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '收货人',
  `cancel_reason` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '订单取消原因',
  `rejection_reason` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '门店拒单原因',
  `cancel_time` datetime DEFAULT NULL COMMENT '订单取消时间',
  `estimated_delivery_time` datetime DEFAULT NULL COMMENT '预计送达时间',
  `delivery_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '履约时效类型 1尽快配送 0预约时间',
  `delivery_time` datetime DEFAULT NULL COMMENT '送达时间',
  `pack_amount` int DEFAULT NULL COMMENT '打包与分拣服务费',
  `tableware_number` int DEFAULT NULL COMMENT '随单附加用品数量（兼容历史字段）',
  `tableware_status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '随单附加用品提供方式 1按系统默认 0按用户指定',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='零售订单表';

-- 订单演示数据，覆盖待接单、已完成、配送中等常见状态
INSERT INTO `ord` (
  `id`, `no`, `status`, `uid`, `addr_id`, `ord_time`, `checkout_time`,
  `pay_method`, `pay_status`, `amount`, `remark`, `phone`, `address`,
  `user_name`, `consignee`, `cancel_reason`, `rejection_reason`,
  `cancel_time`, `estimated_delivery_time`, `delivery_status`,
  `delivery_time`, `pack_amount`, `tableware_number`, `tableware_status`
) VALUES (
  1, 'CGR202606090001', 2, 1, 1, '2026-06-09 09:20:00', '2026-06-09 09:21:10',
  1, 1, 18.40, '早点送达', '13800000001', '北京市朝阳区望京街道SOHO T3 18层',
  '张三', '张三', NULL, NULL,
  NULL, '2026-06-09 10:10:00', 1,
  NULL, 2, 1, 1
);
INSERT INTO `ord` (
  `id`, `no`, `status`, `uid`, `addr_id`, `ord_time`, `checkout_time`,
  `pay_method`, `pay_status`, `amount`, `remark`, `phone`, `address`,
  `user_name`, `consignee`, `cancel_reason`, `rejection_reason`,
  `cancel_time`, `estimated_delivery_time`, `delivery_status`,
  `delivery_time`, `pack_amount`, `tableware_number`, `tableware_status`
) VALUES (
  2, 'CGR202606090002', 5, 2, 2, '2026-06-09 10:05:00', '2026-06-09 10:05:45',
  1, 1, 29.90, '放前台', '13800000002', '上海市浦东新区张江高科博云路2号',
  '李四', '李四', NULL, NULL,
  NULL, '2026-06-09 11:00:00', 1,
  '2026-06-09 10:46:00', 2, 1, 1
);
INSERT INTO `ord` (
  `id`, `no`, `status`, `uid`, `addr_id`, `ord_time`, `checkout_time`,
  `pay_method`, `pay_status`, `amount`, `remark`, `phone`, `address`,
  `user_name`, `consignee`, `cancel_reason`, `rejection_reason`,
  `cancel_time`, `estimated_delivery_time`, `delivery_status`,
  `delivery_time`, `pack_amount`, `tableware_number`, `tableware_status`
) VALUES (
  3, 'CGR202606090003', 4, 3, 3, '2026-06-09 11:30:00', '2026-06-09 11:31:02',
  1, 1, 25.50, '少冰常温都可', '13800000003', '广东省广州市天河区珠江新城花城大道88号',
  '王五', '王五', NULL, NULL,
  NULL, '2026-06-09 12:30:00', 1,
  NULL, 1, 1, 1
);

DROP TABLE IF EXISTS `pack`;
CREATE TABLE `pack` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cat_id` bigint NOT NULL COMMENT '组合商品分类ID',
  `name` varchar(32) COLLATE utf8_bin NOT NULL COMMENT '组合商品名称',
  `price` decimal(10,2) NOT NULL COMMENT '组合商品售价',
  `status` int DEFAULT '1' COMMENT '售卖状态 0停售 1上架',
  `descr` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '组合商品描述',
  `img` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '组合商品图片',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_uid` bigint DEFAULT NULL COMMENT '创建人',
  `update_uid` bigint DEFAULT NULL COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_pack_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='组合商品表';

INSERT INTO `pack` VALUES (30,13,'家庭早餐组合',29.90,1,'纯牛奶+吐司+鸡蛋，适合两人早餐补给','https://dummyimage.com/600x600/f7f7f7/333333&text=Breakfast+Combo','2022-06-10 11:00:00','2022-06-10 11:00:00',1,1);
INSERT INTO `pack` VALUES (31,13,'蔬菜优选组合',21.80,1,'西红柿+黄瓜+土豆+青椒，满足家庭日常烹饪','https://dummyimage.com/600x600/f7f7f7/333333&text=Veggie+Combo','2022-06-10 11:02:00','2022-06-10 11:02:00',1,1);
INSERT INTO `pack` VALUES (32,13,'一周水果组合',26.90,1,'苹果+香蕉+橙子+葡萄，适合家庭水果补给','https://dummyimage.com/600x600/f7f7f7/333333&text=Fruit+Combo','2022-06-10 11:04:00','2022-06-10 11:04:00',1,1);
INSERT INTO `pack` VALUES (33,15,'居家清洁组合',30.80,1,'抽纸+洗洁精+垃圾袋，满足家庭清洁和日用需求','https://dummyimage.com/600x600/f7f7f7/333333&text=Cleaning+Combo','2022-06-10 11:06:00','2022-06-10 11:06:00',1,1);
INSERT INTO `pack` VALUES (34,13,'火锅食材组合',29.90,1,'生菜+金针菇+豆腐+肉卷，满足家庭火锅场景','https://dummyimage.com/600x600/f7f7f7/333333&text=Hotpot+Combo','2022-06-10 11:08:00','2022-06-10 11:08:00',1,1);

DROP TABLE IF EXISTS `pack_item`;
CREATE TABLE `pack_item` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pack_id` bigint DEFAULT NULL COMMENT '组合商品ID',
  `prod_id` bigint DEFAULT NULL COMMENT '商品ID',
  `name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '商品名称（冗余快照）',
  `price` decimal(10,2) DEFAULT NULL COMMENT '商品单价（冗余快照）',
  `copies` int DEFAULT NULL COMMENT '商品数量',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='组合商品明细表';

INSERT INTO `pack_item` VALUES (40,30,61,'纯牛奶',12.80,1);
INSERT INTO `pack_item` VALUES (41,30,63,'吐司',9.90,1);
INSERT INTO `pack_item` VALUES (42,30,55,'鸡蛋',8.80,1);
INSERT INTO `pack_item` VALUES (43,31,46,'西红柿',4.80,1);
INSERT INTO `pack_item` VALUES (44,31,47,'黄瓜',3.90,1);
INSERT INTO `pack_item` VALUES (45,31,48,'土豆',4.20,1);
INSERT INTO `pack_item` VALUES (46,31,50,'青椒',4.50,1);
INSERT INTO `pack_item` VALUES (47,32,51,'苹果',6.80,1);
INSERT INTO `pack_item` VALUES (48,32,52,'香蕉',5.50,1);
INSERT INTO `pack_item` VALUES (49,32,53,'橙子',7.20,1);
INSERT INTO `pack_item` VALUES (50,32,54,'葡萄',9.90,1);
INSERT INTO `pack_item` VALUES (51,33,68,'抽纸',11.90,1);
INSERT INTO `pack_item` VALUES (52,33,69,'洗洁精',9.60,1);
INSERT INTO `pack_item` VALUES (53,33,70,'垃圾袋',8.90,1);
INSERT INTO `pack_item` VALUES (54,34,71,'生菜',4.90,1);
INSERT INTO `pack_item` VALUES (55,34,72,'金针菇',5.80,1);
INSERT INTO `pack_item` VALUES (56,34,73,'豆腐',3.60,1);
INSERT INTO `pack_item` VALUES (57,34,74,'肉卷',18.80,1);

DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '商品名称',
  `image` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '商品图片',
  `uid` bigint NOT NULL COMMENT '用户ID',
  `prod_id` bigint DEFAULT NULL COMMENT '商品ID',
  `pack_id` bigint DEFAULT NULL COMMENT '组合商品ID',
  `spec` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '规格或口味描述',
  `num` int NOT NULL DEFAULT '1' COMMENT '数量',
  `amount` decimal(10,2) NOT NULL COMMENT '加入时单价金额',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='购物车表';

-- 购物车演示数据，便于小程序购物车与结算页直接展示
INSERT INTO `cart` VALUES (1,'黄瓜','https://dummyimage.com/600x600/f7f7f7/333333&text=Cucumber',1,47,NULL,'',2,3.90,'2026-06-09 08:50:00');
INSERT INTO `cart` VALUES (2,'纯牛奶','https://dummyimage.com/600x600/f7f7f7/333333&text=Milk',1,61,NULL,'1L*6盒',1,12.80,'2026-06-09 08:55:00');
INSERT INTO `cart` VALUES (3,'居家清洁组合','https://dummyimage.com/600x600/f7f7f7/333333&text=Cleaning+Combo',2,NULL,33,'',1,28.50,'2026-06-09 09:10:00');

DROP TABLE IF EXISTS `cust`;
CREATE TABLE `cust` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `openid` varchar(45) COLLATE utf8_bin DEFAULT NULL COMMENT '微信用户唯一标识',
  `name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '用户昵称或姓名',
  `phone` varchar(11) COLLATE utf8_bin DEFAULT NULL COMMENT '手机号',
  `sex` varchar(2) COLLATE utf8_bin DEFAULT NULL COMMENT '性别',
  `id_number` varchar(18) COLLATE utf8_bin DEFAULT NULL COMMENT '身份证号',
  `avatar` varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT '头像',
  `create_time` datetime DEFAULT NULL COMMENT '注册时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 COLLATE=utf8_bin COMMENT='平台用户表';

-- 平台用户演示数据，便于用户列表、订单归属与AI客服链路展示
INSERT INTO `cust` VALUES (1,'oGZUI0_demo_user_01','张三','13800000001','1','110101199001010011','https://dummyimage.com/200x200/5b8ff9/ffffff&text=ZS','2026-06-01 10:00:00');
INSERT INTO `cust` VALUES (2,'oGZUI0_demo_user_02','李四','13800000002','1','310101199202020022','https://dummyimage.com/200x200/61d9a5/ffffff&text=LS','2026-06-02 11:20:00');
INSERT INTO `cust` VALUES (3,'oGZUI0_demo_user_03','王五','13800000003','0','440101199303030033','https://dummyimage.com/200x200/f6bd16/ffffff&text=WW','2026-06-03 14:30:00');

DROP TABLE IF EXISTS `ai_conversation`;
CREATE TABLE `ai_conversation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `conversation_type` varchar(32) NOT NULL DEFAULT 'customer_service' COMMENT '会话类型',
  `title` varchar(128) DEFAULT NULL COMMENT '会话标题',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1正常 0删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_conversation_type` (`conversation_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI客服会话表';

DROP TABLE IF EXISTS `ai_message`;
CREATE TABLE `ai_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `conversation_id` bigint NOT NULL COMMENT '会话ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `role` varchar(32) NOT NULL COMMENT '消息角色：user assistant system tool',
  `content` text NOT NULL COMMENT '消息内容',
  `content_type` varchar(32) NOT NULL DEFAULT 'text' COMMENT '内容类型：text markdown json',
  `tool_name` varchar(128) DEFAULT NULL COMMENT '工具名称',
  `tool_result` text DEFAULT NULL COMMENT '工具返回结果',
  `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '风险审计状态：0待处理 1已确认 2已忽略',
  `audit_remark` varchar(500) DEFAULT NULL COMMENT '风险审计备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI客服消息表';

DROP TABLE IF EXISTS `ai_knowledge_document`;
CREATE TABLE `ai_knowledge_document` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(128) NOT NULL COMMENT '文档标题',
  `doc_type` varchar(64) NOT NULL COMMENT '文档类型',
  `source_type` varchar(64) DEFAULT 'manual' COMMENT '来源类型',
  `source_id` bigint DEFAULT NULL COMMENT '来源业务ID',
  `content` mediumtext NOT NULL COMMENT '文档内容',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1启用 0停用',
  `version` int NOT NULL DEFAULT '1' COMMENT '版本号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_doc_type` (`doc_type`),
  KEY `idx_source` (`source_type`,`source_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI知识文档表';

DROP TABLE IF EXISTS `ai_knowledge_chunk`;
CREATE TABLE `ai_knowledge_chunk` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `document_id` bigint NOT NULL COMMENT '知识文档ID',
  `chunk_index` int NOT NULL COMMENT '切片序号',
  `chunk_text` text NOT NULL COMMENT '切片文本',
  `vector_doc_id` varchar(128) DEFAULT NULL COMMENT '向量库文档ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_document_id` (`document_id`),
  KEY `idx_vector_doc_id` (`vector_doc_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI知识切片表';

DROP TABLE IF EXISTS `ai_tool_call_log`;
CREATE TABLE `ai_tool_call_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `conversation_id` bigint DEFAULT NULL COMMENT '会话ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `tool_name` varchar(128) NOT NULL COMMENT '工具名称',
  `request_json` text DEFAULT NULL COMMENT '请求参数',
  `response_json` text DEFAULT NULL COMMENT '响应结果',
  `success_flag` tinyint NOT NULL DEFAULT '1' COMMENT '是否成功：1成功 0失败',
  `error_message` varchar(512) DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_tool_name` (`tool_name`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI工具调用日志表';

DROP TABLE IF EXISTS `ai_after_sale_request`;
CREATE TABLE `ai_after_sale_request` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `conversation_id` bigint DEFAULT NULL COMMENT '会话ID',
  `request_type` varchar(64) NOT NULL COMMENT '申请类型',
  `reason` varchar(255) NOT NULL COMMENT '售后原因',
  `description` varchar(500) DEFAULT NULL COMMENT '补充说明',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态：0待处理 1已受理 2已完成 3已拒绝',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI售后申请表';

INSERT INTO `ai_knowledge_document` (`title`, `doc_type`, `source_type`, `content`)
SELECT '配送时效说明', 'delivery', 'manual', '平台正常配送时效以门店营业状态、订单量和配送距离为准。用户可在订单详情页查看实时订单状态。'
WHERE NOT EXISTS (SELECT 1 FROM `ai_knowledge_document` WHERE `title` = '配送时效说明');

INSERT INTO `ai_knowledge_document` (`title`, `doc_type`, `source_type`, `content`)
SELECT '售后退款说明', 'after_sale', 'manual', '用户如遇商品缺失、破损、错发等问题，可在订单详情页提交售后申请。退款结果以平台审核为准。'
WHERE NOT EXISTS (SELECT 1 FROM `ai_knowledge_document` WHERE `title` = '售后退款说明');

INSERT INTO `ai_knowledge_document` (`title`, `doc_type`, `source_type`, `content`)
SELECT '优惠券使用说明', 'activity', 'manual', '优惠券是否可用取决于优惠券有效期、适用商品、订单金额和使用门槛。具体结果以结算页展示为准。'
WHERE NOT EXISTS (SELECT 1 FROM `ai_knowledge_document` WHERE `title` = '优惠券使用说明');
