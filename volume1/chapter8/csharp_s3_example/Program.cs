using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon;
using Amazon.S3;
using Amazon.S3.Model;
using Amazon.S3.Transfer;

namespace S3Example
{
    class Program
    {
        static void Main(string[] args)
        {
            MainAsync(args).GetAwaiter().GetResult();
        }

        static async Task MainAsync(string[] args)
        {
            // Check if bucket exists.
            string bucket_name = "test1.replacewithyourdomain.com";
            AmazonS3Client s3 = new AmazonS3Client(RegionEndpoint.USEast1);
            ListBucketsResponse list_response = s3.ListBuckets();
            List<S3Bucket> buckets = list_response.Buckets;
            bool bucket_exists = false;
            foreach (S3Bucket b in buckets)
            {
                string bname = b.BucketName;
                if (bname.Equals(bucket_name))
                {
                    bucket_exists = true;
                    break;
                }
            }

            // Create bucket if it does not exist.
            if (!bucket_exists)
            {
                s3.PutBucket(new PutBucketRequest
                    {
                        BucketName = bucket_name,
                        CannedACL = S3CannedACL.PublicRead
                    }
                );
                
            }

            // Upload file to bucket.
            string file_path = "path_to_your_file";
            TransferUtility transfer_utility = new TransferUtility(s3);
            await transfer_utility.UploadAsync(file_path, bucket_name);

        }
    }
}
