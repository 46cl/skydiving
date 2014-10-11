$(function() {
    var colorThief = new ColorThief();
    var image = new Image();

    image.onload = function() {
        dominentColor=colorThief.getColor(image);    
    }

    image.src = "fond.jpg";

    $(".controls").css("",)
    
});