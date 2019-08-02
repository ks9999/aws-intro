require 'rubygems'
require 'bundler/setup'
require 'aws-sdk-s3'
require 'pathname'

s3 = Aws::S3::Client.new(region: 'us-east-1')

# Does bucket exist?
bucket_name = "test1.replacewithyourdomain.com"
list_results = s3.list_buckets()
bucket_exists = false
for b in list_results.buckets do
    if b.name == bucket_name
        bucket_exists = true
        break
    end
end

# Create bucket if it does not exist
bucket_exists = false
if !bucket_exists
  create_results = s3.create_bucket({
      acl: "public-read",
      bucket: bucket_name,
    })
end

# Upload file
file_path = "path_to_your_file"
file_name = File.basename(file_path)

put_results = s3.put_object({
  body: file_path, 
  bucket: bucket_name, 
  key: file_name, 
})

