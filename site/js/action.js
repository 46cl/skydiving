$(function() {
    var colorThief = new ColorThief();
    var image = new Image();

    image.onload = function() {
        /*var dominentColor = colorThief.getColor(image);        
        dominentColor = $c.rgb2hex(dominentColor[0], dominentColor[1], dominentColor[2]);
        var complementaryColor= $c.complement(dominentColor);
        var dominentColorHSV = $c.hex2hsv(dominentColor).a;

        if(dominentColorHSV[2]<50){
            controlsBarColor = dominentColor
            alert("dominent");
        }else{
            controlsBarColor = complementaryColor
            alert("complementary");
        }

        */

        var palette = colorThief.getPalette(image, 8);
        var highS = 0;
        var highSPos = 0;
        for(i=0; i<8; i++){
            var color = $c.rgb2hex(palette[i][0], palette[i][1], palette[i][2]);
            var colorHSV = $c.hex2hsv(color).a;
            if(colorHSV[2]>highS){
              highSPos=i
            }
        }

        if (palette[i])


        $(".controls").css("background-color", color)
        $(".content").css("volor", color)

    }

    image.src = "fond2.jpg";
    $(".content").css("background-image", "url("+image.src+")")

    
});