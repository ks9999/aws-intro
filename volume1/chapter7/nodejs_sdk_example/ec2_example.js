// Load the SDK.
var AWS = require('aws-sdk');

// Create an EC2 client
var ec2 = new AWS.EC2({region: 'us-east-1'});

// Get image_id.
var image_id;
var filter_images = {Filters: [
	{Name: "description", Values: ["Amazon Linux AMI 2018.03.0*"]},
	{Name: "virtualization-type", Values: ["hvm"]}	
	]};
var request_images_promise = ec2.describeImages(filter_images).promise();
request_images_promise.then(
	function(data) {
		image_id = data.Images[0].ImageId;			
	},
	function(error) {
		console.log(err, err.stack); // an error occurred
	}
);

// Get security_group_id
var security_group_id;
var filter_sg = {Filters: [
{Name: "group-name", Values: ["default"]}	
]};
var request_sg_id_promise = ec2.describeSecurityGroups(filter_sg).promise();
request_sg_id_promise.then(
	function(data) {
		security_group_id = data.SecurityGroups[0].GroupId;		
	},
	function(error) {
		console.log(err, err.stack); // an error occurred		
	}
);

// Get vpc_id.
var vpc_id;
var filter_vpc = {Filters: [
{Name: "isDefault", Values: ["true"]}	
]};
var request_vpc_id_promise = ec2.describeVpcs(filter_vpc).promise();
request_vpc_id_promise.then(
	function(data) {
		vpc_id = data.Vpcs[0].VpcId;		
	},
	function(error) {
		console.log(err, err.stack); // an error occurred		
	}
);

// Get subnet_id using previously computed vpc_id
var subnet_id;
var filter_subnet = {Filters: [
{Name: "vpc-id", Values: [vpc_id]}	
]};
var request_subnet_id_promise = ec2.describeSubnets(filter_subnet).promise();
request_subnet_id_promise.then(
	function(data) {
		subnet_id = data.Subnets[0].SubnetId;		
	},
	function(error) {
		console.log(err, err.stack); // an error occurred		
	}
);

// Display extracted parameters
console.log("image_id=", image_id,
 " security_group_id=", security_group_id,
 " vpc_id=", vpc_id, 
 " subnet_id=", subnet_id);
 

var instance_params = {
	// Params with variable arguments
	ImageId: image_id,
	SecurityGroupIds: [
		security_group_id
	],
	SubnetId: subnet_id,
	
	// Params with constant arguments
	InstanceType: "t2.micro",
	KeyName: "aws_intro_2",
	MaxCount: 1,
	MinCount: 1

};

// Start instance based on computed parameters
var instance_id;
var run_instance_promise = ec2.runInstances(instance_params).promise();
run_instance_promise.then(
	function(data) {
		instance_id = data.Instances[0].InstanceId;	
		console.log("instance_id=", instance_id);
	},
	function(error) {
		console.log(err, err.stack); // an error occurred		
	}
);

