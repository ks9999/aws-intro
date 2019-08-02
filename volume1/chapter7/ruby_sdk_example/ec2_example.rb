require 'rubygems'
require 'bundler/setup'
require 'aws-sdk-ec2'

ec2 = Aws::EC2::Client.new(region: 'us-east-1')

# Get image_id.
images_result = ec2.describe_images(
    {filters: [
        {name: 'description', values: ['Amazon Linux AMI 2018.03*']},
        {name: 'virtualization-type', values: ['hvm']}
    ]}
    )
image_id = images_result.images[0].image_id

# Get security_group_id.
sg_result = ec2.describe_security_groups(
    {filters: [
        {name: 'group-name', values: ['default']}
    ]}
    )
group_id = sg_result.security_groups[0].group_id

# Get vpc_id.
vpcs_result = ec2.describe_vpcs(
    {filters: [
        {name: 'isDefault', values: ['true']}
    ]}
    )
vpc_id = vpcs_result.vpcs[0].vpc_id

# Get subnet_id using previously computed vpc_id
subnets_result = ec2.describe_subnets(
    {filters: [
        {name: 'vpc-id', values: [vpc_id]}
    ]}
    )
subnet_id = subnets_result.subnets[0].subnet_id
puts "image_id=#{image_id} group_id=#{group_id} vpc_id=#{vpc_id} subnet_id=#{subnet_id}"

# Create instance
ec2_resource = Aws::EC2::Resource.new(region: 'us-east-1')
instance = ec2_resource.create_instances({
  image_id: image_id,
  subnet_id: subnet_id,
  security_group_ids: [group_id],
  instance_type: 't2.micro',  
  key_name: 'aws_intro_2',  
  min_count: 1,
  max_count: 1
})

instance_id = instance.first.id
puts "instance_id=#{instance_id}" 
instance.batch_create_tags({ tags: [{ key: 'Name', value: 'Created by ec2_example.rb' }]})
ec2_resource.client.wait_until(:instance_status_ok, {instance_ids: [instance_id]})
