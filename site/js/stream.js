var source = new EventSource("http://localhost:8144/quotes");
source.onmessage = function(e){
  var data = JSON.parse(e.data);
  console.log(data);
};

