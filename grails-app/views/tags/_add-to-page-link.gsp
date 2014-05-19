<g:if test="${includeScript}">
    <script type="text/javascript">
        ${customSelector ?: '$'}(function() {
            ${customSelector ?: '$'}('a.fb-sdk-add-to-page-link').click(function() {
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
        });
    </script>
</g:if>
<a <g:if test="${elementId}">id="${elementId}"</g:if>
   class="<g:if test="${elementClass}">${elementClass} </g:if>fb-sdk-add-to-page-link fb-sdk-link"
   <g:if test="${callback}">data-callback="${callback}"</g:if>
   <g:if test="${display}">data-display="${display}"</g:if>
   <g:if test="${returnUrl}">data-redirect_uri="${returnUrl}"</g:if>
   <g:if test="${disabled}">disabled="disabled"</g:if>
   href="#">
    ${raw(body)}
</a>