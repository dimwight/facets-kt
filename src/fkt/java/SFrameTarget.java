package fkt.java;
import fkt.java.util.Debug;

/**
Extends {@link TargetCore} by framing 
application content exposable with simple targets. 
 */
public class SFrameTarget extends TargetCore{
	/**Immutable content framed by the {@link SFrameTarget}.*/
	final public Object framed;
	/**
 	Core constructor. 
  <p>Note that this passes no child target elements to the superclass; 
    elements can only be set by subclassing and  
  <ul>
    <li>in named subclasses where the elements are known at 
      construction, calling {@link #setElements(STarget[])} 
      from the constructor 
    <li>in other cases (in practice the large majority), overriding 
      {@link #lazyElements()} from {@link TargetCore} 
  </ul>
  <p>This limitation ensures that the effective type of 
    a {@link SFrameTarget} with child elements can be distinguished 
    by reference to the compiled type. Care must therefore be 
    taken in client code not vary the effective type of the 
    elements created by a subclass. 
  @param title passed to the superclass 
  @param toFrame must not be <code>null</code>
	 */
	public SFrameTarget(String title,Object toFrame){
	  super(title);
		if(toFrame==null)throw new IllegalArgumentException(
				"Null framed in "+Debug.info(this));
		else framed=toFrame;
	}
	@Override
	protected final boolean notifiesTargeter(){
		return true;
	}
}