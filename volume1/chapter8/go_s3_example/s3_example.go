package main

import (
    "github.com/aws/aws-sdk-go/aws"
    "github.com/aws/aws-sdk-go/aws/session"
    "github.com/aws/aws-sdk-go/service/s3"
    "github.com/aws/aws-sdk-go/service/s3/s3manager"    
    "fmt"
    "os"
    "path/filepath"
)

func main() {
	sess, err := session.NewSession(&aws.Config{
		Region: aws.String("us-east-1")},
	)
	if (err != nil) {
		fmt.Println("Error", err)
		os.Exit(1)
	}

	// Create S3 service client
	svc := s3.New(sess)
	result, err := svc.ListBuckets(nil)
	bucket_name := "test1.replacewithyourdomain.com"
    	// range returns index, bucket pairs. We do not need the index
    	// so, we use the blank identifier _.
    	bucket_exists := false
	for _, b := range result.Buckets {
        bname := aws.StringValue(b.Name)
        if bname == bucket_name {
            bucket_exists = true
            break
        }
	}
    	bucket_exists = false
       if !bucket_exists {
           // Create bucket
           _, err = svc.CreateBucket(&s3.CreateBucketInput{
                    Bucket: aws.String(bucket_name),
                    })
           if err != nil {
               fmt.Printf("Unable to create bucket %s, %v", bucket_name, err)
           }        
       }
    
       // Upload file.
    svc_upload := s3manager.NewUploader(sess)    
    path_name := "path_to_your_file"
    file_name := filepath.Base(path_name)  
    fmt.Printf("file_name=%s", file_name)
	file, err := os.Open(path_name)
	if err != nil {
		fmt.Println("Failed to open file", path_name, err)
		os.Exit(1)
	}
	defer file.Close()  
    
	_, err_upload := svc_upload.Upload(&s3manager.UploadInput{
		Bucket: aws.String(bucket_name),
		Key:    aws.String(file_name),
		Body:   file,
	})
	if err_upload != nil {
		fmt.Println("error", err_upload)
		os.Exit(1)
	}    	
}
