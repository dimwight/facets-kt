package fkt.java.util;

/**
Utility superclass that can issue trace messages.
 */
public abstract class Tracer implements Identified{
	public static int ids;
	public static class TracerTopped extends Tracer{
		private final String top;
		public TracerTopped(String top){
			super(top);
			this.top=top;
		}
		@Override
		protected void doTraceMsg(String msg){
			if(doTrace())super.doTraceMsg(msg);
		}
		protected boolean doTrace(){
			return true;
		}
	}
	private final String top;
	private Integer id=++ids;
	@Override
	public Object identity(){
		return id;
	}
	public static Tracer newTopped(final String top,final boolean live){
		return new TracerTopped(top);
	}
	public Tracer(String top){
		this.top=top;
	}
	public Tracer(){
		top=null;
	}
	final public void trace(String msg){
		doTraceMsg(msg);
	}
	final public void trace(String msg,Object o){
		if(o instanceof Object[])doTraceMsg(msg+newArrayText((Object[])o));		
		else doTraceMsg(msg+Debug.info(o));		
	}
	protected void doTraceMsg(String msg){
		Util.printOut((top!=null?(top+" #"+id):Debug.info(this))+" "+msg);
	}
	private String newArrayText(Object[]array){
		if(false)return Util.arrayPrintString(array);
		String lines=new String("[\n");
		for(Object o:array)
			lines+="  "+(true?Debug.info(o):o.toString())+"\n";
		lines+=("]");
		return lines;
	}
}