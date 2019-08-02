require 'json'

def lambda_handler(event:, context:)
    input = event['word']
    data = input.downcase.gsub(' ', '')
    reverse = data.reverse
    is_palindrome = data == reverse
    message = is_palindrome ? "'#{input}' is a palindrome!" : "'#{input}' is not a palindrome!"
end
