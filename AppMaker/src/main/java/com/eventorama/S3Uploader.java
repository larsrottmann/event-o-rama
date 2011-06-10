package com.eventorama;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import static com.eventorama.ConfigurationParameters.*;
public class S3Uploader implements AppUploader {


	private AmazonS3 s3 = null;

	public S3Uploader() {
		try {
			InputStream i = new FileInputStream(S3_CREDENTIALS_FILE);
			PropertiesCredentials p = new PropertiesCredentials(i);
			s3 = new AmazonS3Client(p);
			s3.createBucket(BUCKET_NAME);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} catch (AmazonServiceException e) {
			throw new IllegalStateException(e);
		} catch (AmazonClientException e) {
			throw new IllegalStateException(e);
		}

	}

	@Override
	public URL upload(File f, String uuid, Date expiration) throws IllegalStateException {
		try {
			s3.putObject(new PutObjectRequest(BUCKET_NAME, uuid, f));
			return s3.generatePresignedUrl(BUCKET_NAME, uuid, expiration);
		} catch (AmazonServiceException e) {
			throw new IllegalStateException(e);
		} catch (AmazonClientException e) {
			throw new IllegalStateException(e);
		}
	}

}
