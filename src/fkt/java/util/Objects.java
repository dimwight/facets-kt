package fkt.java.util;
import java.util.ArrayList;
/**
Utility methods for arrays. 
 */
public final class Objects{
  private final static boolean debug=false;
	public static String toString(Object[]items,String spacer){
	  if(items==null)return "null";
	  else if(items.length==0)return "";
	  ArrayList list=new ArrayList();
		boolean trim=false&&!spacer.equals("\n");
		int at=0;
	  for(Object item:items)list.add(
				(item==null?"null":trim?item.toString().trim():item)
				+(++at==items.length?"":spacer)
			);
	  return list.toString();
	}
	public static String toString(Object[]array){
		return toString(array,",");
	}
	public static String toLines(Object[]array){
	  if(array==null)return "null";
	  ArrayList list=new ArrayList();
	  for(int i=0;i<array.length;i++)
	    list.add((array[i]==null?"null"
	    		:true?Debug.info(array[i]):array[i].toString())
	    	+(i<array.length-1?"\n":""));
	  String lines=list.toString();
		return false?lines:lines.replaceAll("\n"," ");
	}
}
