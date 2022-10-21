package com.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;
import org.xmlpull.v1.XmlPullParserException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.utils.Constant;
import com.utils.PoolHttpClientUtil;
import com.utils.SysLog;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.XmlParserException;
import io.minio.messages.DeleteError;
import io.minio.messages.Item;

public class MinioTools {
	  private static MinioClient minioClient;
	  static{
			try {
//				 minioClient = new MinioClient("http://47.103.56.123:59090", "minioadmin", "minioadmin123?");
				 minioClient = new MinioClient(Constant.MINIO_IP, Constant.MINIO_ACCOUNT, Constant.MINIO_PASSWORD);
			} catch (InvalidEndpointException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }
	
	
	  public static String uploadFile(MultipartFile file,String bucket,String path) {
		  try {
	      
	      // 检查存储桶是否已经存在
	      boolean isExist= minioClient.bucketExists(bucket);
	      if(!isExist) {
	        minioClient.makeBucket(bucket);
	      }

	      // 创建对象
  	      PutObjectOptions poo=new PutObjectOptions(file.getSize(),-1);
  	      poo.setContentType(file.getContentType());
  	      minioClient.putObject(bucket, path,file.getInputStream(), poo);
  	      // 使用putObject上传一个文件到存储桶中。
	    }catch(MinioException e) {
	    	SysLog.error("Error occurred: " + e);
	    }catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		 return Constant.FASTDFS_URL +bucket+path;
	  }
	  
	  public static String uploadFile(InputStream is,String bucket,String contentType,String path) {
		  try {
	      // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
//	      MinioClient minioClient = new MinioClient("http://47.103.56.123:59090", "minioadmin", "minioadmin123?");
	      
	      // 检查存储桶是否已经存在
	      boolean isExist= minioClient.bucketExists(bucket);
	      if(!isExist) {
	         minioClient.makeBucket(bucket);
	      }

	      // 创建对象
  	      PutObjectOptions poo=new PutObjectOptions(is.available(),-1);
  	      poo.setContentType(contentType);
  	      minioClient.putObject(bucket, path,is, poo);
  	      // 使用putObject上传一个文件到存储桶中。
	    }catch(MinioException e) {
	    	SysLog.info("Error occurred: " + e);
	    }catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
		 return Constant.FASTDFS_URL +bucket+path;
	  }
	  
	  
 public static List<String> getFileList(String bucket,String path) {
	   List<String> flist=new ArrayList<String>();
	   try {
		  boolean found = minioClient.bucketExists(bucket);
	      if (!found) {
	    	  SysLog.info("my-bucketname does not exist");
	    	  return flist;
	      }
    	  Iterable<Result<Item>> myObjects=minioClient.listObjects(Constant.BUCKET_NAME, path);
		   for (Result<Item> result : myObjects) {
               Item item = result.get();
               flist.add(Constant.FASTDFS_URL +bucket+"/"+item.objectName());
               SysLog.info(item.lastModified() + ", " + item.size() + ", " + item.objectName());
           }

		} catch (XmlParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InsufficientDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidBucketNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      return flist;
	  }
 
 public static List<String> getObjectList(String bucket,String path) {
	   List<String> flist=new ArrayList<String>();
	   try {
		  boolean found = minioClient.bucketExists(bucket);
	      if (!found) {
	    	  return flist;
	      }
	       Iterable<Result<Item>> myObjects=minioClient.listObjects(Constant.BUCKET_NAME, path);
		   for (Result<Item> result : myObjects) {
             Item item = result.get();
             flist.add(item.objectName());
         }

		} catch (XmlParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ErrorResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InsufficientDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidBucketNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      return flist;
	  }
	
	 public static InputStream getObject(String bucketName, String objectName) {
		 try {
			  // 调用statObject()来判断对象是否存在。
			  // 如果不存在, statObject()抛出异常,
			  // 否则则代表对象存在。
			  ObjectStat objectStat = minioClient.statObject(bucketName, objectName);
			  SysLog.info(objectStat.toString());
			  // 获取"myobject"的输入流。
			  InputStream stream = minioClient.getObject(bucketName, objectName);
			  return stream;
			} catch (MinioException e) {
				SysLog.info("Error occurred: " + e);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 return null;
	 }
	 
	 
	 
	 public static void removeObjects(String bucketName, Iterable<String> objectNames) {
		 try {
			 Iterable<Result<DeleteError>> rlist= minioClient.removeObjects(bucketName, objectNames);
		      // 删除my-bucketname里的多个对象
		      for (Result<DeleteError> errorResult: rlist) {
		        DeleteError error = errorResult.get();
		        if(null!=error)
		        SysLog.info("Failed to remove '" + error.objectName() + "'. Error:" + error.message());
		      }
		} catch (MinioException e) {
			SysLog.info("Error: " + e);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	
	  public static void main(String[] args) {
		  
	  }
}
