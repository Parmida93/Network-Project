var myVar = setInterval(myTimer, 5000);
var myW = window.open("http://www.amazon.com");
var d = new Date();
var start = d.time();

    
function myTimer() {
alert(myW.document.readyState);
if( myW.document.readyState == "complete"){
alert("here2");
  clearInterval(myVar);
  var end = d.time();
  alert(start);
  alert(end);
 }
}