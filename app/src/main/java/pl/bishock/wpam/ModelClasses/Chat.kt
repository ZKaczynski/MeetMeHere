package pl.bishock.wpam.ModelClasses

class Chat {

    var sender: String = ""
    var message: String = ""
    var receiver: String = ""

    @field:JvmField
    var isSeen: Boolean = false
    var messageId: String = ""
    var url: String = ""

    constructor()

    constructor(sender: String, message: String, receiver: String, isSeen: Boolean, messageId: String, url: String) {
        this.sender = sender
        this.message = message
        this.receiver = receiver
        this.isSeen = isSeen
        this.messageId = messageId
        this.url = url
    }

   /* fun setIsSeen(isSeen: String){
        this.isSeen = isSeen == "true"
    }*/


}


