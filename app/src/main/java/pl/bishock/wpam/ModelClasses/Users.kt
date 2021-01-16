package pl.bishock.wpam.ModelClasses

class Users {
    var uid: String = ""
    var username: String = ""
    var profile: String = ""
    var background: String = ""
    var status: String = ""
    var search: String = ""
    var gender = ""
    var looking = ""
    var age = ""
    var marital = ""
    var height = ""
    var figure = ""
    var kids = ""
    var cigarettes = ""
    var about = ""


    constructor()
    constructor(
        id: String,
        username: String,
        profile: String,
        background: String,
        status: String,
        search: String,
        gender: String,
        looking: String,
        age: String,
        marital: String,
        height: String,
        kids: String,
        cigarettes: String,
        about: String
    ) {
        this.uid = id
        this.username = username
        this.profile = profile
        this.background = background
        this.status = status
        this.search = search
        this.gender = gender
        this.looking = looking
        this.age = age
        this.marital = marital
        this.height = height
        this.kids = kids
        this.cigarettes = cigarettes
        this.about = about

    }

}