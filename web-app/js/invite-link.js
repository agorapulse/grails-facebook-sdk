$('a.fb-sdk-invite-link').click(function() {
    var link = $(this);
    var options = {
        method: 'apprequests',
        message: link.data('message')
    };
    if (link.data('data') != undefined) options['data'] = link.data('data');
    if (link.data('display') != undefined) options['display'] = link.data('display');
    if (link.data('exclude_ids') != undefined) options['exclude_ids'] = link.data('exclude_ids');
    if (link.data('filters') != undefined) options['filters'] = link.data('filters');
    if (link.data('max_recipients') != undefined) options['max_recipients'] = link.data('max_recipients');
    if (link.data('title') != undefined) options['title'] = link.data('title');
    if (link.data('to') != undefined) options['to'] = link.data('to');
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