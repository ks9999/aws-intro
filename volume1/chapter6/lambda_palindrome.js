exports.handler = (event, context, callback) => {
	const input = event.word.replace(/ /g, '').toLowerCase();
	const reverse = input.split('').reverse().join('');
	const is_palindrome = input == reverse;
const response = is_palindrome ? `'${event.word}' is a palindrome!` : 
`'${event.word}' is not a palindrome.`;
	callback(null, response);
}
