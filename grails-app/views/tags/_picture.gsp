<g:if test="${linkEnabled}"><a href="${protocol}://www.facebook.com/profile.php?id=${facebookId}"></g:if>
<img <g:if test="${elementId}">id="${elementId}"</g:if> <g:if test="${elementClass}">class="${elementClass}"</g:if> src="${protocol}://graph.facebook.com/${facebookId}/picture?${queryString}"<g:if test="${height}"> height="${height}"</g:if><g:if test="${width}"> width="${width}"</g:if> />
<g:if test="${linkEnabled}"></a></g:if>
