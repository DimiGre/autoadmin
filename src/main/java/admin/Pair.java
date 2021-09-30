package admin;

public class Pair<T extends Number, E extends Number> implements Comparable<Pair> {
    private T first;
    private E second;

    public Pair(T _first, E _second){
        first = _first;
        second = _second;
    }

    public T getFirst(){
        return first;
    }

    public E getSecond(){
        return second;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public void setSecond(E second) {
        this.second = second;
    }

    @Override
    public int compareTo(admin.Pair o) {
        return (o.second.intValue() - this.second.intValue());
    }
}
