package com.aws_intro.example;

import java.io.IOException;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeImagesResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.waiters.WaiterParameters;
import com.amazonaws.waiters.Waiter;
import com.amazonaws.services.ec2.waiters.AmazonEC2Waiters;

public class StartEC2 {

    public static void main(String[] args) throws IOException {
        AmazonEC2 ec2 = new AmazonEC2Client();
        Region us_east_1 = Region.getRegion(Regions.US_EAST_1);
        ec2.setRegion(us_east_1);
        
        // Get image_id 
        DescribeImagesRequest images_request = new DescribeImagesRequest()
            .withFilters(
                new Filter("description").withValues("Amazon Linux AMI 2018.03.0*"),
                new Filter("run").withValues("hvm")
            );
        DescribeImagesResult images_result = ec2.describeImages(images_request);       
        String image_id = images_result.getImages().get(0).getImageId();
        
        // Get security_group_id
        DescribeSecurityGroupsRequest sg_request = new DescribeSecurityGroupsRequest()
            .withFilters(
                new Filter("group-name").withValues("default")
            );
        DescribeSecurityGroupsResult sg_result = ec2.describeSecurityGroups(sg_request);       
        String group_id = sg_result.getSecurityGroups().get(0).getGroupId();
        
        // Get vcpc_id
        DescribeVpcsRequest vpcs_request = new DescribeVpcsRequest()
            .withFilters(
                new Filter("isDefault").withValues("true")
            );
        DescribeVpcsResult vpc_result = ec2.describeVpcs(vpcs_request);       
        String vpc_id = vpc_result.getVpcs().get(0).getVpcId(); 

        // Get subnet_id
        DescribeSubnetsRequest subnets_request = new DescribeSubnetsRequest()
            .withFilters(
                new Filter("vpc-id").withValues(vpc_id)
            );
        DescribeSubnetsResult subnet_result = ec2.describeSubnets(subnets_request);       
        String subnet_id = subnet_result.getSubnets().get(0).getSubnetId();
           
        System.out.println("image_id=" + image_id +
                           " vpc_id=" + vpc_id +
                           " subnet_id=" + subnet_id +     
                           " group_id=" + group_id  
                           );    
        
        // Start instance
        RunInstancesRequest run_request = new RunInstancesRequest()
            .withImageId(image_id)
            .withSubnetId(subnet_id)
            .withInstanceType("t2.micro")
            .withKeyName("aws_intro_2")
            .withMaxCount(1)
            .withMinCount(1);
        RunInstancesResult run_result = ec2.runInstances(run_request);
 
        String instance_id = run_result.getReservation().getInstances().get(0).getInstanceId();                          
        System.out.println("instance_id=" + instance_id);

        DescribeInstanceStatusRequest status_request = 
              = new DescribeInstanceStatusRequest()                                                        
                    .withInstanceIds(instance_id);
           
        Waiter waiter = ec2.waiters().instanceStatusOk();      
        waiter.run(
          new WaiterParameters()
            .withRequest(status_request)

        );            
    }
}
