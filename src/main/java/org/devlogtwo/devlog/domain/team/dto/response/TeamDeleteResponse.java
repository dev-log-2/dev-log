package org.devlogtwo.devlog.domain.team.dto.response;

public record TeamDeleteResponse(
        String message
) {
    public static TeamDeleteResponse of() {
        return new TeamDeleteResponse(
                "팀이 성공적으로 삭제되었습니다."
        );
    }
}
