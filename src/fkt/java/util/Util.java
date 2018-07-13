package fkt.java.util;

public final class Util{
	private static int DIGITS_SF=3,DECIMALS_FX=2;
	public static void printOut(String s){
	  String text=s==null?"null":s.toString();
		System.out.println(text);
	}
	public static void printOut(String msg,Object o){
	  printOut(msg+o);
	}
	public static String arrayPrintString(Object[]toPrint){
		String msg=toPrint==null?"null":Debug.toStringWithHeader(toPrint);
		return msg;
	}
	public static double sf(double val){
		return Util.sigFigs(val,DIGITS_SF);
	}
	static String sfs(double val){
		String sf=String.valueOf(sf(val)),
			sfs=sf.replaceAll("(\\d{"+DIGITS_SF+",})\\.0(\\D?)","$1$2").replaceAll("\\.0\\z","");
		return false?("["+sf+">"+sfs+"]"):sfs;
	}
	public static String fxs(double val){
		return "0."+(DECIMALS_FX==1?"0"
				:DECIMALS_FX==2?"00":"000");
	}
	private static String shortName(String className){
		int semiColon=className.lastIndexOf(';'),stop=semiColon>0?semiColon
				:className.length();
		return false?className:className.substring(className.lastIndexOf('.')+1,
				stop);
	}
	private static double sigFigs(double val,int digits){
		if(digits<0)throw new IllegalArgumentException("Digits <1="+digits);
		else if(Double.isInfinite(val))throw new IllegalArgumentException("Infinite value");
		else if(digits==0||val==0||val!=val)return val;
		double ceiling=Math.pow(10,digits),floor=ceiling/10,signum=Math.signum(val),sf=Math.abs(val);
		if(sf<1E-3)return 0;
		boolean shiftUp=sf<floor;
		double factor=shiftUp?10:0.1;
		int shifted=0;
		for(;sf>ceiling||sf<floor;shifted+=shiftUp?1:-1)sf*=factor;
		double exp=Math.pow(10,shifted);
		if(false)printOut("Doubles.sigFigs: shiftUp="+shiftUp+" shifted="+shifted);
		sf=Math.rint(sf)/exp;
		if(true||!shiftUp)return sf*signum;
		double trim=sf;
		if(trim!=sf)printOut("Doubles.sigFigs: val="+sf+" trim="+trim);
		return trim*signum;
	}
	public static boolean arraysEqual(Object[]now,Object[]then){
		boolean equal=false;
		if(then!=null&&then.length==now.length){
			equal=true;
			for(int i=0;i<now.length;i++){
				boolean equals=now[i].equals(then[i]);
				if(false&&!equals)System.out.println("longEquals: equal="+equal
						+ " "+now[i]+">"+then[i]);
				equal&=equals;
			}
		}
		return equal;
	}
}
