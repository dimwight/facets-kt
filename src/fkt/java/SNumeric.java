package fkt.java;
import fkt.java.util.Debug;
import fkt.java.util.NumberPolicy;
import fkt.java.util.Util;
/**
{@link STarget} representing a numeric value. 
<p>{@link SNumeric} represents a numeric value to 
  be exposed to user view and control in the surface; application-specific 
  mechanism and policy can be defined in a {@link fkt.java.SNumeric.Coupler}.
 */
final public class SNumeric extends TargetCore{
	public static boolean doRangeChecks=false;
	/**
	Connects a {@link SNumeric} to the application. 
	<p>A {@link Coupler} supplies policy and/or client-specific 
	  mechanism to a {@link SNumeric}. 
	 */
	public static class Coupler extends TargetCoupler {
		/**
		Returns the policy to be used by a {@link fkt.java.SNumeric}
		constructed with this {@link Coupler}.
			 */
		public NumberPolicy policy(SNumeric n){
			return new NumberPolicy(0,0);
		}
		/**
		Defines client-specific mechanism for a {@link fkt.java.SNumeric}.
		<p>This method is called whenever <code>setValue</code> is called on <code>n</code>.
		 */
		public void valueSet(SNumeric n){
			throw new RuntimeException("Not implemented in "+Debug.info(this));
		}
	}
	private final Coupler coupler;
	private final NumberPolicy policy;
	private double value=Double.NaN;
	/**
	Unique constructor. 
	@param title passed to superclass
	@param value the initial value
	@param coupler must supply application-specific mechanism and policy
	 */
	public SNumeric(String title,double value,Coupler coupler){
	  super(title);this.coupler=coupler;
		policy=coupler.policy(this);
		if(policy==null)throw new IllegalStateException("No policy in "+Debug.info(this));
	  setValue(value);
	}	
	public double value(){
		if(value!=value)throw new IllegalStateException("Not a number in "+Debug.info(this));
		else if(doRangeChecks){
			double min=policy.min(),max=policy.max();
			if(value<min||value>max)throw new IllegalStateException
				("Value "+value+" should be >="+min+" and <="+max+" in "+Debug.info(this));			
		}
		return value;
	}
	/**
	Sets the nearest valid value to <code>value</code>.
	 <p>Validity will be as defined by <code>validValue</code> in  
	 the {@link NumberPolicy} returned as <code>policy</code>. 
	 Subsequently calls <code>valueSet</code> in the {@link SNumeric.Coupler} with which the
	 {@link SNumeric} was constructed.
	 */
	public void setValue(double value){
  	boolean first=this.value!=this.value;
  	this.value=policy().validValue(this.value,value);
  	if(!first)coupler.valueSet(this);
  }
	/**
	Returns the current number policy. 
	<p>The policy is that returned by the {@link fkt.java.SNumeric.Coupler}
	with which the {@link SNumeric} was constructed.
	 */
	public NumberPolicy policy(){return policy;}
	public String toString(){
		NumberPolicy p=policy();
		return super.toString()+" value="+Util.sf(value)+" policy="+p;
	}
	@Override
	public Object getState(){
		return value();
	}
	@Override
	public void updateState(Object update){
		setValue((double)update);
	}
}
