package uz.perfectalgorithm.projects.tezkor.utils.adding

import timber.log.Timber
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.dashboard.DepartmentStructureBelow
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.AllWorkersShort
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure.StructureResponse
import uz.perfectalgorithm.projects.tezkor.data.sources.remote.response.workers.structure_short.DepartmentStructureShort


interface FragmentPageSelectedListener {
    fun onFragmentSelected()
}

interface StructureSelectListener {
    fun onDepartmentClick(department: DepartmentStructureShort)
    fun onWorkerClick(worker: AllWorkersShort)
}

interface StructureSelectDashboardListener {
    fun onDepartmentClick(department: DepartmentStructureBelow)
    fun onWorkerClick(worker: AllWorkersResponse.DataItem)
}

fun List<StructureResponse.DataItem?>.getCheckedPersons(
    checkedList: Set<String>,
    workersList: MutableSet<AllWorkersResponse.DataItem?>
) {
    val workers = mutableSetOf<AllWorkersResponse.DataItem>()
    forEach { department ->
        department?.getDepartmentWorkers()?.let { workers.addAll(it) }
    }
    workersList.clear()
    workersList.addAll(workers.filter { checkedList.contains(it.id.toString()) })
}

fun List<DepartmentStructureShort?>.getCheckedPersonsShort(
    checkedList: Set<String>,
    workersList: MutableSet<AllWorkersShort?>
) {
    val workers = mutableSetOf<AllWorkersShort>()
    forEach { department ->
        department?.getDepartmentWorkers()?.let { workers.addAll(it) }
    }
    workersList.clear()
    workersList.addAll(workers.filter { checkedList.contains(it.id.toString()) })
}

fun List<DepartmentStructureBelow?>.getCheckedPersonsDashboard(
    checkedList: Set<String>,
    workersList: MutableSet<AllWorkersResponse.DataItem?>
) {
    val workers = mutableSetOf<AllWorkersResponse.DataItem>()
    forEach { department ->
        department?.getDepartmentWorkers()?.let { workers.addAll(it) }
    }
    workersList.clear()
    workersList.addAll(workers.filter { checkedList.contains(it.id.toString()) })
}

// TODO: 8/26/21 very bad for performance, optimize it or use without notifyDataSetChanged() (only once)
fun StructureResponse.DataItem.getDepartmentWorkers(): List<AllWorkersResponse.DataItem> {
    val list = mutableListOf<AllWorkersResponse.DataItem>()
    positions?.forEach { position ->
        position?.staffs?.forEach { worker ->
            worker?.let { list.add(it) }
        }
    }
    children?.forEach { department ->
        list.addAll(department.getDepartmentWorkers())
    }
    return list.distinct()
}

fun DepartmentStructureShort.getDepartmentWorkers(): List<AllWorkersShort> {
    try {
        val list = mutableListOf<AllWorkersShort>()
        positions?.forEach { position ->
            position?.staffs?.forEach { worker ->
                worker?.let { list.add(it) }
            }
        }
        children?.forEach { department ->
            list.addAll(department.getDepartmentWorkers())
        }
        return list.distinct()
    }catch (e:Exception){
        Timber.tag("error_department_").d("error_department_${e.message}")
        return emptyList()
    }

}

// TODO: 8/26/21 very bad for performance, optimize it or use without notifyDataSetChanged() (only once)
fun DepartmentStructureBelow.getDepartmentWorkers(): List<AllWorkersResponse.DataItem> {
    val list = mutableListOf<AllWorkersResponse.DataItem>()
    positions?.forEach { position ->
        position.staffs?.forEach { worker ->
            worker.let { list.add(it) }
        }
    }
    children?.forEach { department ->
        list.addAll(department.getDepartmentWorkers())
    }
    return list.distinct()
}