public class Event {
    private int numPeople;              // Num of people assigned to an event
    private int src;                    // Source floor
    private int dest;                   // Destination floor
    private boolean delete;             // Boolean to track if the event needs to be deleted
    private boolean srcReached;         // Boolean to track if the source floor has been reached

    /**
     * Default constructor of the Event class
     *
     * @param numPeople number of people assigned to an elevator
     * @param src       starting floor of event
     * @param dest      destination floor of event
     */
    public Event(int numPeople, int src, int dest) {
        this.numPeople = numPeople;
        this.src = src;
        this.dest = dest;
        this.delete = false;
        this.srcReached = false;
    }

    /**
     * Getter for numPeople
     *
     * @return
     */
    public int getNumPeople() {
        return numPeople;
    }

    /**
     * Getter for source floor
     *
     * @return
     */
    public synchronized int getSrc() {
        return src;
    }

    /**
     * Getter for destination floor
     *
     * @return
     */
    public synchronized int getDest() {
        return dest;
    }

    /**
     * Getter for the boolean 'delete'
     *
     * @return
     */
    public synchronized boolean getDelete() {
        return delete;
    }

    /**
     * Setter for the boolean 'delete'
     *
     * @param delete
     */
    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    /**
     * Setter for the number of people in an elevator
     *
     * @param numPeople
     */
    public void setNumPeople(int numPeople) {
        this.numPeople = numPeople;
    }

    /**
     * Setter for if a source floor is reached
     *
     * @param srcReached
     */
    public void setSrcReached(boolean srcReached) {
        this.srcReached = srcReached;
    }

    /**
     * Getter for if a source floor is reached
     *
     * @return
     */
    public synchronized boolean getSrcReached() {
        return srcReached;
    }
}
