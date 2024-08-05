package com.example.tripchemistry.model.answer;
import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

// @Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityChemistry {
    
    /* Id */
    // @Id
    // @NonNull
    // private Long id;

    private float metropolis = -1; 
    private float history = -1;
    private float nature = -1;
}
