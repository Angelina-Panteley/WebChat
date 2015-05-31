'use strict';
var UserName = "New User";
var MyID = -1;

var theMessage = function(text,UserName)
{
	return {
		description: text,
		id: appState.messageList.length + 1,
		user: UserName
	};
};

var appState = {
	mainUrl : 'chat',
	messageList: [],
	token : 'TN11EN',
	user: localStorage.getItem('login')
};
var messagesWaiting = false;
function getMessages(){
	if(!messagesWaiting){
		messagesWaiting = true;
		var xmlhttp = new XMLHttpRequest();
		xmlhttp.onreadystatechange=function(){
			if (xmlhttp.readyState==4 && xmlhttp.status==200) {
				messagesWaiting = false;
				console.assert(xmlhttp.responseText != null);
				var response = JSON.parse(xmlhttp.responseText);
				var messages = response.messages;
				var items = document.getElementsByClassName('items')[0];
				for (var i = 0; i < messages.length; ++i)
				{
					var message = messages[i];

					if (i < appState.messageList.length)
					{
						var j;
						for (j = 0; j < items.children.length; ++j)
						{
							if (items.children[j].attributes['data-task-id'].value == appState.messageList[i].id)
								break;
						}
						if (message.user == "" && appState.messageList[i].user != "")//deleted
						{
							appState.messageList[i] = message;
							items.removeChild(items.children[j]);
							//items.children.deleteRow(j);
						}
						else if (message.description != appState.messageList[i].description)//edited
						{
							appState.messageList[i].description = message.description;
							items.children[j].innerHTML = '<table><tr><td style="width:25%"><b>' + message.user + ':</b></td><td width=70%  style="word-wrap: break-word" >' + message.description + '</td><td style="padding-left: 5px">                                            <img src="http://www.defaulticon.com/sites/default/files/styles/icon-front-page-32x32-preview/public/field/image/edit.png?itok=nb2eY85A" onClick="editMessage()" style="cursor: pointer"></img>                      <img src="http://www.defaulticon.com/sites/default/files/styles/icon-front-page-32x32-preview/public/field/image/eraser.png?itok=ohy0hMWI" onClick="deleteMessage()" style="cursor: pointer"</img>                      </td></tr></table>';
							items.children[j].setAttribute('data-task-id', message.id);
						}
					}
					else
					{
						if (message.user != "")
						{
							addMailInternal(message);
						}
						appState.messageList.push(message);
					}
				}
				appState.token = response.token;
			}
		}
		xmlhttp.open("GET",  appState.mainUrl + '?token='+appState.token, true);
		xmlhttp.send();
	}
}
setInterval(getMessages, 1000);
function run()
{
	document.addEventListener('click', delegateEvent);
	var image = document.getElementById("myImage");
	image.src = "images/serveron.png";
	//window.setInterval(function(){  }, 1000);
	restore();

}

function delegateEvent(evtObj)
{
	if(evtObj.type === 'click'
		&& evtObj.target.classList.contains('button'))
		prevPage();
	if(evtObj.type === 'click'
		&& evtObj.target.classList.contains('addMailButton'))
		onAddButtonClick();
}

function prevPage()
{
	window.location.href="index.html";
}

function onAddButtonClick()
{
	var mailText = document.getElementById('mailText');
	if(mailText.value == '')
		return;
	if(MyID >= 0)
	{
		addChangedMessage(MyID, mailText.value,appState.user);
		MyID = -1;
	}
	else
	{
		var newMessage = theMessage(mailText.value,appState.user);
		addMail(newMessage);
	}
	mailText.value = '';
}

function addMail(message)
{
	post(appState.mainUrl, JSON.stringify(message), function(){ restore(); });
}

function addChangedMessage(id, text,user)
{
	var m = theMessage(text,user);
	for (var i = 0; i < appState.messageList.length; ++i)
	{
		if (appState.messageList[i].id == id)
		{
			m.id = appState.messageList[i].id;
			m.user = appState.messageList[i].user;
			break;
		}
	}
	var url = appState.mainUrl + '?id=' + id;
	put(url, JSON.stringify(m), function(){ restore(); });
}

function addMailInternal(message)
{
	if (message.description != "")
	{
		var item = createItem(message);
		var items = document.getElementsByClassName('items')[0];
		items.appendChild(item);
		items.scrollTop = items.scrollHeight;
	}
}

function createItem(mes)
{
	var message = document.createElement('div');
	message.innerHTML = '<table><tr><td style="width:25%"><b>' + mes.user + ':</b></td><td width=70%  style="word-wrap: break-word">' + mes.description + '</td>                      <td style="padding-left: 5px">                                            <img src="http://www.defaulticon.com/sites/default/files/styles/icon-front-page-32x32-preview/public/field/image/edit.png?itok=nb2eY85A" onClick="editMessage()" style="cursor: pointer"></img>                      <img src="http://www.defaulticon.com/sites/default/files/styles/icon-front-page-32x32-preview/public/field/image/eraser.png?itok=ohy0hMWI" onClick="deleteMessage()" style="cursor: pointer"</img>                      </td></tr></table>';
	message = message.firstChild;
	message.setAttribute('data-task-id', mes.id);
	return message;
}

