<g:if test="${includeScript}">
    <script type="text/javascript">
        ${customSelector ?: '$'}(function() {
            ${customSelector ?: '$'}('a.fb-sdk-publish-link').click(function() {
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
        });
    </script>
</g:if>
<a <g:if test="${elementId}">id="${elementId}"</g:if>
    class="<g:if test="${elementClass}">${elementClass} </g:if>fb-sdk-publish-link fb-sdk-link"
    <g:if test="${callback}">data-callback="${callback}"</g:if>
    <g:if test="${caption}">data-caption="${caption.encodeAsHTML()}"</g:if>
    <g:if test="${display}">data-display="${display}"</g:if>
    <g:if test="${description}">data-description="${description.encodeAsHTML()}"</g:if>
    <g:if test="${link}">data-link="${link}"</g:if>
    <g:if test="${name}">data-name="${name.encodeAsHTML()}"</g:if>
    <g:if test="${picture}">data-picture="${picture}"</g:if>
    <g:if test="${source}">data-source="${source}"</g:if>
    <g:if test="${disabled}">disabled="disabled"</g:if>
    href="#">
    ${raw(body)}
</a>