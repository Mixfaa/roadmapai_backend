package ua.torchers.roadmapai.roadmap.scaffold.model


interface Test {
    val questions: List<Question>

    interface Question {
        val text: String
        val options: List<String>
        val correctOption: Int
    }
}