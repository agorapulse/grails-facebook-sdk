<%@ page import="grails.converters.JSON" %>
<g:if test="${!disabled}"><r:require module="fb-sdk-invite-link" /></g:if>
<a <g:if test="${elementId}">id="${elementId}"</g:if>
    class="<g:if test="${elementClass}">${elementClass} </g:if>fb-sdk-invite-link"
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
    ${body}
</a>