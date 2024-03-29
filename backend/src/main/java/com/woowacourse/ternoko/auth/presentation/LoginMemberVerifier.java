package com.woowacourse.ternoko.auth.presentation;

import static com.woowacourse.ternoko.common.exception.ExceptionType.COACH_NOT_ALLOWED;
import static com.woowacourse.ternoko.common.exception.ExceptionType.CREW_NOT_ALLOWED;

import com.woowacourse.ternoko.auth.exception.CoachNotAllowedException;
import com.woowacourse.ternoko.auth.exception.CrewNotAllowedException;
import com.woowacourse.ternoko.core.domain.member.MemberType;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoginMemberVerifier {

    private final MemberTypeCache memberTypeCache;

    @Before("@annotation(com.woowacourse.ternoko.auth.presentation.annotation.CrewOnly)")
    public void checkMemberTyeCrew() {
        final MemberType memberType = memberTypeCache.getMemberType();
        if (!memberType.equals(MemberType.CREW)) {
            throw new CrewNotAllowedException(CREW_NOT_ALLOWED);
        }
    }

    @Before("@annotation(com.woowacourse.ternoko.auth.presentation.annotation.CoachOnly)")
    public void checkMemberTyeCoach() {
        final MemberType memberType = memberTypeCache.getMemberType();
        if (!memberType.equals(MemberType.COACH)) {
            throw new CoachNotAllowedException(COACH_NOT_ALLOWED);
        }
    }
}
