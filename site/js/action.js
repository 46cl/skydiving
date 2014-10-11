$(function() {
    var colorThief = new ColorThief();
    var image = new Image();

    image.onload = function() {
        var palette = colorThief.getPalette(image, 9);
        var highS = 0;
        var highSPos = 0;
        for(i=0; i<8; i++){
            var color = $c.rgb2hex(palette[i][0], palette[i][1], palette[i][2]);
            var colorHSV = $c.hex2hsv(color).a;
            if(colorHSV[1]>highS){
              highSPos=i;
              highS = colorHSV[1];
            }

        }

        var highSColorHex = $c.rgb2hex(palette[highSPos][0], palette[highSPos][1], palette[highSPos][2]);
        var highSColorHSV = $c.hex2hsv(highSColorHex).a;
        

        if(highSColorHSV[1]<60){
            alert("fade");
            var dominentColor = colorThief.getColor(image);        
            dominentColor = $c.rgb2hex(dominentColor[0], dominentColor[1], dominentColor[2]);
            var complementaryColor= $c.complement(dominentColor);
            /*var dominentColorHSV = $c.hex2hsv(dominentColor).a;*/

            if(dominentColorHSV[1]>50){
                colorDef = dominentColor
                alert("dominent");
            }else{
                colorDef = complementaryColor
                alert("complementary");
            }

        }else{
            colorDef = highSColorHex;
        }

        $(".controls").css("background-color", colorDef)
        $(".content").css("color", colorDef)

    }

    image.src = "fond2.jpg";
    $(".content").css("background-image", "url("+image.src+")")

    
});