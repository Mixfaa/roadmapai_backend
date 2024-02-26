package ua.torchers.roadmapai.account

class UsernameTakenException(username:String) : Exception("Username $username is already in use")