package com.example.tripchemistry.model.answer;

import org.springframework.data.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

// @Document
@Data
// @RequiredArgsConstructor
@NoArgsConstructor
public class LeadershipTestAnswer {
    
    /* Id */
    // @Id    
    // @NonNull
    // private Long id;

    private int leadership = 2;
}
