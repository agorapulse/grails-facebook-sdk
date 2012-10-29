$('a.fb-sdk-publish-link').click(function() {
    var link = $(this);
    var options = {
        method: 'feed'
    };
    if (link.data('caption') != undefined) options['caption'] = link.data('caption');
    if (link.data('display') != undefined) options['display'] = link.data('display');
    if (link.data('description') != undefined) options['description'] = link.data('description');
    if (link.data('link') != undefined) options['link'] = link.data('link');
    if (link.data('name') != undefined) options['name'] = link.data('name');
    if (link.data('picture') != undefined) options['picture'] = link.data('picture');
    if (link.data('source') != undefined) options['source'] = link.data('source');
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