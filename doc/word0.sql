SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for word
-- ----------------------------
DROP TABLE IF EXISTS `word`;
CREATE TABLE `word`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '主键',
  `word` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单词',
  `sound` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '音标',
  `translation` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '翻译',
  `schedule` int(1) NOT NULL DEFAULT 0 COMMENT '进度: 0(未背诵), 1(已背诵1遍), 2(已背诵2遍), 3(已背诵3遍, 今日无需再背诵), 4(不认识: 只要一次背诵时忘记, 则变为不认识状态)',
  `start_date` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '计划开始时间, 初始为录入单词的第二天, 后续按照艾宾浩斯记忆曲线背诵单词: 0, 1, 2, 4, 8, 16, 32, 108. 若某个单词没有在规定日期24点前背完, 则改为规定日期的第二天, 即单词进入记忆曲线的开始, 算作没背单词的惩罚',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system' COMMENT '创建人ID，默认为系统创建',
  `update_by` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'system' COMMENT '更新人ID，默认为系统更新',
  `create_date` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_date` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `column1` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备用字段1',
  `column2` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备用字段2',
  `column3` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备用字段3',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '单词表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of word
-- ----------------------------
INSERT INTO `word` VALUES ('1', 'excellent', '\'ekslәnt', '杰出的, 出色的', 0, '2019-07-10 10:55:00', '', 'system', 'system', '2019-07-05 00:22:01', '2019-07-09 23:55:00', NULL, NULL, NULL, '0');
INSERT INTO `word` VALUES ('2', 'accompany', 'ә\'kʌmpәni', '陪伴, 伴随', 0, '2019-07-10 10:55:00', '', 'system', 'system', '2019-07-05 00:22:13', '2019-07-09 23:55:00', NULL, NULL, NULL, '0');
INSERT INTO `word` VALUES ('3', 'compulsory', 'kәm\'pʌlsәri', '义务的, 必须做的', 0, '2019-07-10 10:55:00', '', 'system', 'system', '2019-07-05 00:22:13', '2019-07-09 23:55:00', NULL, NULL, NULL, '0');
INSERT INTO `word` VALUES ('4', 'embrace', 'im\'breis', '拥抱', 0, '2019-07-10 10:55:00', '', 'system', 'system', '2019-07-05 00:22:13', '2019-07-09 23:55:00', NULL, NULL, NULL, '0');
INSERT INTO `word` VALUES ('5', 'extravagant', 'ik\'strævgәnt', '奢侈的, 浪费的', 0, '2019-07-10 10:55:00', '', 'system', 'system', '2019-07-05 00:22:13', '2019-07-09 23:55:00', NULL, NULL, NULL, '0');
INSERT INTO `word` VALUES ('6', 'flourish', '\'flʌriʃ', '繁荣, 茂盛', 0, '2019-07-10 10:55:00', '', 'system', 'system', '2019-07-05 00:22:13', '2019-07-09 23:55:00', NULL, NULL, NULL, '0');
INSERT INTO `word` VALUES ('7', 'homogeneous', ',hәumә\'dʒi:niәs', '同种的, 同质的', 0, '2019-07-10 10:55:00', '', 'system', 'system', '2019-07-05 00:22:13', '2019-07-09 23:55:00', NULL, NULL, NULL, '0');
INSERT INTO `word` VALUES ('8', 'illuminate', 'i\'lju:mineit', '照明, 阐明', 0, '2019-07-10 10:55:00', '', 'system', 'system', '2019-07-05 00:22:13', '2019-07-09 23:55:00', NULL, NULL, NULL, '0');
INSERT INTO `word` VALUES ('9', 'glimpse', 'glimps', '闪烁不定', 0, '2019-07-10 10:55:00', '', 'system', 'system', '2019-07-05 00:22:13', '2019-07-09 23:55:00', NULL, NULL, NULL, '0');

SET FOREIGN_KEY_CHECKS = 1;
