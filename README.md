<h1>User Guide</h1>

To use the Heroku application, all you need to do is go to the URL: 
https://politifactmisinformation.herokuapp.com/

The Heroku app consists of a simple HTML user interface, where you can input a line that you want the program to check for whether it is fact or fiction. It includes a table/key that shows the different types of misinformation, as well as the text color associated with them. Currently, we can only check very specific lines that completely match the title of the page in Politifact. The interface has a bar where you can input the line, and upon checking the line, it prints out the line again with a hyperlink to the Politifact page, and changes the text color to match one of the six different states of misinformation (True, Mostly True, Half True, Mostly False, False, Pants On Fire!), which were taken from Politifact’s rating scale. 

After clicking the Submit button for one statement, the input form is cleared and you can enter another fact to submit and check, which will print out another line of the statement below the previous statement as hyperlinked and colored. 

In the case that the line is not found in the Politifact database, then it will print out a line without the colored text and without a hyperlink. 

Some example statements to test:
<ul>
	<li>The USPS backdated 100K ballots so they could be counted illegally. --> This should be marked.</li>
	<li>Video footage from Georgia shows suitcases filled with ballots pulled from under a table and illegally counted after election observers were told to leave. Says COVID-19 has killed more North Carolinians in a year than the flu has in 10 years. ---> Both of these should be marked as different sentences.</li>
	<li>A Dominion representative in Gwinnett County improperly downloaded election data to “manipulate the data.” Misinformation is important! --> One should be marked, one shouldn't</li>
</ul>

In order to test the Chrome extension:
<ol>
	<li>Download the github code and extract the chrome extension folder.</li>
	<li>In chrome's extension manager, enable developer mode.</li>
	<li>Then, click "Load unpacked" and navigate inside the chrome extension folder and click select folder.</li>
	<li>After this, make sure the extension is turned on, and open up sample.html from the same folder in chrome.</li>
	<li>You should be able to see the text automatically changing.</li>
</ol>
<i>Note: Ensure that you have gone to the heroku app first, otherwise it might take a while to boot up when using the chrome extension.</i>
