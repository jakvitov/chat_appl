package DataStructures;
//A basic Pair generic data structure to hold pairs of values
public class Pair<T, U> {
    private T first;
    private U second;
    public Pair (T first_value, U second_value){
        this.first = first_value;
        this.second = second_value;
    }
    public T getFirst(){
        return this.first;
    }
    public U getSecond(){
        return this.second;
    }
}
