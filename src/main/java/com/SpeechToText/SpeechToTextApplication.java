package com.SpeechToText;

import com.SpeechToText.InfiniteStream.InfiniteStreamRecognize;
import com.SpeechToText.util.ElasticSearch;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Scanner;
import java.io.IOException;

@SpringBootApplication
public class SpeechToTextApplication {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(SpeechToTextApplication.class, args);
	}
}
