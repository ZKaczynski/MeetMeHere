package pl.bishock.wpam.ModelClasses

import java.io.Serializable

class Spot :Serializable{
    var title: String = ""
    var description: String = ""
    var longitude: String = ""
    var latitude: String = ""

    constructor()

    constructor(title: String, description: String, longitude: String, latitude: String) {
        this.title = title
        this.description = description
        this.longitude = longitude
        this.latitude = latitude
    }


}