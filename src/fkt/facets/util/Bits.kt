package fkt.facets.util
fun traceThing(top:String,thing:Any?){
if(top.substring(0)=="^")return 
// Allow for callback eg to find and kill circular references
val callback={key:String,value:Any->
if(false)print(key)
if("|notifiable_|elements_|".contains(key))key else value
}
// Construct body
val tail=if(thing==null)"" else "JSON.stringify(thing, callback, 1)"
// Issue complete message
print(top+tail)
}

