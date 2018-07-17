package fkt.java;
import fkt.java.util.Debug;
/**
{@link TargetCore} that enables editing of the contents of an {@link SIndexing}. 
 */
public class IndexingFrame extends TargetCore{
	private final SIndexing indexing;
	/**
	Unique constructor. 
	@param title passed to superclass 
	@param indexing supplies content for {{@link #newIndexedTargets(Object)}
	 */
	public IndexingFrame(String title,SIndexing indexing){
		super(title);
		if(indexing==null)throw new IllegalArgumentException(
				"Null indexing in "+Debug.info(this));
		this.indexing=indexing;
		indexing.setNotifiable(this);
	}
	/**
	<p>Returns the {@link STarget} created in {@link #newIndexedTargets(Object)}. 
	 */
	final public TTarget indexedTarget(){
		Object indexed=indexing.indexed();
		return indexed instanceof TTarget?(TTarget)indexed:newIndexedTargets(indexed);
	}
	/**
	Create targets exposing non-{STarget) indexed. 
	Default is invalid stub.
	@param indexed the currently indexed member of {@link #indexing}
	 */
	protected TTarget newIndexedTargets(Object indexed){
		throw new RuntimeException("Not implemented in "+this);
	}
	/** 
	Sets an {@link SIndexing} containing content to be exposed.
	 */
	/**
	The indexing passed to the constructor. 
	 */
	public final SIndexing indexing(){
	  return indexing;
	}
  /**
	Overrides superclass method. 
	<p>Returns an {@link IndexingFrameTargeter}. 
	 */
	final public STargeter newTargeter(){
		return new IndexingFrameTargeter();
	}
	/**
	Re-implementation returning <code>true</code>. 
	 */
	final protected boolean notifiesTargeter(){
		return true;
	}
}