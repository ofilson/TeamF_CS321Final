/**
 * This class represents a BBTreeObject that is to be stored in a BTreeNode
 * It stores the long binary value and frequency of a given DNA sequence
 * @author MichaelKinsy, , , ,
 *
 */
public class BTreeObject implements Comparable<BTreeObject> {
	private int freq;
	private long data;
	
	public BTreeObject(long d, int freq) {
		this.freq = freq;
		data = d;
	}
	
	public BTreeObject(long d) {
		freq = 1;
		data = d;
	}
	
	public void increaseFreq() {
		freq++;
	}
	
	public long getData() {
		return data;
	}
	
	public void setData(long data) {
		this.data = data;
	}
	
	public int compareTo(BTreeObject Tobj) {
		if (data < Tobj.data)
			return -1;
		if (data > Tobj.data)
			return 1;
		else
			return 0;
	}
	
	public int getFreq() {
		return freq;
	}
	
	public String toString() {
		return "Key: " + data + "Frequency: " + freq;
	}
}
