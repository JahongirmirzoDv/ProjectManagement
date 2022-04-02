package uz.perfectalgorithm.projects.tezkor.data.sources.local_models.calendar

class EventInfo {
    var eventtitles: Array<String> = arrayOf()
    var isallday = false
    var id = 0
    var accountname: String? = null
    var noofdayevent = 0
    var starttime: Long = 0
    var endtime: Long = 0
    var nextnode: EventInfo? = null
    var title: String? = null
    var timezone: String? = null
    var eventcolor = 0

    constructor(eventtitles: Array<String>) {
        this.eventtitles = eventtitles
    }

    constructor() {}
}