using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Amazon;
using Amazon.EC2;
using Amazon.EC2.Model;

namespace StartEC2
{
    class Program
    {
        static void Main(string[] args)
        {
            var ec2 = new AmazonEC2Client(RegionEndpoint.USEast1);

            // Get image_id
            var images_request = new DescribeImagesRequest();
            image_request.Filters = new List<Filter> {
                new Filter {Name="description",
                            Values = new List<string>() { "Amazon Linux AMI 2018.03.0*" }
                           },
                new Filter {Name="virtualization-type",
                            Values = new List<string>() { "hvm" }
                           }
            };
            var image_result = ec2.DescribeImages(image_request);
            string image_id = image_result.Images[0].ImageId;


            // Get security_group_id
            var sg_request = new DescribeSecurityGroupsRequest();
            sg_request.Filters = new List<Filter> {
                new Filter {Name="group-name",
                            Values = new List<string>() { "default" }
                           }
            };
            var sg_result = ec2.DescribeSecurityGroups(sg_request);
            string group_id = sg_result.SecurityGroups[0].GroupId;

            // Get vpcs_id
            var vpcs_request = new DescribeVpcsRequest();
            vpcs_request.Filters = new List<Filter> {
                new Filter {Name="isDefault",
                            Values = new List<string>() { "true" }
                           }
            };
            var vpcs_result = ec2.DescribeVpcs(vpcs_request);
            string vpc_id = vpcs_result.Vpcs[0].VpcId;

            // Get subnet_id
            var subnet_request = new DescribeSubnetsRequest();
            subnet_request.Filters = new List<Filter> {
                new Filter {Name="vpc-id",
                            Values = new List<string>() {vpc_id}
                           }
            };
            var subnet_result = ec2.DescribeSubnets(subnet_request);
            string subnet_id = subnet_result.Subnets[0].SubnetId;

            Console.WriteLine("image_id={0} group_id={1} vpc_id={2} subnet_id={3}",
                image_id, group_id, vpc_id, subnet_id);


            // Run instance
            var run_request = new RunInstancesRequest
            {
                ImageId = image_id,
                SecurityGroupIds = new List<string> { group_id },
                SubnetId = subnet_id,
                InstanceType = "t2.micro",
                KeyName = "aws_intro_2",
                MaxCount = 1,
                MinCount = 1
            };
            var run_result = ec2.RunInstances(run_request);
            string instance_id = run_result.Reservation.Instances[0].InstanceId;
            Console.WriteLine("instance_id=", instance_id);
 
        }
    }
}
