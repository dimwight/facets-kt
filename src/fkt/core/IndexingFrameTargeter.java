package fkt.core;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import fkt.util.Debug;
public class IndexingFrameTargeter extends TargeterCore{
  final private Map<String,STargeter>titleTargeters=new HashMap();
	private STargeter indexing,indexed;
	private SIndexing indexingTarget;
	private STarget indexedTarget;
	private String indexedTitle;
	public void retarget(STarget target){
		super.retarget(target);
		updateToTarget();
		if(indexing==null){
	  	indexing=indexingTarget.newTargeter();
	  	indexing.setNotifiable(this);
	  }
		if(titleTargeters.isEmpty()){
			int atThen=indexingTarget.index();
			for(int at=0;at<indexingTarget.indexables().length;at++){
				indexingTarget.setIndex(at);
				updateToTarget();
				indexed=((TargetCore)indexedTarget).newTargeter();
				indexed.setNotifiable(this); 
				indexed.retarget(indexedTarget);
				titleTargeters.put(indexedTitle,indexed);
			}
			indexingTarget.setIndex(atThen);
			updateToTarget();
		}
		indexing.retarget(indexingTarget);
		indexed=titleTargeters.get(indexedTitle);
		if(indexed==null)throw new IllegalStateException("Null indexed in "+this);
	  indexed.retarget(indexedTarget);
	}
	private void updateToTarget(){
		IndexingFrame frame=(IndexingFrame)target();
		indexingTarget=frame.indexing();
		indexedTarget=frame.indexedTarget();
		indexedTitle=indexedTarget.title();
	}
  public void retargetFacets(){
    super.retargetFacets();
    indexing.retargetFacets();
    for(STargeter t:titleTargeters.values())t.retargetFacets();
  }
	public STargeter[]titleElements(){
  	ArrayList<STargeter>list=new ArrayList<STargeter>(Arrays.asList(elements));
		list.add(indexing);
		for(STargeter t:titleTargeters.values())list.add(t);
		return list.toArray(new STargeter[]{});
	}
}