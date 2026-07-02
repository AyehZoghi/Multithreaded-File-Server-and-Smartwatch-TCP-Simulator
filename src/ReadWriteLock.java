public class ReadWriteLock {
    private int readers = 0;
    private boolean locked = false;

    public int getReaders() {
        return readers;
    }
    
    public void setReaders(int readers) {
        this.readers = readers;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean isLocked() {
        return locked;
    }
}