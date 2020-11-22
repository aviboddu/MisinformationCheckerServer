//Content.js
chrome.runtime.onMessage.addListener(gotMessage);
function gotMessage(message,sender,sendresponse)
{
	console.log(message.txt);
	document.ge
	let paragraphs = document.getElementsByTagName("p");//This will change to cover more types of text and AJAX requests as well.
	for(elt of paragraphs)
	{
		//This URL will change, right now it's just the local host
		URL = 'http://127.0.0.1:8000/query?s=' + elt
		misinformationType = JSON.parse(fetch(URL));
		if(!(misinformationType === null)) {
			switch(misinformationType.items.type) {
				case 0:
					elt.style['color'] = '#FF0000';
					break;
				case 1:
					elt.style['color'] = '#FFA500';
					break;
				case 2:
					elt.style['color'] = '#FFF700';
					break;
				case 3:
					elt.style['color'] = '#FF009A';
					break;
				case 4:
					elt.style['color'] = '#00FFDE';
					break;
				case 5:
					elt.style['color'] = '#00FF00';
					break;
				default:
					break;
			}
		}
	}
}