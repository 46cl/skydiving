$(function () {

    // var source = new EventSource("http://mayocatdev.radchaps.com/quotes"),
    var source = new EventSource("http://localhost:8144/quotes")
    queue = [];

    $(".content").hide();

    function processQueue() {
        if (queue.length > 0) {
            var data = queue.shift();

            console.log(data.picture);
            randomTemplate(data.picture);

            $("blockquote p").text(data.content);

            $("cite").text(data.author);

            for (var i = 1; i <= 6; i++) {
                $(".content").removeClass("template" + i);
            }
            templateNb = Math.ceil(Math.random() * 5);
            $(".content").addClass("template" + templateNb).show();
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

