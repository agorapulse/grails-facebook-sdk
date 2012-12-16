$('a.fb-sdk-login-link').click(function(event) {
    var link = $(this);
    link.attr('disabled', 'disabled');
    FB.login(function(response) {
        if (link.data('callback') != undefined) {
            var callback = window[link.data('callback')];
            if (typeof callback === 'function') {
                callback(response, event.target);
            }
        } else if (response.authResponse) {
            // user is logged
            if (link.data('return_url')) {
                window.location.href = link.data('return_url');
            } else {
                window.location.reload();
            }
        } else if (link.data('cancel_url')) {
            window.location.href = link.data('cancel_url');
        } else {
            link.removeAttr('disabled');
        }
    }, {
        scope: link.data('permissions')
    });
    return false;
});

