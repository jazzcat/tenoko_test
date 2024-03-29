package com.woowacourse.ternoko.common.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;

    @Override
    public void afterCompletion(@NotNull final HttpServletRequest request, final HttpServletResponse response,
                                @NotNull final Object handler, final Exception ex) throws IOException {
        if (isSuccess(response.getStatus())) {
            ContentCachingRequestWrapper cachingRequest;
            try {
                cachingRequest = (ContentCachingRequestWrapper) request;
            } catch (ClassCastException e) {
                log.info("로깅 필터가 정상적으로 동작하지 않았습니다.");
                return;
            }
            log.info(LogForm.SUCCESS_LOGGING_FORM, request.getMethod(), request.getRequestURI(),
                    StringUtils.hasText(request.getHeader("Authorization")),
                    objectMapper.readTree(cachingRequest.getContentAsByteArray()));
        }
    }

    private boolean isSuccess(int responseStatus) {
        return !HttpStatus.valueOf(responseStatus).is4xxClientError() && !HttpStatus.valueOf(responseStatus)
                .is5xxServerError();
    }
}
