//Content.js
chrome.runtime.onMessage.addListener(gotMessage);
function gotMessage(message,sender,sendresponse)
{
	let paragraphs = document.body.querySelectorAll("*");//This will change to cover more types of text and AJAX requests as well.
	for(elt of paragraphs)
	{	
		if(elt) {
			sendAndReceiveData(elt);
		}
	}
}

function sendAndReceiveData(elt) {
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
			changeHTML(this,elt);
		}
	}
	//This URL will change, right now it's just the local host (This URL change will solve the CORS issue, and will happen when the server is no longer local)
	URL = 'http://127.0.0.1:8000/query?s=' + elt;
	xhttp.open("GET",URL,true);
	xhttp.send();
}

function changeHTML(xhttp,elt) {
	misinformationType = JSON.parse(xhttp.responseText);
	if(misinformationType && misinformationType["items"][0]) {
		switch(misinformationType["items"][0].type) {
			case 0:
				elt.style['color'] = '#FF0000';
				break;
			case 1:
				elt.style['color'] = '#FFA500';
				break;
			case 2:
				elt.style['color'] = '#FFFF00';
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