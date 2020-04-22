import java.util.NoSuchElementException;


public class Cache<T>{
	private int size,size2;
	private IUSingleLinkedList<T> list =null, list2 = null;
	private int nh1,nr1,nh2,nr2;
	/**
	 * @Constructor
	 */
	public Cache(int size) {
		this.setSize(size);
		nh1 = 0;
		nr1 = 0;
		nh2 = 0; nr2 = 0;
		this.list = new IUSingleLinkedList<T>();
	}
	
	public Cache(int size,int size2) throws Exception {
		this.setSize(size);
		if(size2<=size) {
			System.err.println("L2 cache size must be greater than or equal to L1 size.");
			throw new Exception();
		}
		this.setSize2(size2);
		nh1 = 0; nr1 = 0; nh2 = 0; nr2 = 0;
		this.list = new IUSingleLinkedList<T>();
		this.list2 = new IUSingleLinkedList<T>();
	}
	
	/**
	 * @param Object
	 * @return Object
	 */
	public T getObject(T Object) {
		T result = this.list.remove(Object);
		this.nr1++;
		if(result != null) { //if it hits
			this.nh1++;
			this.list.addToFront(result);
			this.list2.remove(Object);
			this.list2.addToFront(Object);
			return result;
		}else if(this.list2 != null) {
			result = this.list2.remove(Object);
			this.nr2++;
			if(result != null) {
				this.nh2++;
				this.list2.addToFront(Object);
				if(this.list.size() == size) {
					this.list.removeLast();
				}
				this.list.addToFront(Object);
			}else {
				this.addObject(Object);
				result = Object;
			}
		}else { 
			if(this.list.size() == size) {
				this.list.removeLast();
			}
			this.list.addToFront(Object);
			result = Object;
		}
		return result;
	}
	
	/**
	 * 
	 * @param Object
	 */
	public void removeObject(T Object) {
		if(this.list.isEmpty() && this.list2.isEmpty()) {
			throw new NoSuchElementException();
		}
		this.list.remove(Object);
		if(list2 != null) {
			this.list2.remove(Object);
		}
	}
	
	/**
	 * 
	 * @param object
	 */
	public void addObject(T object) {
		if(this.list.size() == size) {
			this.list.removeLast();
		}
		this.list.addToFront(object);
		if(this.list2!= null) {
			if(this.list2.size() == size2) {
				this.list2.removeLast();
			}
			this.list2.addToFront(object);
		}
	}
	
	/**
	 * 
	 */
	public void clearCache() {
		this.list.head = this.list.tail = null;
		if(this.list2!= null) {
			this.list2.head = this.list2.tail = null;
		}
	}
	
	public String ratios() {
		String result = "";
		double ghr = (((double) this.nh1) + ((double) this.nh2))/((double)this.nr1);
		double l1r = (((double) this.nh1))/((double)this.nr1);
		double l2r = (((double) this.nh2))/((double)this.nr2);
		result += "First Level Cache with " + this.size + " entries has been created.\n";
		if(this.list2 != null) {
			result += "Second Level Cache with " + this.size2 + " entries has been created.\n";
		}
		result+= ".\nThe number of global references: " +this.nr1 +"\nThe number of global hits: " + (this.nh1+this.nh2);
		result+= "\nGlobal hit ratio\t\t\t:" + ghr;
		
		result+= "\nThe number of 1st-level references: " +this.nr1 +"\nThe number of 1st-level hits: " + this.nh1;
		result+= "\n1st-level hit ratio\t\t\t:" + l1r;
		
		if(this.list2!= null) {
			result+= "\nThe number of 2nd-level references: " +this.nr2 +"\nThe number of 2nd-level hits: " + this.nh2;
			result+= "\n2nd-level hit ratio\t\t\t:" + l2r;
		}
		return result;
	}
	

	/**
	 * @param size the size to set
	 */
	private void setSize(int size) {
		this.size = size;
	}

