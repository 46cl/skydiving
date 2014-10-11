$(function() {
    var colorThief = new ColorThief();
    var image = new Image();

    /*Choix du template*/
    //templateNb = Math.ceil(Math.random()*5);
    templateNb = 6;
    $(".content").addClass("template"+templateNb);
    $(".backgroundImage").addClass("template"+templateNb);

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
            colorDef = complementaryColor
   
        }else{
            colorDef = highSColorHex;
        }

        colorDefComp = $c.complement(colorDef);


        /*Application du template*/
        if (templateNb == 1 ){
            $(".controls .background").css("background-color", "#000")
            $(".controls .background2").css("background-color", "#000")
            $(".content").css("color", "#FFF")
        } else if (templateNb == 2 ){
            $(".controls .background").css("background-color", colorDef);
            $(".controls .background2").css("background-color", colorDef);
            $("blockquote p").css("background-color", colorDef);
            $("cite").css("color", colorDef);
        } else if (templateNb == 3 ){
            $(".controls .background").css("background-color", colorDefComp);
            $(".controls .background2").css("background-color", colorDefComp);
            $(".content").css("color", colorDefComp);
        } else if (templateNb == 4 ){
            $(".controls .background").css("background-color", colorDef);
            $(".controls .background2").css("background-color", colorDef);
            $(".content").css("color", colorDef);
        }else if (templateNb == 5 ){
            $(".controls .background").css("background-color", colorDefComp);
            $(".controls .background2").css("background-color", colorDefComp);
            $(".bloc1").css("background-color", colorDefComp);
        }
        else if (templateNb == 6 ){
            var colors = new Array("#FFE504","#FF8200","#F70073","#4AFF7E");
            colorRand = Math.ceil(Math.random()*4);
            $(".controls .background").css("background-color", colors[colorRand]);
            $(".controls .background2").css("background-color", colors[colorRand]);
            $(".content").css("color", colors[colorRand]);
        }


        /*Animation du font*/

//        $(".content").fadeOut();
        $(".backgroundImage").css("background-image", "url("+image.src+")")
//        $(".content").fadeIn();

    }

    image.src = "fond6.jpg";
    
    /*Placement du bloc*/
    function onresize() {
        var marginTop = ($(".content").outerHeight() -  $(".bloc1").outerHeight())/2;
        $(".content").css("padding-top", marginTop);
    }
    $( window ).resize(onresize);
    onresize();
    
    /*Logs*/
    var controlsOpen = false;
    $( ".showlist" ).click(function() {
      if(controlsOpen){
        $( ".controls" ).animate({
            top: "100%",
            marginTop:"-26px",
            height: "26px"
          }, 400, function() {
            controlsOpen = false;
          });
      }else{
          $( ".controls" ).animate({
            top: 0,
            marginTop:0,
            height: "100%"
          }, 400, function() {
            controlsOpen = true;
          });
      }
    });
    


});
