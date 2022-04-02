package uz.perfectalgorithm.projects.tezkor.data.sources.local_models

data class StructureList(
    var id: Int,
    var title: String,
    var listEmployees: List<Employee>
)

data class Employee(
    var id: Int,
    var name: String
)
