$(function () {

    //var source = new EventSource("http://localhost:8144/quotes")
    var source = new EventSource("http://mayocatdev.radchaps.com/quotes"),
        queue = [],
        isPaused = false;

    $("#pause").click(function(){
        isPaused = !isPaused;
    })

    function processQueue() {
        if (queue.length > 0 && !isPaused) {
            var data = queue.shift();
            randomTemplate(data.picture, function(){
                $("blockquote p").text(data.content);
                $("cite").text(data.author);
            });
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

    window.setInterval(processQueue, 10000)

});

