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
public class CityTestAnswer {
    
    /* Id */
    // @Id
    // @NonNull
    // private Long id;

    private int metropolis = 2; 
    private int history = 3;
    private int nature = 4;
}
