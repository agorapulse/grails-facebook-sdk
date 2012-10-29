$('a.fb-sdk-logout-link').click(function() {
    var link = $(this);
    link.attr('disabled', 'disabled');
    FB.getLoginStatus(function(response) {
        if (response.authResponse) {
            FB.logout(function(response) {
                if (link.data('callback') != undefined) {
                    var callback = window[link.data('callback')];
                    if (typeof fn === 'function') {
                        callback(response);
                    }
                } else if (link.data('next_url')) {
                    window.location.href = link.data('next_url');
                } else {
                    window.location.reload();
                }
            });
        } else {
            if (link.data('callback') != undefined) {
                var callback = window[link.data('callback')];
                if (typeof callback === 'function') {
                    callback(response);
                }
            } else if (link.data('next_url')) {
                window.location.href = link.data('next_url');
            } else {
                window.location.reload();
            }
        }
    });
    return false;
});

