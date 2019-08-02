#!/bin/bash
IMAGE_ID=$(aws ec2 describe-images  --filters "Name=description, Values=Amazon Linux AMI 2018.03.0*" "Name=virtualization-type, Values=hvm" --query "Images[0].ImageId" --output text)

VPC_ID=$(aws ec2 describe-vpcs --filter="Name=isDefault, Values=true" --query="Vpcs[0].VpcId" --output text)

SUBNET_ID=$(aws ec2 describe-subnets --filter="Name=vpc-id, Values=$VPC_ID" --query="Subnets[0].SubnetId" --output text)

SECURITY_GROUP_ID=$(aws ec2 describe-security-groups --filter "Name=group-name, Values=default" --query "SecurityGroups[0].GroupId" --output text)

INSTANCE_ID=$(aws ec2 run-instances --image-id $IMAGE_ID --subnet-id $SUBNET_ID --security-group-ids $SECURITY_GROUP_ID --instance-type t2.micro --key-name aws_intro_2 --query "Instances[0].InstanceId" --output text)

echo "Waiting for $INSTANCE_ID."
aws ec2 wait instance-running --instance-ids $INSTANCE_ID

