<%@ page import="grails.converters.JSON" %>
<g:if test="${includeScript}">
    <script type="text/javascript">
        ${customSelector ?: '$'}(function() {
            ${customSelector ?: '$'}('a.fb-sdk-invite-link').click(function() {
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
        });
    </script>
</g:if>
<a <g:if test="${elementId}">id="${elementId}"</g:if>
    class="<g:if test="${elementClass}">${elementClass} </g:if>fb-sdk-invite-link fb-sdk-link"
    data-message="${message.encodeAsHTML()}"
    <g:if test="${callback}">data-callback="${callback}"</g:if>
    <g:if test="${data}">data-data="${data}"</g:if>
    <g:if test="${display}">data-display="${display}"</g:if>
    <g:if test="${excludeIds}">data-exclude_ids="${(excludeIds as JSON).toString().replace('"',"'")}"</g:if>
    <g:if test="${filters}">data-filters="${(filters as JSON).toString().replace('"',"'")}"</g:if>
    <g:if test="${maxRecipients}">data-max_recipients="${maxRecipients}"</g:if>
    <g:if test="${title}">data-title="${title.encodeAsHTML()}"</g:if>
    <g:if test="${to}">data-to="${to}"</g:if>
    <g:if test="${disabled}">disabled="disabled"</g:if>
    href="#">
    ${raw(body)}
</a>