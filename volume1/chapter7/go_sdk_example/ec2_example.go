package main

import (
    "fmt"

      "github.com/aws/aws-sdk-go/aws"
      "github.com/aws/aws-sdk-go/aws/session"
      "github.com/aws/aws-sdk-go/service/ec2"
)

func main() {
    // Load session from shared config   
    sess := session.Must(session.NewSessionWithOptions(session.Options{
        SharedConfigState: session.SharedConfigEnable,
    }))

    // Create new EC2 client
    ec2Svc := ec2.New(sess)

    // Get image_id
    image_id := ""
    images_input := &ec2.DescribeImagesInput{
		Filters: []*ec2.Filter{
			{
				Name:   aws.String("description"),
				Values: []*string{aws.String("Amazon Linux AMI 2018.03.0*")},
			},
			{
				Name:   aws.String("virtualization-type"),
				Values: []*string{aws.String("hvm")},
			},
		},
	}
    
    result, err := ec2Svc.DescribeImages(images_input)
    if err != nil {
        fmt.Println("Error", err)
    } else {
        image_id = *result.Images[0].ImageId
    } 

    // Get security_group_id
    group_id := ""
    group_input := &ec2.DescribeSecurityGroupsInput{
		Filters: []*ec2.Filter{
			{
				Name:   aws.String("group-name"),
				Values: []*string{aws.String("default")},
			},
		},
	}
    
    result_groups, err := ec2Svc.DescribeSecurityGroups(group_input)
    if err != nil {
        fmt.Println("Error", err)
    } else {
        group_id = *result_groups.SecurityGroups[0].GroupId
    }
 
    // Get vpc_id
    vpc_id := ""
    vpc_input := &ec2.DescribeVpcsInput{
		Filters: []*ec2.Filter{
			{
				Name:   aws.String("isDefault"),
				Values: []*string{aws.String("true")},
			},
		},
	}
    
    result_vpcs, err := ec2Svc.DescribeVpcs(vpc_input)
    if err != nil {
        fmt.Println("Error", err)
    } else {
        vpc_id = *result_vpcs.Vpcs[0].VpcId
    }
    
    // Get subnet_id from previously computed vpc_id
    subnet_id := ""
    subnet_input := &ec2.DescribeSubnetsInput{
		Filters: []*ec2.Filter{
			{
				Name:   aws.String("vpc-id"),
				Values: []*string{aws.String(vpc_id)},
			},
		},
	}
    
    result_subnets, err := ec2Svc.DescribeSubnets(subnet_input)
    if err != nil {
        fmt.Println("Error", err)
    } else {
        subnet_id = *result_subnets.Subnets[0].SubnetId
    }
    fmt.Println("image_id=", image_id, 
        "group_id=", group_id,
        "vpc_id=", vpc_id,
        "subnet_id=", subnet_id,
    )     
    
    instance_id := ""
    run_input := &ec2.RunInstancesInput{
        ImageId: aws.String(image_id),
        SubnetId: aws.String(subnet_id),
        SecurityGroupIds: []*string {aws.String(group_id)},
        InstanceType: aws.String("t2.micro"),
        KeyName: aws.String("aws_intro_2"),
        MinCount: aws.Int64(1),
        MaxCount: aws.Int64(1),
	}
    
    reservation, err := ec2Svc.RunInstances(run_input)
    if err != nil {
        fmt.Println("Error", err)
    } else {
        instance_id = *reservation.Instances[0].InstanceId
    }
    fmt.Println("instance_id", instance_id)  
    
    ec2Svc.WaitUntilInstanceRunning(&ec2.DescribeInstancesInput{
        InstanceIds: []*string{aws.String(instance_id)},
    })  
}
