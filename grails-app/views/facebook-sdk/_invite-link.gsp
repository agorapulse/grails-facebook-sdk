<%@ page import="grails.converters.JSON" %>
<g:if test="${!disabled}">
<script type="text/javascript" charset="utf-8">
    function FBGrailsSDK_invite() {
        FB.ui({
            method: 'apprequests',
            message: '${message.encodeAsJavaScript()}'
            <g:if test="${data}">, data: '${data}'</g:if>
            <g:if test="${display}">, 'display':'${display}'</g:if>
            <g:if test="${excludeIds}">, exclude_ids: '${excludeIds as JSON}'</g:if>
            <g:if test="${filters}">, filters: ${filters as JSON}</g:if>
            <g:if test="${maxRecipients}">, max_recipients: ${maxRecipients}</g:if>
            <g:if test="${title}">, title: '${title.encodeAsJavaScript()}'</g:if>
            <g:if test="${to}">, to: '${to}'</g:if>
        }<g:if test="${callBackJS}">, ${callBackJS}</g:if>);
        return false;
    }
</script>
</g:if>
<a <g:if test="${elementId}">id="${elementId}"</g:if> <g:if test="${elementClass}">class="${elementClass}"</g:if> href="#" onclick="<g:if test="${!disabled}">FBGrailsSDK_invite();</g:if><g:else>return false;</g:else>">${body}</a>