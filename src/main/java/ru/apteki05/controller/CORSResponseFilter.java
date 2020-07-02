package ru.apteki05.controller;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class CORSResponseFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE, PATCH");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "Accept-Encoding, origin, content-type, accept, token, x-auth-token, Access-Control-Allow-Origin, " +
                        "Access-Control-Allow-Methods, Access-Control-Max-Age, Access-Control-Allow-Headers, " +
                        "Content-Language, Content-Length, Keep-Alive, Authorization");
        chain.doFilter(req, res);
    }
}
