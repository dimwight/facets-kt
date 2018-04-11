package fkt.core;

/**
{@link STarget} representing a Boolean value. 
<p>{@link SToggling} represents a Boolean value to 
  be exposed to user view and control in the surface; application-specific 
  mechanism can be defined in a {@link fkt.core.SToggling.Coupler}. 
 */
final public class SToggling extends TargetCore{
	/**
	Connects a {@link SToggling} to the application. 
	<p>A {@link Coupler} supplies application-specific mechanism 
	for a {@link SToggling}.
	 */
	public static class Coupler extends TargetCoupler{
		/**
		Called by the toggling whenever its state is set. 
	 */
		public void stateSet(SToggling t){}
	}
	public final Coupler coupler;
	private boolean state;
	/**
	Unique constructor. 
	@param title passed to superclass
	@param state initial state of the toggling
	@param coupler can supply application-specific mechanism
	 */
	public SToggling(String title,boolean state,Coupler coupler){
		super(title);
		this.state=state;
		this.coupler=coupler;
	}
	/**
	The Boolean state of the toggling. 
	 <p>The value returned will that set using 
	 <code>setState</code> or during construction.    
		 */
	public boolean isSet(){
		return state;
	}
	/**
	Sets the Boolean state. 
	<p> Subsequently calls {@link fkt.core.SToggling.Coupler#stateSet(SToggling)}.
	*/
	public void set(boolean state){
		this.state=state;
		coupler.stateSet(this);
	}
	@Override
	public void updateState(Object update){
		set((boolean)update);
	}
	@Override
	public Object getState(){
		return isSet();
	}
	public String toString(){
		return super.toString()+(false?"":" "+state);
	}
}