//content.js

$('*:not(:has(*:not(:empty)))').each(async function(index) {
	htmlText = await buildHTML(splitText($(this).text().toString()));
	$(this).html(htmlText);
});

function splitText(t) {
	matched = t.match(/([^\.!\?]+[\.!\?]+)|([^\.!\?]+$)/g);
}

async function buildHTML(textArray) {
	if(!textArray) {
		return "";
	}
	var c;
	for(c = 0; c < textArray.length; c++) {
		if(!textArray[c].includes("{")) {
			var URL = 'https://politifactmisinformation.herokuapp.com/query?s=' + encodeURI(textArray[c]);
			var misinformationType = await ajaxCall(URL);
			if(misinformationType && misinformationType["items"][0]) {
				switch(misinformationType["items"][0].type) {
					case 0:
						textArray[c] = "<mark>" + textArray[c] + "</mark>";
						break;
					case 1:
						textArray[c] = "<mark>" + textArray[c] + "</mark>";
						break;
					case 2:
						textArray[c] = "<mark>" + textArray[c] + "</mark>";
						break;
					case 3:
						textArray[c] = "<mark>" + textArray[c] + "</mark>";
						break;
					case 4:
						textArray[c] = "<mark>" + textArray[c] + "</mark>";
						break;
					case 5:
						textArray[c] = "<mark>" + textArray[c] + "</mark>";
						break;
				}
			}
		}
	}
	return textArray.join("");
}

async function ajaxCall(URL) {
	let result;
	try {
		result = await $.ajax(URL,{type:'GET'});
		return result;
	} catch (error) {
		console.error(error);
	}
}

function modify() {
	$('*').bind('DOMSubtreeModified.event1',DOMModificationHandler);
}

function DOMModificationHandler(){
    $(this).unbind('DOMSubtreeModified.event1');
    setTimeout(function(){
        modify();
        $('*').bind('DOMSubtreeModified.event1',
                                   DOMModificationHandler);
    },10);
}
$('*').bind('DOMSubtreeModified.event1',DOMModificationHandler);