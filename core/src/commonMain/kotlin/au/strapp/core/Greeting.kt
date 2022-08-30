package au.strapp.core

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}