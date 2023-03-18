package br.com.devdojo.config;

public class SecurityConstants {
    static final String SECRET = "DevDojoSecret";
    static final String TOKEN_PREFIX = "Bearer ";
    static final String HEADER_STRING = "Authorization";
    static final String SIGN_UP_URL = "/users/sign-up";
    static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;
}
