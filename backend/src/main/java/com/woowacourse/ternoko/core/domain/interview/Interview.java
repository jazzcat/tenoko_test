package com.woowacourse.ternoko.core.domain.interview;

import static com.woowacourse.ternoko.common.exception.ExceptionType.CANNOT_EDIT_INTERVIEW;
import static com.woowacourse.ternoko.common.exception.ExceptionType.CANNOT_UPDATE_CREW;
import static com.woowacourse.ternoko.common.exception.ExceptionType.INVALID_INTERVIEW_DATE;
import static com.woowacourse.ternoko.common.exception.ExceptionType.INVALID_INTERVIEW_MEMBER_ID;
import static com.woowacourse.ternoko.core.domain.availabledatetime.AvailableDateTimeStatus.DELETED;
import static com.woowacourse.ternoko.core.domain.availabledatetime.AvailableDateTimeStatus.OPEN;
import static com.woowacourse.ternoko.core.domain.interview.InterviewStatusType.CANCELED;
import static com.woowacourse.ternoko.core.domain.interview.InterviewStatusType.EDITABLE;
import static com.woowacourse.ternoko.core.domain.member.MemberType.COACH;
import static com.woowacourse.ternoko.core.domain.member.MemberType.CREW;

import com.woowacourse.ternoko.common.exception.InterviewInvalidException;
import com.woowacourse.ternoko.core.domain.availabledatetime.AvailableDateTime;
import com.woowacourse.ternoko.core.domain.availabledatetime.AvailableDateTimeStatus;
import com.woowacourse.ternoko.core.domain.interview.formitem.FormItem;
import com.woowacourse.ternoko.core.domain.interview.formitem.FormItems;
import com.woowacourse.ternoko.core.domain.member.MemberType;
import com.woowacourse.ternoko.core.domain.member.coach.Coach;
import com.woowacourse.ternoko.core.domain.member.crew.Crew;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Interview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JoinColumn(name = "interview_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "available_date_time_id", unique = true)
    private AvailableDateTime availableDateTime;

    @Column(nullable = false)
    private LocalDateTime interviewStartTime;

    @Column(nullable = false)
    private LocalDateTime interviewEndTime;

    @OneToOne
    @JoinColumn(name = "coach_id")
    private Coach coach;

    @OneToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @Embedded
    private FormItems formItems;

    @Enumerated(EnumType.STRING)
    private InterviewStatusType interviewStatusType;

    public Interview(final Long id,
                     final AvailableDateTime availableDateTime,
                     final LocalDateTime interviewStartTime,
                     final LocalDateTime interviewEndTime,
                     final Coach coach,
                     final Crew crew,
                     final List<FormItem> formItems,
                     final InterviewStatusType interviewStatusType) {
        this.id = id;
        this.availableDateTime = availableDateTime;
        this.interviewStartTime = interviewStartTime;
        this.interviewEndTime = interviewEndTime;
        this.coach = coach;
        this.crew = crew;
        this.formItems = new FormItems(formItems, this);
        this.interviewStatusType = interviewStatusType;
    }

    public Interview(final AvailableDateTime availableDateTime,
                     final LocalDateTime interviewStartTime,
                     final LocalDateTime interviewEndTime,
                     final Coach coach,
                     final Crew crew,
                     final List<FormItem> formItems) {
        this(null, availableDateTime, interviewStartTime, interviewEndTime, coach, crew, formItems, EDITABLE);
    }

    public static Interview create(final AvailableDateTime availableDateTime,
                                   final Coach coach,
                                   final Crew crew,
                                   final List<FormItem> formItems) {
        validateNextDay(availableDateTime);

        return new Interview(
                availableDateTime,
                availableDateTime.getLocalDateTime(),
                availableDateTime.getLocalDateTime().plusMinutes(30),
                coach,
                crew,
                formItems);
    }

    private static void validateNextDay(final AvailableDateTime availableDateTime) {
        if (availableDateTime.isPast() || availableDateTime.isToday()) {
            throw new InterviewInvalidException(INVALID_INTERVIEW_DATE);
        }
    }

    public void update(final Interview interview) {
        validateModifiableInterviewStatus();
        validateUpdateCrew(interview);
        openOriginTime(interview);
        this.availableDateTime = interview.getAvailableDateTime();
        this.interviewStartTime = interview.getInterviewStartTime();
        this.interviewEndTime = interview.getInterviewEndTime();
        this.coach = interview.getCoach();
        this.interviewStatusType = interview.getInterviewStatusType();
        formItems.update(interview.getFormItems());
    }

    private void openOriginTime(final Interview interview) {
        final AvailableDateTime availableDateTime = interview.getAvailableDateTime();
        if (this.availableDateTime.isSame(availableDateTime.getId())) {
            return;
        }
        if (this.interviewStatusType.equals(CANCELED)) {
            return;
        }
        this.availableDateTime.changeStatus(OPEN);
    }

    private void validateModifiableInterviewStatus() {
        final List<InterviewStatusType> invalidUpdateStatus = findInvalidUpdateStatus();
        for (InterviewStatusType invalidStatus : invalidUpdateStatus) {
            validateInterviewStatus(invalidStatus);
        }
    }

    private void validateUpdateCrew(final Interview interview) {
        if (!isCreatedBy(interview.getCrew().getId())) {
            throw new InterviewInvalidException(CANNOT_UPDATE_CREW);
        }
    }

    private List<InterviewStatusType> findInvalidUpdateStatus() {
        final List<InterviewStatusType> status = Arrays.stream(InterviewStatusType.values())
                .collect(Collectors.toList());
        status.remove(EDITABLE);
        status.remove(CANCELED);
        return status;
    }

    private void validateInterviewStatus(final InterviewStatusType invalidStatus) {
        if (this.interviewStatusType == invalidStatus) {
            throw new InterviewInvalidException(CANNOT_EDIT_INTERVIEW);
        }
    }

    public void cancel() {
        this.availableDateTime.changeStatus(DELETED);
        this.interviewStatusType = CANCELED;
    }

    public void complete(final MemberType memberType) {
        if (this.interviewStatusType == InterviewStatusType.FIXED && memberType == COACH) {
            this.interviewStatusType = InterviewStatusType.COACH_COMPLETED;
            return;
        }
        if (this.interviewStatusType == InterviewStatusType.FIXED && memberType == CREW) {
            this.interviewStatusType = InterviewStatusType.CREW_COMPLETED;
            return;
        }
        this.interviewStatusType = InterviewStatusType.COMPLETED;
    }

    public void updateStatus(final InterviewStatusType interviewStatusType) {
        this.interviewStatusType = interviewStatusType;
    }

    public MemberType findMemberType(final Long memberId) {
        if (coach.isSameId(memberId)) {
            return coach.getMemberType();
        }

        if (crew.isSameId(memberId)) {
            return crew.getMemberType();
        }

        throw new InterviewInvalidException(INVALID_INTERVIEW_MEMBER_ID);
    }

    public boolean canCreateCommentBy(final MemberType memberType) {
        return interviewStatusType.canCreateCommentStatusBy(memberType);
    }

    public boolean canFindCommentBy() {
        return interviewStatusType.canFindCommentBy();
    }

    public boolean isSame(final Long interviewId) {
        return id.equals(interviewId);
    }

    public boolean isCreatedBy(final Long crewId) {
        return crew.isSameId(crewId);
    }

    public List<FormItem> getFormItems() {
        return formItems.getFormItems();
    }

    public Interview copyOf() {
        return new Interview(this.id,
                this.availableDateTime,
                this.interviewStartTime,
                this.interviewEndTime,
                this.coach,
                this.crew,
                this.formItems,
                this.interviewStatusType);
    }

    public void changeAvailableTimeStatus(final AvailableDateTimeStatus status) {
        availableDateTime.changeStatus(status);
    }

    public boolean isCanceled() {
        return interviewStatusType.equals(CANCELED);
    }

    public boolean containsMember(final long memberId) {
        return coach.isSameId(memberId) || crew.isSameId(memberId);
    }
}

