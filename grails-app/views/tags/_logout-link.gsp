<g:if test="${!disabled}"><r:require module="fb-sdk-logout-link" /></g:if>
<a <g:if test="${elementId}">id="${elementId}"</g:if>
    class="<g:if test="${elementClass}">${elementClass} </g:if>fb-sdk-logout-link"
    <g:if test="${nextUrl}">data-next_url="${nextUrl}"</g:if>
    <g:if test="${disabled}">disabled="disabled"</g:if>
    href="#">
    ${body}
</a>