package com.nonder.example;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class MovieDbApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieDbApplication.class, args);
    }
}

@RestController
class MovieController {

    @GetMapping("/movies/{id}")
    public ResponseEntity<Movie> getMovieDetails(@PathVariable int id, @RequestParam String language) {
        return ResponseEntity.status(200).body(new Movie("Inception", "Christopher Nolan"));
    }
}

@AllArgsConstructor
@NoArgsConstructor
@Getter
class Movie {
    private String title;
    private String director;
}
