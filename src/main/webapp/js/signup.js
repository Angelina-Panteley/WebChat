/**
 * Created by Lenovo on 25.05.2015.
 */
'use strict';

var User = function(name,pass,what)
{
    return {
        login:name,
        password:pass,
        doWhat:what
    };
};
var mainUrl = 'index';
function run() {
   document.addEventListener('click', delegateEvent);
}

function delegateEvent(evtObj) {
    if(evtObj.type === 'click') {
        if(evtObj.target.id === 'signInBut')
            signIn();
        if(evtObj.target.id === 'signUpBut')
            signUp();

    }
}
function signIn() {
    document.getElementById("divUnderPassword").innerHTML="";
    var login = document.getElementById("log").value;
    var password = document.getElementById('pass').value;
    var user = User(login, password, 'signin');
    toPost(login,password,'signin',user);
}
function signUp() {
    document.getElementById("divUnderPassword").innerHTML="";
    var login = document.getElementById('log').value;
    var password = document.getElementById('pass').value;
    var user = User(login, password, 'signup');
    toPost(login,password,'signup',user);
}
function toPost(login,password,action,user)
{
    if(login === '' || password === '') {
        document.getElementById("divUnderPassword").innerHTML = '<h6>Field is empty</h6>';
    }
    else {
        post(mainUrl, JSON.stringify(user), function (responseText) {
            console.assert(responseText != null);

            var response = JSON.parse(responseText);
            confirmSigning(response.answer, login, action);

            continueWith && continueWith();
        });
    }
}
function confirmSigning(answer, login, action) {
    if(answer === 'true') {
        if(typeof(Storage) == "undefined") {
            alert('localStorage is not accessible');
            return;
        }
        document.getElementById("divUnderPassword").innerHTML = '';
        localStorage.setItem("login", login);
        window.location.href = "chat.html";
    }
    else if(answer === 'false'){
        if(action === 'signin') {
            document.getElementById("divUnderPassword").innerHTML = '<p color="red">Wrong password or no such user!</p>';
        }
        if(action === 'signup') {
            document.getElementById("divUnderPassword").innerHTML = '<p color="red">Such login already exists!</p>';
        }
    }
}


function defaultErrorHandler(message) {
    console.error(message);
}

function post(url, data, continueWith, continueWithError) {
    ajax('POST', url, data, continueWith, continueWithError);
}

function isError(text) {
    if(text == "")
        return false;

    try {
        var obj = JSON.parse(text);
    } catch(ex) {
        return true;
    }

    return !!obj.error;
}

function ajax(method, url, data, continueWith, continueWithError) {
    var xhr = new XMLHttpRequest();

    continueWithError = continueWithError || defaultErrorHandler;
    xhr.open(method || 'GET', url, true);

    xhr.onload = function () {
        if (xhr.readyState !== 4)
            return;

        if(xhr.status != 200) {
            continueWithError('Error on the server side, response ' + xhr.status);
            return;
        }

        if(isError(xhr.responseText)) {
            continueWithError('Error on the server side, response ' + xhr.responseText);
            return;
        }
        continueWith(xhr.responseText);
    };

    xhr.ontimeout = function () {
        continueWithError('Server timed out !');
    };

    xhr.onerror = function () {
        var errMsg = 'Server connection error !\n'+
            '\n' +
            'Check if \n'+
            '- server is active\n'+
            '- server sends header "Access-Control-Allow-Origin:*"';

        continueWithError(errMsg);
    };

    xhr.send(data);
}