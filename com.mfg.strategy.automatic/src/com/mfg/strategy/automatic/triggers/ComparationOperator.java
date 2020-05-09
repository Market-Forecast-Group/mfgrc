package com.mfg.strategy.automatic.triggers;

/**
 * compares two values.
 * <p> Possible values are:
 * <ul>
 * <li> {@code GE} for Greater or Equal.
 * <li> {@code G} for Greater.
 * <li> {@code LE} for Less or Equal.
 * <li> {@code L} for Less.
 * </ul>
 * @author gardero
 *
 */
public enum ComparationOperator {
	GE(true,true,"Greater or Equal"){
		@Override
		public ComparationOperator getInverse(){
			return LE;
		}
	},
	G(false,true,"Greater"){
		@Override
		public ComparationOperator getInverse(){
			return L;
		}
	},
	LE(true,false,"Less or Equal"){
		@Override
		public ComparationOperator getInverse(){
			return GE;
		}
	},
	L(false,false,"Less"){
		@Override
		public ComparationOperator getInverse(){
			return G;
		}
	};
	
	private ComparationOperator(boolean aEqual, boolean aGreater, String aT) {
		this.equal = aEqual;
		this.greater = aGreater;
		this.t = aT;
	}
	protected boolean equal;
	protected boolean greater;
	
	public boolean isEqual() {
		return equal;
	}

	public boolean isGreater() {
		return greater;
	}

	public boolean compare(double a, double b){
		return (equal && (a==b)) ||
		   (greater && a>b)  ||
		   (!greater && a<b);
	}
	
	@SuppressWarnings("static-method")//It´s overwritten in this class.
	public ComparationOperator getInverse(){
		return null;
	}
	
	protected String t;
	@Override
	public String toString() {
		return t;
	}
	public String toHtmlString(){
		String op = (greater)?"&gt;":"&lt;";
		if (equal)
			op+="=";
		else
			op+="";
		return op;
	}
}
