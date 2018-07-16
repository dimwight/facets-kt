package fkt.facets.core
abstract class NotifyingCore(val type:String,val title_:String) : Notifying{
lateinit var notifiable_:Notifiable
override fun title():String{
return this.title_
}
override fun setNotifiable(n:Notifiable){
this.notifiable_=n
}
override fun notifiable():Notifiable{
return this.notifiable_
}
override fun notifyParent(){
this.notifiable_.notify(this)
}
abstract override fun elements():Array<out Notifying> 
override fun notify(notice:Any){
if(this.notifiable_!=null)this.notifiable_.notify(this.title())
}
}

