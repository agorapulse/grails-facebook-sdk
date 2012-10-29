$("a.facebook-sdk-add-to-page").click(function() {
    var link = $(this);
    var options = {

    };
    //if (link.data('data') != undefined) options['data'] = link.data('data');
    FB.ui(options, function(response) {
        if (link.data('callback') != undefined) {
            var callback = window[link.data('callback')];
            if (typeof fn === 'function') {
                callback(response);
            }
        }
    });
    return false;
});