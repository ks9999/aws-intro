package com.aws_intro.example;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.Bucket;

import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.Transfer.TransferState;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;

import java.util.List;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class S3Example
{
    public static void main(String[] args)
    {
        String bucket_name = "test1.replacewithyourdomain.com";
        final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        Bucket bucket = null;
        if (s3.doesBucketExist(bucket_name)) {
            // Get bucket name.
            List<Bucket> buckets = s3.listBuckets();
            for (Bucket b : buckets) {
                if (b.getName().equals(bucket_name)) {
                    bucket = b;
                    break;
                }
            }
        } else {
            try {
                bucket = s3.createBucket(bucket_name);
            } catch (AmazonS3Exception e) {
                System.err.println(e.getErrorMessage());
            }
        }

        // Upload file to bucket.
        String file_path = "path_to_your_file";
        File f = new File(file_path);
        String key = f.getName();
        System.out.println("key=" +key);
        TransferManager xfer_mgr = TransferManagerBuilder.standard().build();
        try {
            Upload u = xfer_mgr.upload(bucket_name, key, f);
            printProgressBar(0.0);
            u.addProgressListener(new ProgressListener() {
                public void progressChanged(ProgressEvent e) {
                    double pct = e.getBytesTransferred() * 100.0 / e.getBytes();
                    eraseProgressBar();
                    printProgressBar(pct);
                }
            });
            // Block with Transfer.waitForCompletion()
            waitForCompletion(u);
            // Print the final state of the transfer.
            TransferState xfer_state = u.getState();
            System.out.println(": " + xfer_state);
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        xfer_mgr.shutdownNow();
    }
    
    // Prints a simple text progressbar: [#####     ]
    public static void printProgressBar(double pct)
    {
        // If bar_size changes, then change erase_bar (in eraseProgressBar) to
        // match.
        final int bar_size = 40;
        final String empty_bar = "                                        ";
        final String filled_bar = "########################################";
        int amt_full = (int)(bar_size * (pct / 100.0));
        System.out.format("  [%s%s]", filled_bar.substring(0, amt_full),
              empty_bar.substring(0, bar_size - amt_full));
    }

    // Erases the progress bar.
    public static void eraseProgressBar()
    {
        // erase_bar is bar_size (from printProgressBar) + 4 chars.
        final String erase_bar = "\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b";
        System.out.format(erase_bar);
    }    
    
    // Waits for the transfer to complete.
    public static void waitForCompletion(Transfer xfer)
    {
        try {
            xfer.waitForCompletion();
        } catch (AmazonServiceException e) {
            System.err.println("Amazon service error: " + e.getMessage());
            System.exit(1);
        } catch (AmazonClientException e) {
            System.err.println("Amazon client error: " + e.getMessage());
            System.exit(1);
        } catch (InterruptedException e) {
            System.err.println("Transfer interrupted: " + e.getMessage());
            System.exit(1);
        }
    }    
}
