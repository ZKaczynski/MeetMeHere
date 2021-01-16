package pl.bishock.wpam.ModelClasses

import java.io.Serializable

class Place :Serializable {

    var title: String = ""
    var description: String = ""
    var  spotList: List<Spot> = ArrayList<Spot>()
    var id: String = ""



    constructor()

    constructor(title: String, description: String, spotList: List<Spot>,id:String) {
        this.title = title
        this.description = description
        this.spotList = spotList
        this.id = id
    }


}