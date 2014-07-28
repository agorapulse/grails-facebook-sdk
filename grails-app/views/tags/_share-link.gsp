<g:if test="${includeScript}">
    <script type="text/javascript">
        ${customSelector ?: '$'}(function() {
            ${customSelector ?: '$'}('a.fb-sdk-share-link').click(function() {
                var link = $(this);
                var options = {
                    method: 'share',
                    href: link.data('href')
                };
                if (link.data('display') != undefined) options['display'] = link.data('display');
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
   class="<g:if test="${elementClass}">${elementClass} </g:if>fb-sdk-share-link fb-sdk-link"
   <g:if test="${callback}">data-callback="${callback}"</g:if>
   data-href="${href}"
   <g:if test="${display}">data-display="${display}"</g:if>
   <g:if test="${disabled}">disabled="disabled"</g:if>
   href="#">
    ${raw(body)}
</a>