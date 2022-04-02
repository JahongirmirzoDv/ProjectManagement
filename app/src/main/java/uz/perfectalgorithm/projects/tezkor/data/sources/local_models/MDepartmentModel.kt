package uz.perfectalgorithm.projects.tezkor.data.sources.local_models


class MDepartmentModel(private val title: String, private val id: Int) {
    override fun toString(): String {
        return title
    }

    fun getId(): Int {
        return id
    }
}
