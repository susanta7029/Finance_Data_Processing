package com.finance.backend.security;

import com.finance.backend.model.AppUser;
import jakarta.servlet.http.HttpServletRequest;

public final class RequestUserContext {

    private static final String KEY = "currentUser";

    private RequestUserContext() {
    }

    public static void set(HttpServletRequest request, AppUser user) {
        request.setAttribute(KEY, user);
    }

    public static AppUser get(HttpServletRequest request) {
        return (AppUser) request.getAttribute(KEY);
    }
}
