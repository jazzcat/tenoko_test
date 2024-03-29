package com.woowacourse.ternoko.auth.config;

import com.woowacourse.ternoko.auth.application.JwtProvider;
import com.woowacourse.ternoko.auth.presentation.AuthInterceptor;
import com.woowacourse.ternoko.auth.presentation.AuthenticationPrincipalArgumentResolver;
import com.woowacourse.ternoko.auth.presentation.MemberTypeCache;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AuthenticationPrincipalConfig implements WebMvcConfigurer {

    private final JwtProvider jwtTokenProvider;
    private final MemberTypeCache memberTypeCache;
    private final AuthInterceptor authInterceptor;

    public AuthenticationPrincipalConfig(final JwtProvider jwtProvider,
                                         final MemberTypeCache memberTypeCache,
                                         final AuthInterceptor authInterceptor) {
        this.jwtTokenProvider = jwtProvider;
        this.memberTypeCache = memberTypeCache;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/interviews/**")
                .addPathPatterns("/api/calendar/times/**")
                .addPathPatterns("/api/schedules/**")
                .addPathPatterns("/api/coaches/**")
                .addPathPatterns("/api/crews/**");
    }

    @Override
    public void addArgumentResolvers(final List argumentResolvers) {
        argumentResolvers.add(createAuthenticationPrincipalArgumentResolver());
    }

    @Bean
    public AuthenticationPrincipalArgumentResolver createAuthenticationPrincipalArgumentResolver() {
        return new AuthenticationPrincipalArgumentResolver(jwtTokenProvider, memberTypeCache);
    }
}
