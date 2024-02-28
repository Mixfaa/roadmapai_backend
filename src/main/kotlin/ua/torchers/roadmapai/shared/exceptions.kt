package ua.torchers.roadmapai.shared

class NotFoundException(subject: String) : Exception("$subject: not found")