package example;

import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Palindrome implements RequestHandler<RequestClass, ResponseClass>{   

    public ResponseClass handleRequest(RequestClass request, Context context){
	String input = request.word;
	String data = input.toLowerCase().replace(" ", "");
	StringBuilder sb = new StringBuilder(data);
	String reverse = sb.reverse().toString();
	Boolean is_palindrome = data.equals(reverse);
        String message = is_palindrome ? 
		String.format("'%s' is a palindrome!", input):
		String.format("'%s' is not a palindrome!", input);
 
        return new ResponseClass(message);
    }
}
