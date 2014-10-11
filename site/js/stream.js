var source = new EventSource("http://localhost:8144/quotes");
source.onmessage = function(e){
  var data = JSON.parse(e.data);
  console.log(data);

  var image = new Image();
  image.onload = function() {
     $(".content").fadeOut();
     $(".content").css("background-image", "url(" + image.src + ")");
     $(".content").fadeIn();

     $("blockquote p").text(data.content);

     $("cite").text(data.author);

      templateNb = Math.ceil(Math.random()*5);
      $(".content").addClass("template"+templateNb);

  };
  image.src = data.picture;

};

