<g:if test="${!disabled}"><r:require module="fb-sdk-login-link" /></g:if>
<a <g:if test="${elementId}">id="${elementId}"</g:if>
   class="<g:if test="${elementClass}">${elementClass} </g:if>fb-sdk-login-link"
   data-permissions="${appPermissions instanceof List ? appPermissions.join(',') : appPermissions}"
   <g:if test="${callback}">data-callback="${callback}"</g:if>
   <g:if test="${cancelUrl}">data-cancel_url="${cancelUrl}"</g:if>
   <g:if test="${returnUrl}">data-return_url="${returnUrl}"</g:if>
   <g:if test="${disabled}">disabled="disabled"</g:if>
   href="#">
    ${body}
</a>