<g:if test="${!disabled}">
<script type="text/javascript" charset="utf-8">
    function FBGrailsSDK_addToPage() {
        FB.ui({
            'method':'pagetab'
        }<g:if test="${callBackJS}">, ${callBackJS}</g:if>);
        return false;
    }
</script>
</g:if>
<a <g:if test="${elementId}">id="${elementId}"</g:if> <g:if test="${elementClass}">class="${elementClass}"</g:if> href="#" onclick="<g:if test="${!disabled}">FBGrailsSDK_addToPage();</g:if><g:else>return false;</g:else>">${body}</a>