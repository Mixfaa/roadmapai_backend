package ua.torchers.roadmapai.account

import ua.torchers.roadmapai.account.model.Account

class UsernameTakenException(username:String) : Exception("Username $username is already in use")

class AccessException(account: Account, obj: Any) : Exception("${account.username} can`t access $obj")
