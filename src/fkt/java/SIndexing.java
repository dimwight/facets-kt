package fkt.java;
import java.util.ArrayList;
import java.util.List;

import fkt.facets.TargetCoupler;
import fkt.java.util.Debug;

/**
{@link STarget} representing an index into a list of items. 
<p>{@link SIndexing} represents a list of items to be exposed 
  in the surface, together with an index 
  into that list; application-specific mechanism and policy can be defined in 
	a {@link fkt.java.SIndexing.Coupler}.
 */
final public class SIndexing extends TargetCore{
	/**
	Connects an {@link SIndexing} to the application. 
	<p>A {@link Coupler} supplies client-specific mechanism and 
	  policy for an {@link SIndexing}. 
	 */
	public static abstract class Coupler extends TargetCoupler {
		/**
		Called whenever an index changes. 
		 */
		public void indexSet(SIndexing i){}
		/**
		Returns dynamic indexables. 
		<p>Called by {@link SIndexing#indexables()} 
		@return a non-empty array
		 */
		protected abstract Object[]getIndexables(SIndexing i);
		/**
		Return strings to represent the indexables.  
		 */
		protected String[]getFacetSelectables(SIndexing i){
			List<String>selectables=new ArrayList();
			String top=i.title();
			int at=0;
			for(Object each:i.indexables())selectables.add(top+String.valueOf(at++));
			return selectables.toArray(new String[]{});
		}
	}
	/**
	The <code>Indexing.Coupler</code> passed to the java constructor.
	 */
	public final Coupler coupler;	
	private Integer index;
	private transient Object[]indexings;  
	/**
	Unique constructor. 
	 @param title passed to superclass
	 @param coupler supplies application-specific mechanism and policy
	 */
	public SIndexing(String title,Coupler coupler){
		super(title);this.coupler=coupler;
	}
	/**
	The first index into the <code>indexables</code>. 
		 */
	public int index(){
		return index;
	}
  /**
	Sets a single index into the <code>indexables</code>. 
		 */
	public void setIndex(int index){
		boolean first=this.index==null;
		this.index=index;
	  if(!first)coupler.indexSet(this);
	}
	/**
	The items exposed to indexing. 
	<p>These will either have been set during construction or be read 
	dynamically from the coupler. 
		 */
	public Object[]indexables(){
		Object[]indexables=coupler.getIndexables(this);
	  if(indexables==null||indexables.length==0)throw new IllegalStateException(
	  		"Null or empty indexables in "+this);
	  else return indexables;
	}
	public String[]facetSelectables(){
		return coupler.getFacetSelectables(this);
	}
	/**
	The item denoted by the current <code>index</code>. 
		 */
	public Object indexed(){
	  if(index==null)throw new IllegalStateException("No index in "+Debug.info(this));
	  else return indexables()[index];
	}
	public void setIndexed(Object indexable){
		Object[]indexables=indexables();
		for(int i=0;i<indexables.length;i++)
			if(indexables[i]==indexable) {
				setIndex(i);
				break;
			}
	}
	@Override
	public Object getState(){
		return index();
	}
	@Override
	public void updateState(Object update){
		setIndex((Integer)update);
	}
}