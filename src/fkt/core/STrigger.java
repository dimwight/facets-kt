package fkt.core;
import fkt.util.Debug;
/**
{@link STarget} representing an application process. 
<p>{@link STrigger} represents an application process  
  to be exposed to user view and control in the surface; additional 
  mechanism must be defined in a 
  {@link fkt.core.STrigger.Coupler}. 
 */
final public class STrigger extends TargetCore{
	/**
	Connects a {@link STrigger} to the application. 
	<p>A {@link Coupler} supplies application-specific mechanism and policy
	for a {@link STrigger}.
	 */
	public static abstract class Coupler extends TargetCoupler{
		/**
		Called by <code>Trigger.fire</code>. 
		@param t the calling {@link STrigger}
		 */
		public abstract void fired(STrigger t);
	}
	/**
	The <code>Trigger.Coupler</code> passed to the constructor. 
	 */
	public final Coupler coupler;
	/**
	Unique constructor. 
	@param title passed to superclass
	@param coupler supplies application-specific mechanism
	 */
	public STrigger(String title,Coupler coupler){
		super(title);
		if(coupler==null)throw new IllegalArgumentException("Null coupler in "+Debug.info(this));
		this.coupler=coupler;
	}
	/**
	Initiates the application process represented. 
	<p>Calls {@link Coupler#fired(STrigger)}. 
	 */
  public void fire(){
  	coupler.fired(this);
  }
	@Override
	public Object getState(){
		return "Stateless!";
	}
	@Override
	public void updateState(Object update){
		fire();
	}
}