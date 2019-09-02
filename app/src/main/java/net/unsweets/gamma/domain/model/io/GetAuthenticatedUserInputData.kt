package net.unsweets.gamma.domain.model.io

import androidx.lifecycle.MutableLiveData
import net.unsweets.gamma.domain.entity.Token

data class GetAuthenticatedUserInputData(
    val liveData: MutableLiveData<Token>
)
