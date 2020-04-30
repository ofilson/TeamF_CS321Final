/**
 * This class represents a BTreeObject that is to be stored in a BTreeNode
 * It stores the long binary value and frequency of a given DNA sequence
 * @author MichaelKinsy, , , ,
 *
 */
public class BTreeObject implements Comparable<BTreeObject> {
	/**@InstanceVariables for BTreeObject*/
	private int freq; //The frequency of which the value of the object occurs
    private long value; //The binary long value of the DNA sequence for the object

    /**
     * @constructor for BTreeObject with a given frequency
     * @param value
     * @param freq
     */
    public BTreeObject(long value, int freq){
        this.value = value;
        this.freq = freq;
    }
    /**
     * @constructor for BTreeObject without a given frequency
     * @param value
     */
    public BTreeObject(long value){
        this.value = value;
        this.freq = 1;
    }
    /**
     * Increments the BTreeObjects frequency
     */
    public void increaseFreq(){
        this.freq++;
    }
    /**
     * @return the BTreeObjects frequency
     */
    public int getFreq()
    {
        return freq;
    }
    /**
     * @return the BTreeObjects value
     */
    public long getValue()
    {
        return value;
    }
    
    @Override
    /**
     * Method to compare BTreeObject to a given BTreeObject
     */
    public int compareTo(BTreeObject object)
    {
        if (this.value < object.value)
            return -1;
        else if (this.value > object.value)
            return 1;
        else
            return 0;
    }
    /**
     * @return String of BTreeObject
     */
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("Key: "); result.append(value); result.append(" freq: "); result.append(freq);
        return result.toString();
    }
}
