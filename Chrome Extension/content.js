//content.js

$('*').each(function(index) {
	var t = $(this).html();
	var URL = 'http://127.0.0.1:8000/query?s=' + t;
	$.get(URL,function(data,status,xhr) {
		var misinformationType = JSON.parse(xhr.responseText);
		if(misinformationType && misinformationType["items"][0]) {
		switch(misinformationType["items"][0].type) {
			case 0:
				$(this).addClass("marked");
				break;
			case 1:
				$(this).addClass("marked");
				break;
			case 2:
				$(this).addClass("marked");
				$(this).prepend(`<mark>`);
				$(this).append(`</mark>`);
				break;
			case 3:
				$(this).addClass("marked");
				break;
			case 4:
				$(this).addClass("marked");
				break;
			case 5:
				$(this).addClass("marked");
				break;
			default:
				$(this).addClass("marked");
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