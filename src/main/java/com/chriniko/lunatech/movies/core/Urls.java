package com.chriniko.lunatech.movies.core;

import com.chriniko.lunatech.movies.error.ProcessingException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@Component
public class Urls {

    // Note: Firstname%Surname ---> Firstname Surname
    public String decoder(String input) {
        try {
            return URLDecoder.decode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new ProcessingException("Urls#decoder, error: " + e.getMessage(), e);
        }
    }
}
