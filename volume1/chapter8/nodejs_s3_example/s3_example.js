// Load the AWS SDK for Node.js
var AWS = require('aws-sdk');
// Set the region 
AWS.config.update({region: 'us-east-1'});

// Create S3 service object
s3 = new AWS.S3({apiVersion: '2006-03-01'});

// Call S3 to list the buckets
s3.listBuckets(function(err, data) {
  if (err) {
    console.log("Error", err);
  } else {
    var bucket_exists = false;
    for (var i = 0; i < data.Buckets.length; ++i) {
        var bucket = data.Buckets[i].Name;
        if (bucket.includes("test1.replacewithyourdomain.com")) {
           bucket_exists = true;
           break;
        }
    }
    if (!bucket_exists) {
        // Create bucket.
        var bucket_params = {
          Bucket : "test1.replacewithyourdomain.com",
          ACL : 'public-read'
        };

        // Call S3 to create the bucket
        s3.createBucket(bucket_params, function(err, data) {
          if (err) {
            console.log("Error", err);
          } else {
            console.log("Success", data.Location);
          }
        });
    }
    
    // Call S3 to upload file.
    //
    
    // Setup the file stream
    var file_system = require('fs');
    // Replace file with pathname of actual file.
    var file = "path_to_your_file";
    var file_stream = file_system.createReadStream(file);
    file_stream.on('error', function(err) {
      console.log('File Error', err);
    });    
    
    var path = require('path');    
    var key = path.basename(file);
    var body = file_stream;    
    var upload_params = {
        Bucket: "test1.replacewithyourdomain.com", 
        Key: key, 
        Body: body
    };

    // Call S3.upload() to upload file.
    s3.upload (upload_params, function (err, data) {
      if (err) {
        console.log("Error", err);
      } if (data) {
        console.log("Upload Success", data.Location);
      }
    });    
 
  }
});

