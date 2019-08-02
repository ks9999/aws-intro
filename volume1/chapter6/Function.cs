using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

using Amazon.Lambda.Core;

// Assembly attribute to enable the Lambda function's JSON input to be converted into a .NET class.
[assembly: LambdaSerializer(typeof(Amazon.Lambda.Serialization.Json.JsonSerializer))]

namespace palindrome
{
    public class Function
    {

        /// <summary>
        /// A simple function that takes a string and does a ToUpper
        /// </summary>
        /// <param name="input"></param>
        /// <param name="context"></param>
        /// <returns></returns>
        public string FunctionHandler(string input, ILambdaContext context)
        {
            string data = input.ToLower().Replace(" ", "");
            char[] ca = data.ToCharArray();
            Array.Reverse(ca);
            string reverse = new String(ca);
            bool is_palindrome = data.Equals(reverse);
            string message = is_palindrome ?
                String.Format("'{0}' is a palindrome!", input) :
                String.Format("'{0}' is not a palindrome!", input);
            return message;
        }
    }
}

