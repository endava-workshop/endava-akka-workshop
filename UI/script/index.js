
	var started = false;
	var hardcodedActions = [
		"Most recently browsed pages: <br/>http://en.wikipedia.org/wiki/Ukraine<br/>http://en.wikipedia.org/wiki/Internal_market<br/>.....................<br/>",
		"Total browsed web pages: 92.328.409<br/>Average browsing speed: 287 pages per second<br/>....................."
	];
	var actionIndex = 0;
	function startCrawling() {
		started = true;
		document.getElementById("startCrawlingButton").style.display="none";
		document.getElementById("pauseCrawlingButton").style.display="block";
		document.getElementById("resumeCrawlingButton").style.display="none";
		crawl();
	}
	function crawl() {
		if(! started) {
			return;
		}
		window.setTimeout(function() {
			var statusFrame = document.getElementById("crawlerFeedbackDiv");
			statusFrame.innerHTML = hardcodedActions[actionIndex];
			if(actionIndex < hardcodedActions.length - 1) {
				actionIndex ++;
			} else {
				actionIndex = 0;
			}
			crawl();
		}, 300);
	}
	function pauseCrawling() {
		started = false;
		document.getElementById("pauseCrawlingButton").style.display="none";
		document.getElementById("resumeCrawlingButton").style.display="block";
	}
