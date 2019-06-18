package net.unsweets.gamma.domain.usecases

abstract class UseCase<out Type, in Params> where Type : Any {
    abstract fun run(params: Params): Type
}
