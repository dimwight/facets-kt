package fkt.core;
import java.util.ArrayList;
import java.util.List;
import fkt.util.Debug;
/**
Implements {@link STargeter}. 
<p>{@link TargeterCore} is a public implementation of {@link STargeter} 
  to provide for extension in other packages; instances are generally 
  created by an implementation of {@link fkt.core.TargetCore#newTargeter()}. 
*/
public class TargeterCore extends NotifyingCore implements STargeter{
	protected transient STargeter[]elements;
	private transient ArrayList<SFacet>facets=new ArrayList();
	private transient STarget target;
	private String targetTitle;  
	public TargeterCore(){
		super("Untargeted");
	  if(Debug.trace)Debug.traceEvent("Created " +//"targeter " +targeters+" "+
				this);
	}
	public void retarget(STarget target){
	  if(target==null)throw new IllegalArgumentException(
	  		"Null target in "+Debug.info(this));
	  else this.target=target;
	  String checkTitle=target.title();
		if(targetTitle!=null&&!checkTitle.equals(targetTitle))throw new IllegalStateException(
				"Bad target title="+targetTitle);
		if(((TargetCore)target).notifiesTargeter())target.setNotifiable(this);
	  if(false)trace(": retargeting on "+Debug.info(target));
		STarget[]targets=target.elements();
		if(targets==null)throw new IllegalStateException("Null targets in "+Debug.info(this));
		if(elements==null){
    	ArrayList<STargeter>list=new ArrayList<STargeter>();
			for(STarget t:targets){
				STargeter add=((TargetCore)t).newTargeter();
				add.setNotifiable(this);
				list.add(add);
			}
	  	elements=list.toArray(new STargeter[]{});
	  	if(false)trace(".TargeterCore: elements=",elements.length);
	  }
		boolean anyLengths=true;
		if(!anyLengths&&targets.length!=elements.length)
			throw new IllegalStateException("Targets="+targets.length+" differ from elements="+elements.length);
		else for(int i=0;i<(anyLengths?targets:elements).length;i++)
			elements[i].retarget(targets[i]);
	}
	final public void attachFacet(SFacet facet){
		if(facet==null)throw new IllegalArgumentException("Null facet in "+Debug.info(this));
		if(!facets.contains(facet)){
			facet.retarget(target);
			facets.add(facet);
		}
		if(Debug.trace)Debug.traceEvent("Attached facet "+Debug.info(facet)+" to "+Debug.info(this));
	}
	public void retargetFacets(){
		for(STargeter e:elements)e.retargetFacets();
		for(SFacet f:facets){
			f.retarget(target);
			if(Debug.trace)Debug.traceEvent("Retargeted facet " +Debug.info(f)+" in "+this);
		}
	}
	final public STargeter[]elements(){
	  if(elements==null)throw new IllegalStateException("No elements in "+Debug.info(this));
	  return elements;
	}
	final public STarget target(){
	  if(target==null)throw new IllegalStateException("No target in "+Debug.info(this));
	  return target;
	}
	final public String title(){
		return target==null?super.title():target.title();
	}
	final public String toString(){
		String targetInfo=target==null?"":Debug.info(target);
		return Debug.info(this)+(true?"":" ["+targetInfo+"]");
	}
	public STargeter[]titleElements(){
		if(elements==null)throw new IllegalStateException("Null elements in "+this);
		else return elements;
	}
}
