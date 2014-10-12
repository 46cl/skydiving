$(function () {

    //var source = new EventSource("http://localhost:8144/quotes")
    var source = new EventSource("http://mayocatdev.radchaps.com/quotes"),
        logs = new EventSource("http://localhost:8144/logs"),
        queue = [],
        isPaused = false;

    $("#pause").click(function(){
        isPaused = !isPaused;
    })

    function processQueue() {
        if (queue.length > 0 && !isPaused) {
            var data = queue.shift();
            (function(data){
                randomTemplate(data.picture, function(){
                    $("blockquote p").text(data.content);
                    $("cite").text(data.author);
                });
            })(data);
        }
    }

    source.onmessage = function (e) {
        var data = JSON.parse(e.data);
        (function (data) {
            var image = new Image();
            image.onload = function () {
                queue.push(data);
            };
            image.src = data.picture;
        })(data);
    };

    logs.onmessage = function(e) {
        var log = e.data;
        $("#logs").prepend($("<br />"));
        $("#logs").prepend($("<span/>").text(log));
        if ($("#logs span").length > 200) {
            $("#logs span").last().remove();
        }
    }

    window.setInterval(processQueue, 10000)

});

