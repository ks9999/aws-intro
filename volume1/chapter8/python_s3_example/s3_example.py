import boto3
import os

# Create a S3 client
s3 = boto3.client('s3')
response = s3.list_buckets()
buckets = response['Buckets']
bucket_exists = False
bucket_name = "test1.replacewithyourdomain.com"
for b in buckets:
    bname = b['Name']
    if "test1.replacewithyourdomain.com" in bname:
        bucket_exists = True
        break
        
# Create bucket if it does not exist.
if not bucket_exists:
    response = s3.create_bucket(
        Bucket=bucket_name,
        ACL='public-read'
    )
    print("Created  ", response['Location'])


# Upload to bucket.   
# Replace pathname with path to your file.  
pathname = "path_to_your_file"
key = os.path.basename(pathname)
s3.upload_file(pathname, bucket_name, key)
