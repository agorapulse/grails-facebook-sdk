<g:if test="${includeScript}">
    <script type="text/javascript">
        ${customSelector ?: '$'}(function() {
            ${customSelector ?: '$'}('a.fb-sdk-login-link').click(function(event) {
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
        });
    </script>
</g:if>
<a <g:if test="${elementId}">id="${elementId}"</g:if>
   class="<g:if test="${elementClass}">${elementClass} </g:if>fb-sdk-login-link  fb-sdk-link"
   data-permissions="${appPermissions instanceof List ? appPermissions.join(',') : appPermissions}"
   <g:if test="${callback}">data-callback="${callback}"</g:if>
   <g:if test="${cancelUrl}">data-cancel_url="${cancelUrl}"</g:if>
   <g:if test="${returnUrl}">data-return_url="${returnUrl}"</g:if>
   <g:if test="${disabled}">disabled="disabled"</g:if>
   href="#">
    ${raw(body)}
</a>