package com.finance.backend.security;

import com.finance.backend.exception.ForbiddenException;
import com.finance.backend.exception.UnauthorizedException;
import com.finance.backend.model.AppUser;
import com.finance.backend.model.UserStatus;
import com.finance.backend.repository.AppUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Optional;

@Component
public class RbacInterceptor implements HandlerInterceptor {

    private final AppUserRepository appUserRepository;
    private final String userHeaderName;

    public RbacInterceptor(AppUserRepository appUserRepository,
                           @Value("${app.auth.user-header:X-User-Id}") String userHeaderName) {
        this.appUserRepository = appUserRepository;
        this.userHeaderName = userHeaderName;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod method)) {
            return true;
        }

        RequireRoles annotation = findAnnotation(method);
        if (annotation == null) {
            return true;
        }

        String rawUserId = request.getHeader(userHeaderName);
        if (rawUserId == null || rawUserId.isBlank()) {
            throw new UnauthorizedException("Missing authentication header: " + userHeaderName);
        }

        Long userId;
        try {
            userId = Long.parseLong(rawUserId.trim());
        } catch (NumberFormatException ex) {
            throw new UnauthorizedException("Invalid user id in authentication header");
        }

        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found for provided authentication header"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new ForbiddenException("User is inactive and cannot access this resource");
        }

        boolean allowed = Arrays.stream(annotation.value()).anyMatch(role -> role == user.getRole());
        if (!allowed) {
            throw new ForbiddenException("Insufficient permissions for this action");
        }

        RequestUserContext.set(request, user);
        return true;
    }

    private RequireRoles findAnnotation(HandlerMethod method) {
        RequireRoles methodAnnotation = method.getMethodAnnotation(RequireRoles.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        Optional<RequireRoles> classAnnotation = Optional.ofNullable(method.getBeanType().getAnnotation(RequireRoles.class));
        return classAnnotation.orElse(null);
    }
}
