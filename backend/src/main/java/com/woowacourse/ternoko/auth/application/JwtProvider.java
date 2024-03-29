package com.woowacourse.ternoko.auth.application;

import static com.woowacourse.ternoko.common.exception.ExceptionType.UNAUTHORIZED_MEMBER;

import com.woowacourse.ternoko.auth.exception.TokenNotValidException;
import com.woowacourse.ternoko.common.exception.ExceptionType;
import com.woowacourse.ternoko.common.exception.TokenInvalidException;
import com.woowacourse.ternoko.core.domain.member.Member;
import com.woowacourse.ternoko.core.domain.member.MemberType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    public JwtProvider(@Value("${security.jwt.token.secret-key}") final String secretKey,
                       @Value("${security.jwt.token.expire-length}") final long validityInMilliseconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.validityInMilliseconds = validityInMilliseconds;
    }

    public String createToken(Member member) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder().setSubject(member.getId().toString()).setIssuedAt(now).setExpiration(validity)
                .claim("memberType", member.getMemberType()).signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public MemberType getMemberType(final String token) {
        return MemberType.valueOf(
                Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("memberType")
                        .toString());
    }

    public void validateToken(final String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new TokenInvalidException(ExceptionType.EXPIRED_TOKEN);
        } catch (Exception e) {
            throw new TokenInvalidException(ExceptionType.INVALID_TOKEN);
        }
    }

    public String extractSubject(String token) {
        try {
            return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            throw new TokenNotValidException(UNAUTHORIZED_MEMBER);
        }
    }
}
