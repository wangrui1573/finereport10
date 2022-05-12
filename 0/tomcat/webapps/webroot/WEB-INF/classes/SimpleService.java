public class SimpleService
{
    public String getGreeting(String name)
    {
        return "你好 " + name;
    }
    public int getPrice()
    {
        return new java.util.Random().nextInt(1000);
    }
}