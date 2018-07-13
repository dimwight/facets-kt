package fkt.java.util;
/**
Utilities for use during development. 
 */
public final class Debug{
	/**
		Returns basic Debug.information about an object's type and identity. 
		<p>This will be some combination of
	<ul>
		<li>the non-trivial simple class name
		<li>{@link Titled#title()} if available
		</ul>
			 */
	public static String info(Object o){
		if(o==null)return "null";
		else if(o instanceof Boolean)return ((Boolean)o).toString();
		else if(o instanceof Number)return String.valueOf(o);
		else if(o instanceof String){
			String text=(String)o;
			int length=text.length();
			return text.substring(0,Math.min(length,60))
					+(true?"":(": "+("length="+length)));
		}
		Class classe=o.getClass();
		String name=classe.getSimpleName(),id="",
				title=false?"o instanceof Titled":classe.getName();
		if(o instanceof Identified)id=" #"+((Identified)o).identity();
		if(o instanceof Titled)title=" "+((Titled)o).title();
		return name+id+title;
	}
	/**
	Returns an array of <code>Debug.info</code>s. 
	 */
	public static String arrayInfo(Object[]array){
		return "arrayInfo";
	}
	public static boolean trace=false;
	public static void traceEvent(String string){
		Util.printOut(">>"+string);
	}
	public static String toStringWithHeader(Object[]array){
		return info(array)+" ["+array.length+"] " +Objects.toLines(array);
	}
}
