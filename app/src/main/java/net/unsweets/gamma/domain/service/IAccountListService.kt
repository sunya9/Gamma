package net.unsweets.gamma.domain.service

interface IAccountListService {
    fun getAccountList(): List<String>
    fun setAccountList(ids: List<String>?)
}