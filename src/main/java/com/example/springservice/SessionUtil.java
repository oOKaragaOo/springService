package com.example.springservice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUtil {
    public static void storeUserSession(HttpServletRequest request, User user) {
        if (user == null) return; // ✅  user shout not null

        HttpSession session = request.getSession(true);
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(30 * 60);  //✅  session timeout  30 min
    }
    public static void clearUserSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
    public static Integer getUserIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;

        User user = (User) session.getAttribute("user");
        return user != null ? user.getUser_id() : null;
    }
}