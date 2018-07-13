package fkt.java;
import fkt.facets.util.Titled;
/**
Undertakes to respond to notification. 
<p>{@link Notifiable} is basically an observer, with a slightly stronger
	contract in that it promises a 'best efforts' response to notification. 
 */
public interface Notifiable extends Titled{
  /**
Respond to the Notice passed. 
<p>When this method is called, the {@link Notifiable} should
respond based on 
<ul>
<li>its own nature and state 
<li>the Notice passed. 
</ul>  
 */
  void notify(Object notice);
}
