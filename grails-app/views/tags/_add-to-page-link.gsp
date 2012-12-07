<g:if test="${!disabled}"><r:require module="fb-sdk-add-to-page-link" /></g:if>
<a <g:if test="${elementId}">id="${elementId}"</g:if>
   class="<g:if test="${elementClass}">${elementClass} </g:if>fb-sdk-add-to-page-link"
   <g:if test="${callback}">data-callback="${callback}"</g:if>
   <g:if test="${display}">data-display="${display}"</g:if>
   <g:if test="${returnUrl}">data-redirect_uri="${returnUrl}"</g:if>
   <g:if test="${disabled}">disabled="disabled"</g:if>
   href="#">
    ${body}
</a>