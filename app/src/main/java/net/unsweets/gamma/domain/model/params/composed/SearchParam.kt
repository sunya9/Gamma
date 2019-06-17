package net.unsweets.gamma.domain.model.params.composed

import net.unsweets.gamma.domain.model.params.composed.BaseComposeParam

data class SearchParam(
    val q: String
): BaseComposeParam() {

}
