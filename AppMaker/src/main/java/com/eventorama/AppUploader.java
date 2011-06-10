package com.eventorama;

import java.io.File;
import java.net.URL;
import java.util.Date;

public interface AppUploader {
	URL upload(File f, String uuid, Date expiration); 	
}
