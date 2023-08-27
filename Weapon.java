public class Weapon {
    private int x;
    private int y;
    private String name;
    Weapon(int x, int y, String name){
        this.x = x;
        this.y = y;
        this.name = name;
    }
    String getName(){
        return name;
    }

    public String toString(){
        return this.name;
    }

    public int getX(){
        return this.x;
    }
    public int getY(){
        return this.y;
    }
    public void move(int x, int y){
        this.x = x;
        this.y = y;
    }

}
