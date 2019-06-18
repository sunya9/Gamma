package net.unsweets.gamma.domain.usecases

import kotlinx.coroutines.*

abstract class AsyncUseCase<out Type, in Params> where Type : Any {

    abstract suspend fun run(params: Params): Type

    fun execute(onResult: (Type) -> Unit, params: Params) {
        val job = GlobalScope.async(Dispatchers.Default, CoroutineStart.DEFAULT) { run(params) }
        GlobalScope.launch(Dispatchers.Main, CoroutineStart.DEFAULT) { onResult.invoke(job.await()) }
    }
}
