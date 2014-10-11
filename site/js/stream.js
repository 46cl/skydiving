$(function() {

  var source = new EventSource("http://mayocatdev.radchaps.com/quotes"),
      queue = [];

  $(".content").hide();

  function processQueue() {
    if (queue.length > 0) {
      var data = queue.shift();
      $(".content").fadeOut();
      $(".content").css("background-image", "url(" + data.picture + ")");
      $(".content").fadeIn();

      $("blockquote p").text(data.content);

      $("cite").text(data.author);

      templateNb = Math.ceil(Math.random()*5);
      $(".content").addClass("template"+templateNb).show();
    }
  }

  source.onmessage = function(e){
    var data = JSON.parse(e.data);
    (function(data) {
      var image = new Image();
      image.onload = function() {
        queue.push(data);
      };
      image.src = data.picture;
    })(data);
  };

  window.setInterval(processQueue, 5000)

});

