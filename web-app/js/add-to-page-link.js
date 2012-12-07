$('a.fb-sdk-add-to-page-link').click(function() {
    var link = $(this);
    var options = {
        method: 'pagetab'
    };
    if (link.data('display') != undefined) options['display'] = link.data('display');
    if (link.data('redirect_uri') != undefined) options['redirect_uri'] = link.data('redirect_uri');
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