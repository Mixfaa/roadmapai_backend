package ua.torchers.roadmapai.roadmap.scaffold.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class TestDto(
    override val questions: List<QuestionDto>
) : Test {

    data class QuestionDto(
        override val text: String,
        override val options: List<String>,
        override val correctOption: Int
    ) : Test.Question

    companion object {
        @JsonIgnore
        const val JSON_SCHEMA: String = """
            Test json schema:
            {
                "questions" // array of Question objects
            }
            
            Question json schema:
            {
                "text" // string with question text
                "options" // array of available options
                "correctOption" // int, correct option index in array
            }
        """
    }
}