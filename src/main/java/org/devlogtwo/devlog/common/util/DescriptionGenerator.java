package org.devlogtwo.devlog.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.devlogtwo.devlog.common.type.ActivityType;
import org.devlogtwo.devlog.domain.actlog.entity.ActivityLog;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DescriptionGenerator {

    private final ObjectMapper objectMapper;

    public String createDescription(ActivityLog activityLog) {
        ActivityType type = activityLog.getType();
        String resultJson = activityLog.getResult();

        // comment 삭제와 task 삭제의 result가 달라서 별도로 먼저 처리
        if (type == ActivityType.TASK_DELETED || type == ActivityType.COMMENT_DELETED) {
            return getDefaultDescription(activityLog);
        }

        try {
            if (resultJson == null || resultJson.equals("N/A")) {
                return getDefaultDescription(activityLog);
            }

            JsonNode resultNode = objectMapper.readTree(resultJson);

            return switch (type) {
                case TASK_CREATED -> {
                    String title = resultNode.path("title").asText("알 수 없는 작업");
                    yield String.format("새로운 작업 '%s'을 생성했습니다.", title);
                }
                case TASK_UPDATED -> "작업 정보를 수정했습니다.";
                case TASK_STATUS_CHANGED -> {
                    String before = resultNode.path("before").asText("이전 상태");
                    String after = resultNode.path("after").path("status").asText("현재 상태");
                    yield String.format("작업 상태를 '%s'에서 '%s'로 변경했습니다.", before, after);
                }
                case COMMENT_CREATED -> "작업에 댓글을 작성했습니다.";
                case COMMENT_UPDATED -> "댓글을 수정했습니다.";
                default -> getDefaultDescription(activityLog);
            };
        } catch (Exception e) {
            log.warn("[DescriptionGenerator] 로그 설명 생성 중 오류 발생: type={}, result={}, error={}",
                    type, resultJson, e.getMessage());
            return type.getDescription();
        }
    }

    private String getDefaultDescription(ActivityLog activityLog) {
        return switch (activityLog.getType()) {
            case TASK_DELETED -> "작업을 삭제했습니다.";
            case COMMENT_DELETED -> "댓글을 삭제했습니다.";
            default -> activityLog.getType().getDescription();
        };
    }
}