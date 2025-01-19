public class Person {
    private int name;
    private int age;

    public Person() {
    }

    public Person(int name, int age) {
        this.name = name;
        this.age = age;
    }

    private void eat() {
        System.out.println("먹기");
    }

    private void sleep() {
        System.out.println("잠자기");
    }
}
