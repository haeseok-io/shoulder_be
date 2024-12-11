package org.shoulder.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtServiceImple implements JwtService {
    @Value("${custom.jwt.secretKey}")
    private String secretKey;

    @Override
    public String getToken(String key, Object value) {
        return null;
    }
}
