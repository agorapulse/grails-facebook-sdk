class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(action:"welcome", controller:"website")
		"500"(view:"/error")
	}
}
