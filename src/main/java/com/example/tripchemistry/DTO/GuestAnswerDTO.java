package com.example.tripchemistry.DTO;

import org.springframework.data.annotation.Id;

import com.example.tripchemistry.model.TestAnswer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestAnswerDTO {

    @Id
    private String nickname;
    private String chemistryId;
    private TestAnswer testAnswer;
}