	/**
	 * @param size2 the size2 to set
	 */
	private void setSize2(int size2) {
		this.size2 = size2;
	}



	/**
	 * CS 221: The Node class represents a node in a linked list. Node holds a
	 * single element with generic type set, references next node in list. Previous
	 * added for double linked List that references previous node in list.
	 * 
	 * @author MichaelKinsy
	 * @version 1.0 CS221 Summer 2019
	 * @param <T>
	 */
	@SuppressWarnings("hiding")
	private class Node<T>{
		// Instance Variables
		private T element;
		private Node<T> next;
		/**
		 * Constructor for node that takes element as parameter, sets nodes element to
		 * passed element and sets next and previous nodes to null.
		 * 
		 * @param element
		 */
		private Node(T element) {
			this.setElement(element);
			setNext(null);
		}

		/**
		 * @return the element
		 */
		private T getElement() {
			return element;
		}

		/**
		 * @param element the element to set
		 */
		private void setElement(T element) {
			this.element = element;
		}


		/**
		 * @return the next
		 */
		private Node<T> getNext() {
			return next;
		}

		/**
		 * @param next the next to set
		 */
		private void setNext(Node<T> next) {
			this.next = next;
		}
	}
	/**
	 * Modified Single Linked List.
	 * Iterator and ListIterator is unsupported
	 * @author MichaelKinsy
	 *
	 * @param <T>
	 */
	@SuppressWarnings("hiding")
	private class IUSingleLinkedList<T> {
		private Node<T> head, tail;
		private int size;
		private int modCount;
		
		/**Constructor*/
		private IUSingleLinkedList() {
			head = tail = null;
			size = 0;
			modCount = 0;
		}
		
		
		private void addToFront(T element) {
			Node<T> newNode = new Node<T>(element);
			newNode.setNext(head);
			head = newNode;
			if(isEmpty()) {
				tail = newNode;
			}
			size++;
			modCount++;
		}

		
		private T removeLast() {
			if(isEmpty()) {
				throw new NoSuchElementException();
			}
			T retVal = tail.getElement();
			if(size > 1) {
				remove(indexOf(tail.getElement()));
			}else {
				tail =head = null;
				modCount++;
				size--;
			}
			
			return retVal;
		}

		
		private T remove(T element) {
			if (isEmpty()) {
				return null;
			}
			
			boolean found = false;
			Node<T> previous = null;
			Node<T> current = head;
			
			while (current != null && !found) {
				if (element.equals(current.getElement())) {
					found = true;
				} else {
					previous = current;
					current = current.getNext();
				}
			}
			
			if (!found) {
				return null;
			}
			
			if (size() == 1) { //only node
				head = tail = null;
			} else if (current == head) { //first node
				head = current.getNext();
			} else if (current == tail) { //last node
				tail = previous;
				tail.setNext(null);
			} else { //somewhere in the middle
				previous.setNext(current.getNext());
			}
			
			size--;
			modCount++;
			
			return current.getElement();
		}

		
		private T remove(int index) {
			if(index<0 || index >= size) {
				throw new IndexOutOfBoundsException();
			}
			if(isEmpty()) {
				throw new NoSuchElementException();
			}
			T retVal = null;
			if(index == 0) {
				retVal = head.getElement();
				head = head.getNext();
				if(head == null) {
					tail = null;
				}
			}else {
				Node<T> current = head;
				for(int i = 0; i < index-1; i++) {
					current = current.getNext();
				}
				retVal = current.getNext().getElement();
				current.setNext(current.getNext().getNext());
				if(index == size-1) {
					tail = current;
				}
			}
			size--;
			modCount++;
			
			return retVal;
		}

		
		private int indexOf(T element) {
			int index = 0;
			Node<T> current = head;
			while(current != null && !current.getElement().equals(element)) {
				current = current.getNext();
				index++;
			}
			if(current == null) {
				index = -1;
			}
			return index;
		}


		private boolean isEmpty() {
			return (size == 0);
		}

		
		private int size() {
			return size;
		}
		
	}
}

