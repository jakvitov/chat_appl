package DataStructures;
//A basic Pair generic data structure to hold pairs of values
//Pair has two special reserved values -1111 and -1110 to signalize errors etc.
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
