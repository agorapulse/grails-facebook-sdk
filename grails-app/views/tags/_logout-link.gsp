<g:if test="${includeScript}">
    <script type="text/javascript">
        ${customSelector ?: '$'}(function() {
            ${customSelector ?: '$'}('a.fb-sdk-logout-link').click(function() {
                var link = $(this);
                link.attr('disabled', 'disabled');
                FB.getLoginStatus(function(response) {
                    if (response.authResponse) {
                        FB.logout(function(response) {
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

        });
    </script>
</g:if>
<a <g:if test="${elementId}">id="${elementId}"</g:if>
    class="<g:if test="${elementClass}">${elementClass} </g:if>fb-sdk-logout-link fb-sdk-link"
    <g:if test="${nextUrl}">data-next_url="${nextUrl}"</g:if>
    <g:if test="${disabled}">disabled="disabled"</g:if>
    href="#">
    ${raw(body)}
</a>