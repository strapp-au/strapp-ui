package so.strapp.strappui

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}