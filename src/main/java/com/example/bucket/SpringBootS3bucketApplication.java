package com.example.bucket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootS3bucketApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootS3bucketApplication.class, args);
		
		/*
		
		amazonProperties:
  endpointUrl: https://s3.us-east-1.amazonaws.com
  accessKey: AKIAJXGLQRJB77KBBF5A
  secretKey: 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
  
  */
	}
}
