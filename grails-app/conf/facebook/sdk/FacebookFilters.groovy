package facebook.sdk

class FacebookFilters {

    def facebookAppService

    def filters = {
        all(controller:'*', action:'*') {
            before = {
                request.facebook = [
                    appId:facebookAppService.appId,
                    appPermissions:facebookAppService.appPermissions,
                    userId:facebookAppService.getUserId()
                ]
                request.facebook.authenticated = (request.facebook.userId?true:false)
                return true
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }
}
