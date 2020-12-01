//content.js

$('*:not(:has(*:not(:empty)))').each(function(index) {
	console.log($(this).text());
	var elem = $(this);
	var t = $(this).text();
	var URL = 'http://127.0.0.1:8000/query?s=' + t;
	$.get(URL,function(data,status,xhr) {
		console.log(data);
		var misinformationType = JSON.parse(xhr.responseText);
		if(misinformationType && misinformationType["items"][0]) {
		switch(misinformationType["items"][0].type) {
			case 0:
				elem.addClass("marked");
				break;
			case 1:
				elem.addClass("marked");
				break;
			case 2:
				elem.addClass("marked");
				elem.html("<mark>" + elem.html() + "</mark>");
				break;
			case 3:
				elem.addClass("marked");
				break;
			case 4:
				elem.addClass("marked");
				break;
			case 5:
				elem.addClass("marked");
				break;
			default:
				elem.addClass("marked");
				break;
		}
	}});
});

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