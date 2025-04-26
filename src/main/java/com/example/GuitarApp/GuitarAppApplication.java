package com.example.GuitarApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GuitarAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(GuitarAppApplication.class, args);
	}

}

//TODO: ПАМЯТАЙ!!! в таблиці комент при видаленні юзера поле з його id стає null.
// 		Треба чекати нал поінтер при натисканні на комент неіснуйочого юзера.
