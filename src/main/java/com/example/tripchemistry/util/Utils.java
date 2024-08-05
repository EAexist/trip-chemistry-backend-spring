package com.example.tripchemistry.util;

import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class Utils {

    public static <T> void logMono(Mono<T> target, String prefix) {
        target.map(it -> it.toString())
                .defaultIfEmpty("!empty!")
                .subscribe(it -> log.info(prefix + it));
    }

    public static String tokenizeToLetter(String input) {
        String[] chosungs = { "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ",
                "ㅎ" };
        String[] jungsungs = { "ㅏ", "ㅐ", "ㅑ", "ㅒ", "ㅓ", "ㅔ", "ㅕ", "ㅖ", "ㅗ", "ㅘ", "ㅙ", "ㅚ", "ㅛ", "ㅜ", "ㅝ", "ㅞ", "ㅟ", "ㅠ",
                "ㅡ", "ㅢ", "ㅣ" };
        String[] jongsungs = { "", "ㄱ", "ㄲ", "ㄳ", "ㄴ", "ㄵ", "ㄶ", "ㄷ", "ㄹ", "ㄺ", "ㄻ", "ㄼ", "ㄽ", "ㄾ", "ㄿ", "ㅀ", "ㅁ", "ㅂ",
                "ㅄ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ" };

        final int CHO_BASE = 0x1100;
        final int JUNG_BASE = 0x1161;
        final int JONG_BASE = 0x11a7;

        String result = input.chars()
                .mapToObj( c -> {
                    if( c >= 0xAC00 ){
                        int uniBase = c - 0xAC00;
                        // log.info("[tokenizeKoreanToLetter] " + Character.toString((char)4462));
                        // log.info("[tokenizeKoreanToLetter] " + (byte)4519);
                        // log.info("[tokenizeKoreanToLetter] " + (JONG_BASE + uniBase % 28) );
                        char[] cs = new char[]{ 
                            (char) (CHO_BASE + ( uniBase / 28 ) / 21),
                            (char) (JUNG_BASE + ( uniBase / 28 ) % 21),
                            (char) (JONG_BASE + uniBase % 28)
                        };
                        // log.info("[tokenizeKoreanToLetter] " + cs);
                        // log.info("[tokenizeKoreanToLetter] " + new String(cs));
                        return new String(cs);
                    }
                    else {
                        return String.valueOf((char)c);
                    }
                }).collect(Collectors.joining());

        log.info("[tokenizeKoreanToLetter] " + input + "=>" + result);

        return ( 
            input.chars()
            .mapToObj( c -> {
                if( c >= 0xAC00 ){
                    int uniBase = c - 0xAC00;
                    char[] cs = new char[]{ 
                        (char) (CHO_BASE + ( uniBase / 28 ) / 21),
                        (char) (JUNG_BASE + ( uniBase / 28 ) % 21),
                        (char) (JONG_BASE + uniBase % 28)
                    };
                    return new String(cs);
                }
                else {
                    return String.valueOf((char)c);
                }
            }).collect(Collectors.joining())
        );
    }
}
