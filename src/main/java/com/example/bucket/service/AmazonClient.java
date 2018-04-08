package com.example.bucket.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.datamodeling.S3ClientCache;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Service
public class AmazonClient {
	
	//@Autowired
    private AmazonS3 s3client;
    
    private static final String SUFFIX = "/";

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;
   // @Value("${amazonProperties.bucketName}")
    private String bucketName;
    private String folderName;
    private String sfoldername;
    @Value("${amazonProperties.accessKey}")
    private String accessKey;
    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @PostConstruct
    private void initializeAmazon() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
        this.s3client = new AmazonS3Client(credentials);
        bucketName="test-bucketaccp12";
        folderName="temp";
        sfoldername = "original";
        s3client.setEndpoint(endpointUrl);
    	
        if(!(s3client.doesBucketExist(bucketName)))
        {
        	// Note that CreateBucketRequest does not specify region. So bucket is 
        	// created in the region specified in the client.
        	s3client.createBucket(bucketName);
        	s3client.setRegion(Region.getRegion(Regions.US_EAST_1));
        		
        }
        
    	
       
        String bucketLocation = s3client.getBucketLocation(new GetBucketLocationRequest(bucketName));	
        System.out.println("bucket location = " + bucketLocation);
    }
    
    
    
    
    
    
    
    
//	public static void createFolder(String bucketName, String folderName, AmazonS3 client) {
//		// create meta-data for your folder and set content-length to 0
//		ObjectMetadata metadata = new ObjectMetadata();
//		metadata.setContentLength(0);
//		// create empty content
//		InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
//		// create a PutObjectRequest passing the folder name suffixed by /
//		PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName,
//				folderName + SUFFIX, emptyContent, metadata);
//		// send request to S3 to create folder
//		client.putObject(putObjectRequest);
//	}
	
	
	
	public  void moveFileName(String bucketName,String filename,String destination) {
		
		
		s3client.copyObject(bucketName, filename, bucketName, filename);
		
	}

    
    
    
    
    
    
    
    
    
    
    

    public String uploadFile(MultipartFile multipartFile) {
        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = folderName + SUFFIX+generateFileName(multipartFile);
            fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
           e.printStackTrace();
        }
        return fileUrl;
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }

    public String deleteFileFromS3Bucket(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        return "Successfully deleted";
    }

    
    public String moveFile(String fileName) {
    	String sfileName=folderName + SUFFIX+fileName;
    	String dfileName = sfoldername + SUFFIX+fileName;
    	moveFileName(bucketName,sfileName,dfileName) ;
		return "successfuly moved";      
    }
    
}
