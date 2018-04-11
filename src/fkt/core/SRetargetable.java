package fkt.core;
/**
Application element targetable on a {@link STarget}. 
<p>{@link SRetargetable} captures two distinct types of retargeting: 
<ul>
	<li>of a targeter by its parent in the targeter tree
	<li>of a facet by the targeter or target to which it is attached 
</ul>  
 */
public interface SRetargetable{
  /**
 Set the target if changed, adjust to latest state.  
 <p>The NotifyingImpact allows for refining the retargeting response. 
 */
	void retarget(STarget target);
}