function deleteMessage()
{
	var toDelete = event.target;
	toDelete = toDelete.parentNode.parentNode.parentNode.parentNode;
	var id = toDelete.attributes['data-task-id'].value;
	var url = appState.mainUrl + '?id=' + id;
	erase(url, function(){ restore(); });
}

function editMessage()
{
	appState.isChanged = false;
	var toEdit = event.target;
	toEdit = toEdit.parentNode;
	toEdit = toEdit.parentNode.parentNode.parentNode;
	MyID = toEdit.attributes['data-task-id'].value;
	for (var i = 0; i < appState.messageList.length; i++)
	{
		if (appState.messageList[i].id == MyID)
		{
			document.getElementById('mailText').value = appState.messageList[i].description;
			MyID = i+1;
			break;
		}
	}
}

function restore(continueWith)
{
	//messagesWaiting = true;
	var url = appState.mainUrl + '?token=TN11EN';
	get(url, function(responseText)
	{
		//messagesWaiting = true;
		console.assert(responseText != null);
		var response = JSON.parse(responseText);
		var messages = response.messages;
		var items = document.getElementsByClassName('items')[0];
		for (var i = 0; i < messages.length; ++i)
		{
			var message = messages[i];
			
			if (i < appState.messageList.length)
			{
				var j;
				for (j = 0; j < items.children.length; ++j)
				{
					if (items.children[j].attributes['data-task-id'].value == appState.messageList[i].id)
						break;
				}
				if (message.id != appState.messageList[i].id)//deleted
				{
					appState.messageList[i] = message;
					items.removeChild(items.children[j]);
					//items.children.deleteRow(j);
				}
				else if (message.description != appState.messageList[i].description)//edited
				{
					appState.messageList[i].description = message.description;
					items.children[j].innerHTML = '<table><tr><td style="width:25%"><b>' + message.user + ':</b></td><td width=70%  style="word-wrap: break-word" >' + message.description + '</td><td style="padding-left: 5px">                                            <img src="http://www.defaulticon.com/sites/default/files/styles/icon-front-page-32x32-preview/public/field/image/edit.png?itok=nb2eY85A" onClick="editMessage()" style="cursor: pointer"></img>                      <img src="http://www.defaulticon.com/sites/default/files/styles/icon-front-page-32x32-preview/public/field/image/eraser.png?itok=ohy0hMWI" onClick="deleteMessage()" style="cursor: pointer"</img>                      </td></tr></table>';	
					items.children[j].setAttribute('data-task-id', message.id);
				}
			}
			else
			{
				if (message.user != "")
				{
					addMailInternal(message);
				}
				appState.messageList.push(message);
			}
		}
		appState.token = response.token;
		continueWith && continueWith();
	});
}
function defaultErrorHandler(message)
{
	console.error(message);
}

function get(url, continueWith, continueWithError)
{
	ajax('GET', url, null, continueWith, continueWithError);
}

function post(url, data, continueWith, continueWithError)
{
	ajax('POST', url, data, continueWith, continueWithError);
}

function put(url, data, continueWith, continueWithError)
{
	ajax('PUT', url, data, continueWith, continueWithError);
}
function erase(url, continueWith, continueWithError)
{
	ajax('DELETE', url,null, continueWith, continueWithError);
}
function isError(text)
{
	if (text == "")
		return false;
	try
	{
		var obj = JSON.parse(text);
	}
	catch (ex)
	{
		return true;
	}
	return !!obj.error;
}

function ajax(method, url, data, continueWith, continueWithError)
{
	var xhr = new XMLHttpRequest();
	var image=document.getElementById('myImage');
	continueWithError = continueWithError || defaultErrorHandler;
	xhr.open(method || 'GET', url, true);
	var flag = 0;
	xhr.onload = function()
	{
		if (xhr.readyState !== 4)
		{
			++flag;
			return;
		}
		if (xhr.status != 200)
		{
			continueWithError('Error on the server side, response ' + xhr.status);
			++flag;
			return;
		}

		if (isError(xhr.responseText))
		{
			continueWithError('Error on the server side, response ' + xhr.responseText);
			++flag;
			return;
		}
		continueWith(xhr.responseText);
	};
	
	xhr.ontimeout = function()
	{
		++flag;
		continueWithError('Server timed out !');
	}
	
	xhr.onerror = function(e)
	{
		++flag;
		var errMsg = 'Server connection error !\n' + '\n' + 'Check if \n' + '- server is active\n' + '- server sends header "Access-Control-Allow-Origin:*"';
		continueWithError(errMsg);
	};

	xhr.send(data);
	if (flag == 0)
	{
		image.src = "images/serveron.png";
	}
	else
	{
		image.src = "images/serveroff.png";
	}
}

window.onerror = function(err) {}