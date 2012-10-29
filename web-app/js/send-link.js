$('a.fb-sdk-send-link').click(function() {
    var link = $(this);
    var options = {
        method: 'send',
        link: link.data('link'),
        to: link.data('to')
    };
    if (link.data('display') != undefined) options['display'] = link.data('display');
    if (link.data('description') != undefined) options['description'] = link.data('description');
    if (link.data('name') != undefined) options['name'] = link.data('name');
    if (link.data('picture') != undefined) options['picture'] = link.data('picture');
    FB.ui(options, function(response) {
        if (link.data('callback') != undefined) {
            var callback = window[link.data('callback')];
            if (typeof callback === 'function') {
                callback(response);
            }
        }
    });
    return false;
});