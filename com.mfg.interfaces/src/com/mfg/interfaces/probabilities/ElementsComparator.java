package com.mfg.interfaces.probabilities;
import java.util.Comparator;


/**
 * a comparator to sort some lists.
 * @author gardero
 *
 */
public class ElementsComparator implements Comparator<IElement> {

	private int ratioIndex;
	private int scale;
	
	public ElementsComparator(int aRatioIndex, int aScale) {
		super();
		ratioIndex = aRatioIndex;
		this.scale = aScale;
	}

	@Override
	public int compare(IElement e1, IElement e2) {
		return (int)Math.signum(
				e1.getRatio(ratioIndex,scale)
				-e2.getRatio(ratioIndex,scale));
	}

	public int getRatioIndex() {
		return ratioIndex;
	}
	
	

}
