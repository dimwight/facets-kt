package fkt.java;
import fkt.java.util.Debug;
import fkt.java.util.Tracer;
/**
Core implementation of key interfaces. 
<p>{@link NotifyingCore} is the base class of both the {@link STarget} and
{@link STargeter} class hierarchies. 
<p>Declared <code>public</code> for documentation purposes only; client code should 
use the concrete subclass hierarchies. 
 */
abstract class NotifyingCore extends Tracer implements Notifying{
	private final String title;
	public NotifyingCore(String title){
		super();
		this.title=title;
	}
	public String title(){
		return title;
	}
	transient Notifiable notifiable;
	private static int identities;
	private final int identity=identities++;
	@Override
  final public Notifiable notifiable(){
  	if(notifiable==null)throw new IllegalStateException("No notifiable in "+Debug.info(this));
  	else return notifiable;
  }
	@Override
	public void notify(Object notice){
		if(Debug.trace)Debug.traceEvent("Notified in "+this+" with "+notice+": notifiable="+notifiable);
		if(notifiable==null)return;
		if(!blockNotification())notifiable.notify(notice);
		else if(Debug.trace)Debug.traceEvent("Notification blocked in "+this);
	}
	@Override
  final public void notifyParent(){
    if(notifiable==null)return;
    notifiable.notify(Debug.info(this));
  }
  /**
  Enables notification to be restricted to this member of the tree. 
  <p>Checked by {@link #notify(Object)}; default returns <code>false</code>.
   */
  protected boolean blockNotification(){return false;}
	@Override
	public final void setNotifiable(Notifiable n){
		this.notifiable=n;
	}
	@Override
  public String toString(){return Debug.info(this);}
}
