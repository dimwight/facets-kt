package fkt.java;
import fkt.java.util.Debug;

public class TargetCore extends NotifyingCore implements STarget{
	public static int targets;
	private STarget[]elements;
	private boolean elementsSet=false;
	private boolean live=true;
	
	public TargetCore(String title){
		this(title,new STarget[]{});
	}
	
	public TargetCore(String title,STarget...elements){
		super(title);
		if(title==null||title.equals(""))
			throw new IllegalArgumentException("Null or empty title in "+Debug.info(this));
		if(false)trace(".TargetCore: elements=",elements.length);
		if(elements.length>0)setElements(elements);
		targets++;
		if(Debug.trace)Debug.traceEvent("Created "+Debug.info(this));
	}
	
	final protected void setElements(STarget[]elements){
		if(false&&elementsSet)throw new RuntimeException("Immutable elements in "
				+Debug.info(this));
		this.elements=elements;
		elementsSet=true;
		for(int i=0;i<elements.length;i++)if(elements[i]==null)
				throw new IllegalArgumentException("Null element "+i+" in "+Debug.info(this));
	}
	
	final public STarget[]elements(){
		if(false)trace(".elements: elementsSet=",elementsSet);
		if(!elementsSet){
			STarget[]lazy=lazyElements();
			if(false)trace(".TargetCore: lazy=",lazy.length);
			setElements(lazy);
		}
		for(STarget e:elements)
			if(!((TargetCore)e).notifiesTargeter())e.setNotifiable(this);
		return this.elements;
	}
	
	protected STarget[]lazyElements(){
		return new STarget[]{};
	}
	
	public STargeter newTargeter(){
		return new TargeterCore();
	}
	public boolean isLive(){
		Notifiable n=notifiable();
		boolean notifiesTarget=n!=null&&n instanceof STarget;
		return !notifiesTarget?live:live&&((STarget)n).isLive();
	}
	public void setLive(boolean live){
		this.live=live;
	}
	
	protected boolean notifiesTargeter(){
		return elements!=null;
	}
	@Override
	public Object getState(){
		throw new RuntimeException("Not implemented in "+this);
	}
	@Override
	public void updateState(Object update){
		throw new RuntimeException("Not implemented in "+this);
	}
	public String toString(){
		return Debug.info(this);
	}
}
