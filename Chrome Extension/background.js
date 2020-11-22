//background.js
console.log("Background running");
chrome.tabs.onUpdated.addListener( function (tabId, changeInfo, tab) {//This will change to include AJAX requests
  if (changeInfo.status == 'complete' && tab.active) {

    let msg = {
		txt : "Hello"
	}
	chrome.tabs.sendMessage(tab.id,msg);

  }
})

