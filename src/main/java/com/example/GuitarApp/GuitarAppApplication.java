package com.example.GuitarApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GuitarAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(GuitarAppApplication.class, args);
	}

}

//TODO: Треба вирішити проблему з унікальними значеннями. Їх варто валідувати в застосунку а не за помилками з бд.
// 		Для цього можна використовувати кастомні аннотації, треба дослідити можливість створення універсальної аннотації.
