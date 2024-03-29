package com.woowacourse.ternoko.auth.application;

import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;

public class AuthorizationExtractor {

    public static final String AUTHORIZATION = "Authorization";
    public static String BEARER_TYPE = "Bearer ";
    public static final String ACCESS_TOKEN_TYPE = AuthorizationExtractor.class.getSimpleName() + ".ACCESS_TOKEN_TYPE";

    public static String extract(final HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(AUTHORIZATION);
        String authHeaderValue = null;
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            authHeaderValue = getAuthHeaderValue(request, value);
        }
        return authHeaderValue;
    }

    public static String extract(String request) {

        if ((request.toLowerCase().startsWith(BEARER_TYPE.toLowerCase()))) {
            String authHeaderValue = request.substring(BEARER_TYPE.length()).trim();
            int commaIndex = authHeaderValue.indexOf(',');
            if (commaIndex > 0) {
                authHeaderValue = authHeaderValue.substring(0, commaIndex);
            }
            return authHeaderValue;
        }

        return null;
    }

    private static String getAuthHeaderValue(final HttpServletRequest request, final String value) {
        if ((value.toLowerCase().startsWith(BEARER_TYPE.toLowerCase()))) {
            request.setAttribute(ACCESS_TOKEN_TYPE, value.substring(0, BEARER_TYPE.length()).trim());
            return generateAuthHeaderValue(value);
        }
        return null;
    }

    private static String generateAuthHeaderValue(final String value) {
        String authHeaderValue = value.substring(BEARER_TYPE.length()).trim();
        int commaIndex = authHeaderValue.indexOf(',');
        if (commaIndex > 0) {
            authHeaderValue = authHeaderValue.substring(0, commaIndex);
        }
        return authHeaderValue;
    }
}
