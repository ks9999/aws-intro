import json

def lambda_handler(event, context):
    input = event["word"]
    data = input.lower().replace(' ', '')
    reverse = data[::-1]
    is_palindrome = data == reverse
    response = "'{0}' is a palindrome!".format(input) if is_palindrome else "'{0}' is not a palindrome".format(input)
    return response

