package service;

public class MyService
{
    public String getGreeting(String name)
    {
        return "您好 " + name;
    }
    public void update(String data)
    {
        System.out.println("<" + data + ">已经更新");
    }
}