public class Event
{
    private int numPeople;
    private int src;
    private int dest;
    private boolean delete;
    private boolean srcReached;

    public Event(int numPeople, int src, int dest)
    {
        this.numPeople = numPeople;
        this.src = src;
        this.dest = dest;
        this.delete = false;
        this.srcReached = false;
    }

    public int getNumPeople()
    {
        return numPeople;
    }

    public int getSrc()
    {
        return src;
    }

    public int getDest()
    {
        return dest;
    }

    public boolean getDelete()
    {
        return delete;
    }

    public void setDelete(boolean delete)
    {
        this.delete = delete;
    }

    public void setNumPeople(int numPeople)
    {
        this.numPeople = numPeople;
    }

    public void setSrcReached(boolean srcReached)
    {
        this.srcReached = srcReached;
    }

    public boolean getSrcReached()
    {
        return srcReached;
    }
}
