//background.js
console.log("Background running");
chrome.tabs.onUpdated.addListener( function (tabId, changeInfo, tab) {//Runs when the page is loaded.
  if (changeInfo.status == 'complete' && tab.active) {

    let msg = {
		txt : "Hello"
	}
	chrome.tabs.sendMessage(tab.id,msg);

  }
})

