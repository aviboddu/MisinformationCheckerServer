<h1>Design Document</h1>
This project initially had three possible goals:
<ul>
	<li>A good outcome: Creating a website which will take in text, find misinformation based on data from Politifact and highlight it.</li>
	<li>A better outcome: Creating a Chrome extension to take in text from web pages, then processing it the same way.</li>
	<li>A best outcome to find similar statements to misinformation, not just identical ones</li>
</ul>
At the end, we managed to complete the good and part of the better outcome.

<h3>The code for the good outcome</h3>
When first creating the program, we had 3 different problems to deal with. 
<ol>
<li><b>Obtaining the necessary data from Politifact.</b> This was done using a Python webscraper, which would process each webpage in Politifact's most recent articles pages. This would be converted into a .csv format to be processed by our server. Since the webpage we were scraping from was somewhat complicated, we used BeautifulSoup4 to process the HTML, and make it easier to find things. Fortunately, Politifact's frequent use of div made things easy for us. The statements were also processed in order to make it easier to be read by the server.</li>
	
<li><b>Creating the server.</b> The server would have to store each misinformation statement in an easily searchable way. For this we used a HashMap. In order to speed up the server, we used multithreading, and in order to deal with concurrency issues, we used a ConcurrentHashMap instead (from Kevin's suggestion). Since we were using a HashMap, we had to create a class which would store both the URL and Misinformation Type (so we could have one object associated with each key). We also used the Sun Http package in order to easily send information over the internet and create a fast server with multithreading.</li>
	
<li><b>Creating the client.</b> The client was an HTML file on the server. This client had to take in text using a form, split the text up properly, send this text to the server, and produce the edited text based on the server's response. The text was split into sentences (on the client and server side) using RegEx. The text was sent to the server using the URL, which would be in this format: [server].com/query?s=[URI-encoded-text]. The text was then sent using synchronous xhttp requests. The server would send the relevant data (the url and type of misinformation) in JSON, which would be parsed and used. Splitting up the sentences were difficult as we had to ensure we split the same way on both the server and client side. The challenge was that the client's sentences may be surrounded by other, unknown text. In order to minimize this issue, we removed certain characters when searching and creating the HashMap, such as % and ".</li>
</ol>	
<h3>The code for the better outcome</h3>

The Chrome extension was definitely quite challenging, and as a result, while the extension is working, <i>it is not ready to be used normally, due to severe memory performance issues.</i> </br>


The first new challenge of the Chrome extension was being able to take in all the text from a page. While this might seem easy at first, the issue is that modern websites often use a technique called AJAX in order to send and receive data from servers which are not necessarily in the initial HTML. In fact, we tried to deal with this issue using regular JavaScript, but in the end decided to re-do all the extension code using jQuery in order to handle this issue easily. 

The second problem was not causing the broswer to hang. While initially we used synchronous xhttp requests to get data from the server, when done with a Chrome extension, this would cause the website to hang. Since our server is running on just one dyno on Heroku, it can take a long time for each request to be handled. This would not work from a usability perspective. No client is going to wait 10-15 seconds just for the loaded page to be processed. Therefore, we decided to use AJAX asynchronously. This meant that the program would allow the website to be responsive while processing the text. The server could also deal with multiple requests due to its multithreading. 

The third issue was dealing with CORS. Most websites have a CORS check, ensuring that data can be sent between domains. This is done for security, but as a result, we had to ensure our server was adding the correct Access-Control-Allow-Origin header. This took time since we had to learn what CORS was, identify the specific issue, and figure out how to solve it. At the end, we just added a CORS header to every message sent by our server saying it could be sent anywhere.

However, we did run into issues with memory, as stated. These issues would likely require significant changes to the code (we believe). There were also smaller issues to be dealt with (especially because none of us had any experience in JavaScript or jQuery until now), but these were relatively simple and are addressed in the comments. At the same time, <b>there are still problems with the Chrome extension when accessing real websites.</b> Unfortunately, we do not have enough experience or knowledge in web development to fully understand why we are running into issues. (It's likely related to CORS and other techniques real web developers use). However, in order to test the extension for now, we created a sample.html file, which is in the Chrome extension folder. Steps to run and test this are in the README.