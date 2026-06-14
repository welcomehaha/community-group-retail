package com.community.ai.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AiMessageMapperXmlTest {

    @Test
    void shouldParseAiMessageMapperXmlFromClasspath() throws Exception {
        ClassPathResource resource = new ClassPathResource("mapper/AiMessageMapper.xml");

        assertTrue(resource.exists(), "AiMessageMapper.xml 必须存在于 classpath");

        try (InputStream inputStream = resource.getInputStream()) {
            // 直接解析构建产物中的 Mapper XML，兜住非法字符导致的启动失败
            assertNotNull(DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream));
        }
    }

    @Test
    void shouldKeepAuditStatusFilterAndApprovalGuardsInMapperXml() throws Exception {
        ClassPathResource resource = new ClassPathResource("mapper/AiMessageMapper.xml");
        String xmlContent;
        try (InputStream inputStream = resource.getInputStream()) {
            xmlContent = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }

        // 分页、汇总、概览、趋势四组查询都必须带 auditStatus 过滤，避免统计口径再次漂移
        assertEquals(4, countMatches(xmlContent, "<if test=\"auditStatus != null\">"));
        // 审批更新必须限制仅待审批记录可写，避免并发覆盖
        assertTrue(xmlContent.contains("ifnull(approval_status, 0) = 0"));
        // 风险更新只允许 assistant 且命中高风险规则的消息进入状态机
        assertTrue(xmlContent.contains("and role = 'assistant'"));
        assertTrue(xmlContent.contains("or tool_name = 'action'"));
    }

    /**
     * 统计子串出现次数，避免引入额外依赖
     */
    private int countMatches(String text, String fragment) {
        int count = 0;
        int fromIndex = 0;
        while (true) {
            int matchIndex = text.indexOf(fragment, fromIndex);
            if (matchIndex < 0) {
                return count;
            }
            count++;
            fromIndex = matchIndex + fragment.length();
        }
    }
}
