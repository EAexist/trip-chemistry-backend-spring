package com.example.tripchemistry.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class LoginResultDTO {
    // https://gimquokka.github.io/spring/Spring_Jackson_is_%EC%83%9D%EB%9E%B5%EB%AC%B8%EC%A0%9C/
    private Boolean doRequireInitialization;
    private ProfileDTO profile;
}
