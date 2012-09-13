<g:if test="${!disabled}">
<script type="text/javascript" charset="utf-8">
    function FBGrailsSDK_publish() {
        FB.ui({
            'method':'feed'
            <g:if test="${display}">, 'display':'${display}'</g:if>
            <g:if test="${caption}">, 'caption':'${caption.encodeAsJavaScript()}'</g:if>
            <g:if test="${description}">, 'description':'${description.encodeAsJavaScript()}'</g:if>
            <g:if test="${link}">, 'link':'${link}'</g:if>
            <g:if test="${name}">, 'name':'${name.encodeAsJavaScript()}'</g:if>
            <g:if test="${picture}">, 'picture':'${picture}'</g:if>
            <g:if test="${source}">, 'source':'${source}'</g:if>
        });
        return false;
    }
</script>
</g:if>
<a <g:if test="${elementId}">id="${elementId}"</g:if> <g:if test="${elementClass}">class="${elementClass}"</g:if> href="#" onclick="<g:if test="${!disabled}">FBGrailsSDK_publish();</g:if><g:else>return false;</g:else>">${body}</a>