package fkt.java;
import java.lang.reflect.Array;

/**
Superfical target. 
<p>{@link STarget} represents an application element to one or more 
facets; it is also a {@link Notifying} to enable it to form part of the 
	notification tree. 
 */
public interface STarget extends Notifying,FacetsTarget{
	/**
	Dynamically-defined children. 
<p>Return {@link STarget} child elements of this {@link STarget}.
		@return a non-<code>null</code> {@link STarget}[]
	 */
	STarget[]elements();
	/**
	Indicates whether the {@link STarget} should
	be exposed by a surface facet as open to control eg 'enabled'.   
	<p>Returns <code>true</code> only if both the following conditions are met:</p>
	<ul>
	<li>the {@link STarget} itself is 'live' as constructed or  
	set by <code>setLive</code></li> 
	<li>any {@link STarget} monitor also returns <code>isLive</code> as
	<code>true</code></li>   
	</ul>   
	 
		 */
	boolean isLive();
	/**
	Sets the internal state used by <code>isLive</code>. 
  
	 */
  void setLive(boolean live);
	static STarget[]newTargets(Object[]src){
	  Class type=STarget.class;
		STarget[]array=(STarget[])Array.newInstance(type,src.length);
	  /*
	  if(false&&src.length>0&&!type.isAssignableFrom(src[0].getClass()))
	    throw new IllegalArgumentException(Debug.info(src[0])+" should be STarget: \n"+type);
	  else if(false)for(Object s:src)
	    if(!type.isAssignableFrom(s.getClass()))
	      throw new IllegalArgumentException(Debug.info(s)+" should be STarget: \n"+type);
	  */
	  System.arraycopy(src,0,array,0,array.length);
	  return array;
	}
	Object getState();
	void updateState(Object update);
}
