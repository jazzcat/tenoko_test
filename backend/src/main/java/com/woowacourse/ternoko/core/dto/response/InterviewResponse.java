package com.woowacourse.ternoko.core.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.woowacourse.ternoko.core.domain.interview.Interview;
import com.woowacourse.ternoko.core.domain.interview.InterviewStatusType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResponse {

    private Long id;
    private String coachNickname;
    private String coachImageUrl;
    private String crewNickname;
    private String crewImageUrl;
    private InterviewStatusType status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime interviewStartTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime interviewEndTime;

    private List<FormItemResponse> interviewQuestions;

    public static InterviewResponse from(final Interview interview) {
        final List<FormItemResponse> formItemResponses = interview.getFormItems().stream()
                .map(FormItemResponse::from)
                .collect(Collectors.toList());

        return InterviewResponse.builder()
                .id(interview.getId())
                .coachNickname(interview.getCoach().getNickname())
                .coachImageUrl(interview.getCoach().getImageUrl())
                .crewNickname(interview.getCrew().getNickname())
                .crewImageUrl(interview.getCrew().getImageUrl())
                .interviewStartTime(interview.getInterviewStartTime())
                .interviewEndTime(interview.getInterviewEndTime())
                .interviewQuestions(formItemResponses)
                .status(interview.getInterviewStatusType())
                .build();
    }
}
