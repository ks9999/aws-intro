import boto3

# Create an EC2 client
ec2 = boto3.client('ec2')

# Get image_id.
images = ec2.describe_images(
    Filters=[
                {'Name': 'description',
                'Values': ["Amazon Linux AMI 2018.03.0*"]
                },
                {'Name': 'virtualization-type',
                'Values': ["hvm"]
                },
            ])
image_id = images['Images'][0]['ImageId']

# Get security_group_id
group_ids = ec2.describe_security_groups(
    Filters=[
                {'Name': 'group-name',
                'Values': ["default"]
                },
            ])
security_group_id = group_ids['SecurityGroups'][0]['GroupId']

# Get vpc_id.
vpcs = ec2.describe_vpcs(
    Filters=[
                {'Name': 'isDefault',
                'Values': ["true"]
                },
            ])

vpc_id = vpcs['Vpcs'][0]['VpcId']

# Get subnet_id using previously computed vpc_id
subnet_ids = ec2.describe_subnets(
    Filters=[
                {'Name': 'vpc-id',
                'Values': [vpc_id]
                },
            ])
subnet_id = subnet_ids['Subnets'][0]['SubnetId']

# Display extracted parameters
print("image_id=", image_id,
 " security_group_id=", security_group_id,
 " vpc_id=", vpc_id,
 " subnet_id=", subnet_id)

# Get running instances
instances = ec2.run_instances(
    ImageId=image_id,
    SecurityGroupIds=[
        security_group_id
    ],
    SubnetId=subnet_id,

    InstanceType="t2.micro",
    KeyName="aws_intro_2",
    MaxCount=1,
    MinCount=1
)

for i in instances['Instances']:
    state = i['State']
    if state['Name'] in ('pending'):
        instance_id = i['InstanceId']
        print(instance_id)
        break

ec2_waiter = ec2.get_waiter('instance_running')
ec2_waiter.wait(InstanceIds=[instance_id])

