$(function() {
    var colorThief = new ColorThief();
    var image = new Image();

    /*Choix du template*/
    //templateNb = Math.ceil(Math.random()*5);
    templateNb = 1;
    $(".content").addClass("template"+templateNb);

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
            //alert("fade");
            var dominentColor = colorThief.getColor(image);        
            dominentColor = $c.rgb2hex(dominentColor[0], dominentColor[1], dominentColor[2]);
            var complementaryColor= $c.complement(dominentColor);

            if(dominentColorHSV[1]>50){
                colorDef = dominentColor
                //alert("dominent");
            }else{
                colorDef = complementaryColor
                //alert("complementary");
            }
        }else{
            colorDef = highSColorHex;
        }

        colorDefComp = $c.complement(colorDef);


        /*Application du template*/
        if (templateNb == 1 ){
            $(".controls").css("background-color", "#000")
            $(".content").css("color", "#FFF")
            $("img").css("filter","url('data:image/svg+xml;utf8,<svg xmlns=\'http://www.w3.org/2000/svg\'><filter id=\'grayscale\'><feColorMatrix type=\'matrix\' values=\'0.3333 0.3333 0.3333 0 0 0.3333 0.3333 0.3333 0 0 0.3333 0.3333 0.3333 0 0 0 0 0 1 0\'/></filter></svg>#grayscale')");
            $("img").css("filter","gray");  
            $("img").css("-webkit-filter", "grayscale(100%)");
        } else if (templateNb == 2 ){
            $(".controls").css("background-color", colorDef)
            $("blockquote p").css("background-color", colorDef)
            $("cite").css("color", colorDef)
        } else if (templateNb == 3 ){
            $(".controls").css("background-color", colorDefComp)
            $(".content").css("color", colorDefComp)
        }


        /*Animation du font*/
        $(".content").fadeOut();
        $(".content").css("background-image", "url("+image.src+")")
        $(".content").fadeIn();
    }

    image.src = "fond2.jpg";
    

    
});